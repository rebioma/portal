package org.rebioma.server.services;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.server.services.OccurrenceDbImpl.OccurrenceFilter;
import org.rebioma.server.util.StringUtil;

/**
 * A simple helper class that parses a string filter into it's component parts
 * (column, operator, value). Provides methods for testing the filter operation.
 */
public abstract class QueryFilter {
	
	boolean disjunction;

  /**
   * Exception for invalid filters.
   */
  public static class InvalidFilter extends Exception {
    public InvalidFilter(String msg) {
      super(msg);
    }
  }

  public enum Operator {
    EQUAL("="), NOT_EQUAL("!="), NOT_START_WITH("!like"), NOT_CONTAIN("!like"), START_WITH(
        "like"), CONTAIN("like"), GREATER(">"), LESS("<"), GREATER_EQUAL(">="), LESS_EQUAL(
        "<="), IN("in"), NOT_IN("!in"), IS_EMPTY("empty"), IS_NOT_EMPTY(
        "!empty");
    String operator;

    Operator(String operator) {
      this.operator = operator;
    }

    public String toString() {
      return operator;
    }
  }

  /**
   * The {@link Logger} for this class.
   */
  private static final Logger log = Logger.getLogger(QueryFilter.class);

  /**
   * Creates {@link OccurrenceFilter} objects from each filter string and
   * returns them in a set.
   * 
   * @param filters the filter strings of the form "concept op value"
   * @return set of {@link OccurrenceFilter} objects
   */
  public static <Q extends QueryFilter> Set<Q> getFilters(Set<String> filters,
      Class<Q> filterClass) {
    Set<QueryFilter> set = new HashSet<QueryFilter>();
    if (filters == null) {
      return (Set<Q>) set;
    }
    QueryFilter f;
    Constructor<?> queryConstructor = filterClass.getConstructors()[0];
    if(queryConstructor.getParameterTypes().length != 1){
    	Constructor<?> queryConstructors[] = filterClass.getConstructors();
        for(Constructor<?> qc:queryConstructors){
        	if(qc.getParameterTypes().length ==1 && qc.getParameterTypes()[0]==String.class){
        		queryConstructor = qc;
        	}
        }
    }
    for (String filter : filters) {
      if (filter.equals("")) {
        continue;
      }
      try {
        f = (QueryFilter) queryConstructor.newInstance(filter);
        set.add(f);
      } catch (IllegalArgumentException e) {
        log.info(e);
        e.printStackTrace();
      } catch (SecurityException e) {
        log.info(e);
        e.printStackTrace();
      } catch (InstantiationException e) {
        log.info(e);
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        log.info(e);
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        log.info(e);
        e.printStackTrace();
      }
    }
    return (Set<Q>) set;
  }

  private static Operator getOperator(String op) {
    Operator operator;
    if (op.equals("=")) {
      operator = Operator.EQUAL;
    } else if (op.equals("!=")) {
      operator = Operator.NOT_EQUAL;
    } else if (op.equalsIgnoreCase("like")) {
      operator = Operator.CONTAIN;
    } else if (op.equalsIgnoreCase("!like")) {
      operator = Operator.NOT_CONTAIN;
    } else if (op.equalsIgnoreCase("sw")) {
      operator = Operator.START_WITH;
    } else if (op.equalsIgnoreCase("!sw")) {
      operator = Operator.NOT_START_WITH;
    } else if (op.equals(">")) {
      operator = Operator.GREATER;
    } else if (op.equals("<")) {
      operator = Operator.LESS;
    } else if (op.equals(">=")) {
      operator = Operator.GREATER_EQUAL;
    } else if (op.equalsIgnoreCase("in")) {
      operator = Operator.IN;
    } else if (op.equals("<=")) {
      operator = Operator.LESS_EQUAL;
    } else if (op.equals("empty")) {
      operator = Operator.IS_EMPTY;
    } else if (op.equals("!empty")) {
      operator = Operator.IS_NOT_EMPTY;
    } else {
      operator = null;
    }
    return operator;
  }

  String column;
  Operator operator;
  Object value;
  Class<?> filterForClass;

  /**
   * Constructs a new Filter from a string and throws an {@link InvalidFilter}
   * exception if the column or operator is unknown or if an error occurs while
   * processing the string filter.
   * 
   * Expects filter strings in the form:
   * 
   * "column op value"
   * 
   * where column is an {@link Occurrence} property name, op is either '=' or
   * 'like', and value is non-null.
   * 
   * @param filter the string filter
   * @throws InvalidFilter
   */
  QueryFilter(String filter, Class<?> filterForClass) throws InvalidFilter {
    try {
      String[] array = filter.trim().split(" ");
      column = getPropertyName(array[0].trim());
      if (column == null) {
        throw new InvalidFilter("Invalid column: " + column);
      }
      operator = getOperator(array[1].trim());
      if (operator == null) {
        throw new InvalidFilter("Invalid operator: " + array[1].trim());
      }
      String valueString = "";// = array[2];
      for (int i = 2; i < array.length; i++) {
        valueString += " " + array[i];
      }
      valueString = valueString.trim();

      if (column.equalsIgnoreCase("userReviewed")
          || StringUtil.isType(filterForClass, column, Boolean.class)
          || StringUtil.isType(filterForClass, column, boolean.class)) {
        value = Boolean.parseBoolean(valueString);
      } else if (StringUtil.isType(filterForClass, column, Integer.class)
          || StringUtil.isType(filterForClass, column, int.class)) {
    	  //issue 363
        value = isInteger(valueString)?Integer.parseInt(valueString):valueString;
      } else {
        value = valueString;
      }
    } catch (Exception e) {
      throw new InvalidFilter("Filter invalid: " + filter + " : " + e);
    }
  }

  QueryFilter(String column, Operator op, Object value, Class<?> filterClass)
      throws InvalidFilter {
    this.column = getPropertyName(column);
    filterForClass = filterClass;
    if (column == null) {
      throw new InvalidFilter("Invalid column: " + column);
    }
    operator = op;
    this.value = value;
  }

  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    QueryFilter f = (QueryFilter) obj;

    return f.column.equals(column) && f.operator.equals(operator)
        && f.value.equals(value);
  }

  public abstract String getPropertyName(String property);

  public int hashCode() {
    return toString().hashCode();
  }

  public String toString() {
    String value = this.value.toString();
    String op = this.operator.toString();
    String values[] = null;
    if (operator == Operator.CONTAIN || operator == Operator.NOT_CONTAIN) {
      value = "'%" + this.value + "%'";
    } else if (operator == Operator.START_WITH
        || operator == Operator.NOT_START_WITH) {
      value = "'" + this.value + "%'";
      op = "like";
    } else if (this.value instanceof String) {
      value = "'" + value + "'";
    }
    if (operator == Operator.NOT_CONTAIN || operator == Operator.NOT_START_WITH) {
      return "not(" + column + " like " + value + ")";
    } else if (operator == Operator.IN) {
      return column + " in (" + putInSingleQuoteForValue() + ")";
    } else if (operator == Operator.NOT_IN) {
      return "not(" + column + " in (" + putInSingleQuoteForValue() + "))";
    } else if (operator == Operator.IS_EMPTY) {
      return column + " is null or " + column + " = ''";
    } else if (operator == Operator.IS_NOT_EMPTY) {
      return column + " is not null and " + column + " != ''";
    }
    return column + " " + op + " " + value;
  }

  String[] getCollectionValues() {
    String values[] = value.toString().split(",");
    for (int i = 0; i < values.length; i++) {
      values[i] = values[i].trim();
    }
    return values;
  }

  Object[] getIntegerValues() {
	  String values[] = value.toString().split(",");
	  Object oValues[] = new Object[values.length];
	  for (int i = 0; i < values.length; i++) {
		  oValues[i] = Integer.valueOf(values[i].trim());
	  }
	  return oValues;
  }

	  
  Set<Integer> getIntCollectionValues() {
    String values[] = value.toString().split(",");
    Set<Integer> intValues = new HashSet<Integer>();
    for (String value : values) {
      try {
        intValues.add(Integer.parseInt(value.trim()));
      } catch (Exception e) {
        continue;
      }
    }
    return intValues;
  }

  Operator getOperator() {
    return operator;
  }

  Object getValue() {
    return value;
    // return (value instanceof String) ? ("'" + value + "'") : value;
  }

  void setCollectionValue(Collection<?> values) {
    value = values;
  }

  private String putInSingleQuoteForValue() {
    String val = value.toString().replaceFirst("\\[", "");
    val = val.replaceFirst("\\]", "");
    String values[] = val.split(",");
    StringBuilder sb = new StringBuilder();
    for (String v : values) {
      sb.append("'" + v.trim() + "',");
    }
    sb.delete(sb.length() - 1, sb.length());
    return sb.toString();
  }
  
  //ZO: issue 363: check if String is parseable to int, if not return the String
  private boolean isInteger(String string) {
	    try {
	        Integer.valueOf(string);
	        return true;
	    } catch (NumberFormatException e) {
	        return false;
	    }
	}

/**
 * @return the disjunction
 */
public boolean isDisjunction() {
	return disjunction;
}

/**
 * @param disjunction the disjunction to set
 */
public void setDisjunction(boolean disjunction) {
	this.disjunction = disjunction;
}

}

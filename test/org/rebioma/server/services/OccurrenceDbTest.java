package org.rebioma.server.services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.rebioma.client.Occurrence;
import org.rebioma.client.OccurrenceQuery;
import org.rebioma.client.OccurrenceQuery.ResultFilter;
import org.rebioma.server.util.HibernateUtil;

import com.google.inject.Inject;
import com.google.inject.testing.guiceberry.GuiceBerryEnv;
import com.google.inject.testing.guiceberry.NoOpTestScopeListener;
import com.google.inject.testing.guiceberry.TestScopeListener;
import com.google.inject.testing.guiceberry.junit3.GuiceBerryJunit3Env;
import com.google.inject.testing.guiceberry.junit3.GuiceBerryJunit3TestCase;

@GuiceBerryEnv("org.rebioma.server.services.OccurrenceDbTest$OccurrenceDbEnv")
public class OccurrenceDbTest extends GuiceBerryJunit3TestCase {
  public static final class OccurrenceDbEnv extends GuiceBerryJunit3Env {
    @Override
    protected Class<? extends TestScopeListener> getTestScopeListener() {
      return NoOpTestScopeListener.class;
    }
  }

  @Inject
  AscDataDb ascDataDb;
  private static Properties testProperties = null;
  private static Map<String, Method> fieldToGetMethodMap = new HashMap<String, Method>();
  private static Map<String, String[]> specialFixedValues = new HashMap<String, String[]>();
  private static Map<String, String> otherFixedValues = new HashMap<String, String>();
  private static List<Occurrence> allOccurrences;

  static {
    allOccurrences = getAllOccurrences();
    System.out.println(allOccurrences.size());
  }
  static {
    specialFixedValues.put("basisofrecord", new String[] { "FossilSpecimen",
        "HumanObservation", "LivingSpecimen", "MachineObservation",
        "MovingImage", "PreservedSpecimen", "SoundRecording", "StillImage",
        "OtherSpecimen" });
    specialFixedValues.put("sex", new String[] { "male", "female",
        "hermaphroditic" });
    otherFixedValues.put("basisofrecord", "Non-standardSpecimen");
    otherFixedValues.put("sex", "unknown");
  }

  static {
    Class<Occurrence> c = Occurrence.class;
    Method methods[] = c.getMethods();
    for (Method method : methods) {
      String fieldName = method.getName();
      int beginIndex = 0;
      if (fieldName.startsWith("get")) {
        beginIndex = 3;
      } else if (fieldName.startsWith("is")) {
        beginIndex = 2;
      }
      if (beginIndex != 0) {
        if (fieldName.equals("getClass")) {
          continue;
        }
        fieldName = fieldName.substring(beginIndex, fieldName.length());
        fieldName = fieldName.replaceAll("_", "");

        fieldToGetMethodMap.put(fieldName.toLowerCase(), method);
      }
    }
  }

  static {
    ResourceBundle rb = ResourceBundle.getBundle("OccurrenceTest");
    if (rb != null) {
      testProperties = new Properties();
      Enumeration<String> keys = rb.getKeys();

      while (keys.hasMoreElements()) {
        String key = keys.nextElement();
        testProperties.put(key, rb.getObject(key));
      }
    }
  }

  private static List<Occurrence> getAllOccurrences() {
    List<Occurrence> occurrences = null;
    SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    try {
      occurrences = session.createQuery("from Occurrence").list();
    } catch (Exception e) {
      tx.rollback();
      e.printStackTrace();
    }
    return occurrences;
  }

  @Inject
  OccurrenceDb occService;

  OccurrenceQuery query = null;
  Boolean isValidated = null;
  Boolean isVetted = null;
  Boolean isPublic = null;
  boolean isMineOnly = false;
  Integer userId = null;

  public void testAllInVaidatedQueries() throws Exception {
    Set<String> filters = new HashSet<String>();
    filters.add("validated = false");
    runTest(filters, ResultFilter.BOTH, true);
  }

  public void testAllOccurrencesQueries() throws Exception {
    runTest(null, ResultFilter.BOTH, true);

  }

  public void testAllVaidatedQueries() throws Exception {
    Set<String> filters = new HashSet<String>();
    filters.add("validated = true");
    runTest(filters, ResultFilter.BOTH, true);
  }

  public void testAllValidatedNotVetteddQueries() throws Exception {
    Set<String> filters = new HashSet<String>();
    filters.add("validated = true");
    filters.add("vetted = false");
    runTest(filters, ResultFilter.BOTH, true);
  }

  public void testAllVettedQueries() throws Exception {
    Set<String> filters = new HashSet<String>();
    filters.add("vetted = true");
    runTest(filters, ResultFilter.BOTH, true);
  }

  public void testMyInVaidatedQueries() throws Exception {
    Set<String> filters = new HashSet<String>();
    filters.add("validated = false");
    filters.add("owner = "
            + Integer.parseInt(testProperties.getProperty("userId")));
    runTest(filters, ResultFilter.BOTH, false);
    runTest(filters, ResultFilter.PUBLIC, false);
    runTest(filters, ResultFilter.PRIVATE, false);
  }

  public void testMyOccurrencesQueries() throws Exception {
    Set<String> filters = new HashSet<String>();
    filters.add("owner = "
            + Integer.parseInt(testProperties.getProperty("userId")));
    runTest(filters, ResultFilter.BOTH, false);
    runTest(filters, ResultFilter.PUBLIC, false);
    runTest(filters, ResultFilter.PRIVATE, false);
    runTest(filters, ResultFilter.BOTH, true);

  }

  public void testMyVaidatedQueries() throws Exception {
    Set<String> filters = new HashSet<String>();
    filters.add("validated = true");
    filters.add("owner = "
            + Integer.parseInt(testProperties.getProperty("userId")));
    runTest(filters, ResultFilter.BOTH, false);
    runTest(filters, ResultFilter.PUBLIC, false);
    runTest(filters, ResultFilter.PRIVATE, false);
  }

  public void testMyValidatedNotVetteddQueries() throws Exception {
    Set<String> filters = new HashSet<String>();
    filters.add("validated = true");
    filters.add("vetted = false");
    filters.add("owner = "
            + Integer.parseInt(testProperties.getProperty("userId")));
    runTest(filters, ResultFilter.BOTH, false);
    runTest(filters, ResultFilter.PUBLIC, false);
    runTest(filters, ResultFilter.PRIVATE, false);
  }

  public void testMyVettedQueries() throws Exception {
    Set<String> filters = new HashSet<String>();
    filters.add("vetted = true");
    filters.add("owner = "
            + Integer.parseInt(testProperties.getProperty("userId")));
    runTest(filters, ResultFilter.BOTH, false);
    runTest(filters, ResultFilter.PUBLIC, false);
    runTest(filters, ResultFilter.PRIVATE, false);
  }

  private void checkOccurrenceResult(Occurrence occurrence, String fieldName,
          String value[], String operator) {
    if (fieldName != null) {
      Object resultValue = getOccurrenceFieldValue(occurrence, fieldName);
      String searchValue = value[0].toLowerCase();
      if (!searchValue.equals("")) {
        String result = resultValue.toString().toLowerCase();
        String lowerCaseFieldName = fieldName.toLowerCase();
        if (specialFixedValues.containsKey(lowerCaseFieldName)) {
          this.checkSpecialField(otherFixedValues.get(lowerCaseFieldName),
                  searchValue, specialFixedValues.get(lowerCaseFieldName),
                  result, operator);
        } else if (operator.equals("<=")) {
          try {
            double numberResult = Double.parseDouble(result);
            double numberValue1 = Double.parseDouble(searchValue);
            double numberValue2 = Double.parseDouble(value[1]);
            assertTrue(numberResult >= numberValue1
                    && numberResult <= numberValue2);
          } catch (Exception e) {
            assertTrue(result.compareTo(searchValue) >= 0
                    && result.compareTo(value[1]) <= 0);
          }
        } else if (operator.equals("=")) {
          if (!result.equals(searchValue)) {
            System.out.println(fieldName + " - " + searchValue + "-" + result);
          }
          if (fieldName.equalsIgnoreCase("acceptedSpecies")) {
            assertTrue(result.equals(searchValue)
                    || result.contains(searchValue));
          } else {
            assertEquals(result, searchValue);
          }
        } else if (operator.equals("!=")) {
          if (result.equals(searchValue)) {
            System.out.println(fieldName + " - " + searchValue + "-" + result);
          }
          if (fieldName.equalsIgnoreCase("acceptedSpecies")) {
            assertTrue(!result.contains(searchValue)
                    || !result.equals(searchValue));
          } else {
            assertNotSame(result, searchValue);
          }
        } else if (operator.equals("like")) {
          if (!result.contains(searchValue)) {
            System.out.println(fieldName + " - " + searchValue + "-" + result);
          }
          assertTrue(result.contains(searchValue));
        } else if (operator.equals("!like")) {
          if (result.contains(searchValue)) {
            System.out.println(fieldName + " - " + searchValue + "-" + result);
          }
          assertTrue(!result.contains(searchValue));
        }

      }
    }
    if (isPublic != null) {
      assertTrue(occurrence.isPublic_()
              || occurrence.getOwner().equals(userId + ""));
    }
    if (isMineOnly) {
      assertEquals(occurrence.getOwner(), userId + "");
    }
    if (isValidated != null) {
      assertTrue(isValidated.equals(occurrence.isValidated()));
    }
    if (isVetted != null) {
      assertTrue(isVetted.equals(occurrence.isVetted()));
      if (isVetted.booleanValue()) {
        assertTrue(occurrence.isValidated());
      }
    }
  }

  /*
   * private void checkMultipleAdvanceSearchsResult(OccurrenceQuery query,
   * Set<String> baseFilters, ResultFilter resultFilter) throws Exception {
   * String fieldNames[] = getPropertyValues("searchField"); String fieldTypes[]
   * = getPropertyValues("searchType"); String validValues[] =
   * getPropertyValues("validSearchValue1"); int totalFields =
   * fieldNames.length; for (int i = 0; i < totalFields; i++) { String
   * fieldName1 = fieldNames[i]; String fieldType1 = fieldTypes[i]; String
   * validValues1[] = validValues[i].split("\\+"); String operators1[] =
   * getOperators(fieldName1); String operator1 = null; for (int j = i + 1; j <
   * totalFields; i++) { String oFields[] = new String[] { fieldName1,
   * fieldNames[j] }; String fieldType2 = fieldTypes[j]; if
   * (fieldType1.equals("string")) { for (String value : validValues1) { query =
   * getBaseQuery(baseFilters, resultFilter); operator1 =
   * getRandomOperator(fieldName1); query.addSearchFilter(fieldName1 + " " +
   * operator1 + " " + value); this.checkResult(query, new String[] { fieldName1
   * }, new String[][] { new String[] { value } }, new String[] { operator1 });
   * } } else { if (validValues1.length < 2) { continue; } query =
   * getBaseQuery(baseFilters, resultFilter); query.addSearchFilter(fieldName1 +
   * " >= " + validValues1[0]); query.addSearchFilter(fieldName1 + " <= " +
   * validValues1[1]); // this.checkResult(query, new String[] { fieldName }, //
   * new String[][] { validValue }, new String"<="); } } } }
   */

  private void checkResult(OccurrenceQuery query, String fieldNames[],
          String values[][], String operators[]) throws Exception {
    List<Occurrence> occurrences = occService.findByOccurrenceQuery(query,
            userId);
    int count = query.getCount();
    int manualCount = getCount(fieldNames, values, operators, query
            .getResultFilter());
    if (count != manualCount) {
      if (fieldNames != null) {
        for (String fieldName : fieldNames) {
          System.out.print(fieldName + ",");
        }
        System.out.print(" - [");

        for (String vs[] : values) {
          for (String c : vs) {
            System.out.print(c + ",");
          }
          System.out.print("], - ");
        }
        for (String operator : operators) {
          System.out.println(operator + ",");
        }
      }
      System.out.println(" - " + query.getResultFilter());
    }
    assertEquals(count, manualCount);
    query.setCountTotalResults(false);
    while (count > 0) {
      count -= occurrences.size();
      for (Occurrence occurrence : occurrences) {
        if (fieldNames != null) {
          for (int i = 0; i < fieldNames.length; i++) {
            String fieldName = fieldNames[i];
            String value[] = values[i];
            String operator = operators[i];
            checkOccurrenceResult(occurrence, fieldName, value, operator);
          }
        } else {
          checkOccurrenceResult(occurrence, null, null, null);
        }
      }
      query.setStart(query.getStart() + query.getLimit());
      occurrences = occService.findByOccurrenceQuery(query, userId);
    }
  }

  private void checkSingleAdvanceSearchResult(OccurrenceQuery query,
          Set<String> baseFilters, ResultFilter resultFilter) throws Exception {
    String fieldNames[] = getPropertyValues("searchField");
    String fieldTypes[] = getPropertyValues("searchType");
    String validValues[] = getPropertyValues("validSearchValue1");
    int totalFields = fieldNames.length;
    for (int i = 0; i < totalFields; i++) {
      String fieldName = fieldNames[i];
      String fieldType = fieldTypes[i];
      String validValue[] = validValues[i].split("\\+");
      if (fieldType.equals("string")) {
        for (String value : validValue) {
          for (String operator : getOperators(fieldName)) {
            query = getBaseQuery(baseFilters, resultFilter);
            query.addSearchFilter(fieldName + " " + operator + " " + value);
            this.checkResult(query, new String[] { fieldName },
                    new String[][] { new String[] { value } },
                    new String[] { operator });
          }
        }
      } else {
        if (validValue.length < 2) {
          continue;
        }
        query = getBaseQuery(baseFilters, resultFilter);
        query.addSearchFilter(fieldName + " >= " + validValue[0]);
        query.addSearchFilter(fieldName + " <= " + validValue[1]);
        this.checkResult(query, new String[] { fieldName },
                new String[][] { validValue }, new String[] { "<=" });
      }
    }
  }

  private void checkSpecialField(String otherValue, String searchValue,
          String[] availableValues, String resultValue, String operator) {
    if (searchValue.equalsIgnoreCase(otherValue)) {
      boolean isAvailableValue = false;
      for (String availableValue : availableValues) {
        if (availableValue.equalsIgnoreCase(resultValue)) {
          isAvailableValue = true;
          break;
        }
      }
      if (operator.equals("=")) {
        if (isAvailableValue) {
          System.out.println(searchValue + " - " + resultValue + " - "
                  + operator);
        }
        assertFalse(isAvailableValue);
      } else if (operator.equals("!=")) {
        if (!isAvailableValue) {
          System.out.println(searchValue + " - " + resultValue + " - "
                  + operator);
        }
        assertTrue(isAvailableValue);
      }
    } else {
      if (operator.equals("=")) {
        assertEquals(searchValue.toLowerCase(), resultValue.toLowerCase());
      } else if (operator.equals("!=")) {
        assertNotSame(searchValue.toLowerCase(), resultValue.toLowerCase());
      }
    }
  }

  private OccurrenceQuery getBaseQuery(Set<String> baseFilters,
          ResultFilter resultFilter) {
    Integer limit = Integer.parseInt(testProperties.getProperty("defaultLimit",
            "10"));
    OccurrenceQuery query = new OccurrenceQuery(0, limit);
    query.setResultFilter(resultFilter);
    query.setCountTotalResults(true);
    if (baseFilters != null) {
      query.setBaseFilters(baseFilters);
    }
    return query;
  }

  private int getCount(String fieldNames[], String values[][],
          String operators[], ResultFilter resultFilter) {
    int count = 0;

    for (Occurrence occurrence : allOccurrences) {
      List<String> fieldValues = new ArrayList<String>();
      if (fieldNames == null) {
        if (isMatchBasicFilters(occurrence, resultFilter)) {
          count++;
        }
      } else {
        boolean isMatch = true;
        for (int i = 0; i < fieldNames.length; i++) {
          String fieldName = fieldNames[i].toLowerCase();
          Object value = getOccurrenceFieldValue(occurrence, fieldName);
          if (value == null) {
            isMatch = false;
            break;
          }
          String fieldValue = value.toString().toLowerCase();
          String operator = operators[i];
          String searchValues[] = values[i];
          if (!isMatchBasicFilters(occurrence, resultFilter)) {
            isMatch = false;
          } else if (otherFixedValues.get(fieldName) != null) {
            boolean isSpecialMatch = isSpecialFieldMatch(otherFixedValues
                    .get(fieldName), searchValues[0], specialFixedValues
                    .get(fieldName), fieldValue, operator);
            if (!isSpecialMatch) {
              isMatch = false;
            }
          } else if (operator.equals("=")) {
            String searchValue = searchValues[0].toLowerCase();
            if (fieldName.equalsIgnoreCase("acceptedSpecies")) {
              if (!fieldValue.contains(searchValue)
                      && !searchValue.equals(fieldValue)) {
                isMatch = false;
              }
            } else if (!searchValue.equals(fieldValue)) {
              isMatch = false;
            }
          } else if (operator.equals("!=")) {
            String searchValue = searchValues[0].toLowerCase();
            if (searchValue.equals(fieldValue)) {
              isMatch = false;
            }
          } else if (operator.equals("like")) {
            String searchValue = searchValues[0].toLowerCase();
            if (!fieldValue.contains(searchValue)) {
              isMatch = false;
            }
          } else if (operator.equals("!like")) {
            String searchValue = searchValues[0].toLowerCase();
            if (fieldValue.contains(searchValue)) {
              isMatch = false;
            }
          } else if (operator.equals("<=") || operator.equals(">=")) {
            try {
              double numberResult = Double.parseDouble(fieldValue);
              double numberValue1 = Double.parseDouble(searchValues[0]);
              double numberValue2 = Double.parseDouble(searchValues[1]);
              if (!(numberResult >= numberValue1 && numberResult <= numberValue2)) {
                isMatch = false;
              }
            } catch (Exception e) {
              if (!(fieldValue.compareTo(searchValues[0]) >= 0 && fieldValue
                      .compareTo(searchValues[1]) <= 0)) {
                isMatch = false;
              }
            }
          }
          if (!isMatch) {
            break;
          }
        }
        if (isMatch) {
          count++;
        }
      }
    }

    return count;
  }

  private Object getOccurrenceFieldValue(Occurrence occurrence, String field) {
    Method method = fieldToGetMethodMap.get(field.toLowerCase());
    try {
      return method.invoke(occurrence, null);
    } catch (IllegalArgumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      // TODO Auto-generated catch block

      e.printStackTrace();
    } catch (Exception e) {

    }
    fail("fail invoking method get" + field);
    return "";
  }

  private String[] getOperators(String fieldName) {
    String operators[] = null;
    if (fieldName.equalsIgnoreCase("acceptedSpecies")) {
      operators = new String[] { "=" };
    } else if (fieldName.equalsIgnoreCase("basisOfRecord")
            || fieldName.equalsIgnoreCase("sex")) {
      operators = new String[] { "=", "!=" };
    } else {
      operators = new String[] { "=", "!=", "like", "!like" };
    }
    return operators;
  }

  private String[] getPropertyValues(String key) {
    return testProperties.getProperty(key).split(",");
  }

  private String getRandomOperator(String fieldName) {
    String operators[] = getOperators(fieldName);
    Random random = new Random();
    int index = random.nextInt(operators.length);
    return operators[index];
  }

  private boolean isMatchBasicFilters(Occurrence occurrence,
          ResultFilter resultFilter) {
    String userId = testProperties.getProperty("userId");
    if (resultFilter == ResultFilter.BOTH) {
      if (!occurrence.isPublic_() && !occurrence.getOwner().equals(userId)) {
        return false;
      }
    } else {
      if (isPublic != null && !isPublic.equals(occurrence.isPublic_())) {
        return false;
      }
    }
    if (isMineOnly && !occurrence.getOwner().equals(userId)) {
      return false;
    }

    if (isValidated != null && !isValidated.equals(occurrence.isValidated())) {
      return false;
    }

    if (isVetted != null && !isVetted.equals(occurrence.isVetted())) {
      return false;
    }

    return true;
  }

  private boolean isSpecialFieldMatch(String otherValue, String searchValue,
          String[] availableValues, String resultValue, String operator) {
    if (otherValue == null) {
      return true;
    }
    if (searchValue.equalsIgnoreCase(otherValue)) {
      boolean isAvailableValue = false;
      for (String availableValue : availableValues) {
        if (availableValue.equalsIgnoreCase(resultValue)) {
          isAvailableValue = true;
          break;
        }
      }
      if (operator.equals("=")) {
        return !isAvailableValue;
      } else if (operator.equals("!=")) {
        return isAvailableValue;
      }
    } else {
      if (operator.equals("=")) {
        return searchValue.equalsIgnoreCase(resultValue);
      } else if (operator.equals("!=")) {
        return !searchValue.equalsIgnoreCase(resultValue);
      }
    }

    return false;
  }

  private void runTest(Set<String> filters, ResultFilter resultFilter,
          boolean isAll) throws Exception {
    isValidated = null;
    isVetted = null;
    isPublic = null;
    isMineOnly = false;
    if (filters != null) {
      for (String filter : filters) {
        String propertyValue[] = filter.split("=");
        String property = propertyValue[0].trim().toLowerCase();
        String value = propertyValue[1].trim().toLowerCase();
        if (property.equals("validated")) {
          isValidated = Boolean.parseBoolean(value);
        } else if (property.equals("vetted")) {
          isVetted = Boolean.parseBoolean(value);
        } else if (property.equals("owner")) {
          isMineOnly = true;
        }
      }
    }
    if (resultFilter == ResultFilter.BOTH && !isMineOnly) {
      resultFilter = ResultFilter.PUBLIC;
    }
    if (resultFilter == null) {
      isPublic = true;
    } else {
      switch (resultFilter) {
      case BOTH:
      case PUBLIC:
        isPublic = true;
        break;
      case PRIVATE:
        isPublic = false;
        break;
      }
    }
    if (isAll) {
      OccurrenceQuery query = getBaseQuery(filters, resultFilter);
      checkResult(query, null, null, null);
      checkSingleAdvanceSearchResult(query, filters, resultFilter);
    }
    // user logged in.
    resultFilter = (isAll && resultFilter == ResultFilter.PUBLIC) ? ResultFilter.BOTH
            : resultFilter;
    query = getBaseQuery(filters, resultFilter);
    userId = Integer.parseInt(testProperties.getProperty("userId"));
    checkResult(query, null, null, null);
    checkSingleAdvanceSearchResult(query, filters, resultFilter);
  }
}

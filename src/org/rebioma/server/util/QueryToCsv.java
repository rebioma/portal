/*
 * Copyright 2008 University of California at Berkeley
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.rebioma.server.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rebioma.client.bean.Occurrence;

public class QueryToCsv {

  /**
   * ReturnCsvString returns a String in the CSV format that defines a query of
   * Occurrences
   * 
   * @param occurrenceList
   * @param delimiter TODO
   * @return String in the CSV format
   */
  public static String ReturnCsvString(List<Occurrence> occurrenceList,
      String delimiter) {
    if (occurrenceList.isEmpty()) {
      return null;
    }

    StringBuilder builder = new StringBuilder();
    Occurrence firstOccurrence = occurrenceList.get(0);
    ArrayList<String> fieldList = new ArrayList<String>();
    int counter = 0;
    for (String field : getFieldMap(firstOccurrence).keySet()) {
      counter++;
      fieldList.add(field);
    }

    // add field names to csv as a header. fields are sorted alphabetically
    Collections.sort(fieldList);
    for (String field : fieldList) {
      if (field.contains(delimiter)) {
        field = "\"" + field + "\"";
      }
      builder.append(field + delimiter);
    }
    int lastDelimiterIndex = builder.lastIndexOf(delimiter);
    builder.setCharAt(lastDelimiterIndex, '\n');

    // gets rid of unsightly underscores
    while (builder.lastIndexOf("_") != -1) {
      builder.deleteCharAt(builder.lastIndexOf("_"));
    }

    for (Occurrence occurrence : occurrenceList) {
      Map<String, String> fieldMap = getFieldMap(occurrence);
      for (String key : fieldList) {
        String value = fieldMap.get(key);
        if (value.contains(delimiter)) {
          value = "\"" + value + "\"";
        } else {

        }
        builder.append(value + delimiter);
      }
      lastDelimiterIndex = builder.lastIndexOf(delimiter);
      builder.setCharAt(lastDelimiterIndex, '\n');
    }

    return builder.toString();
  }

  /**
   * getFieldMap returns a Map of Occurrence fields
   * 
   * @return Map from field name to field value as given by a given Occurrence
   *         object
   */
  private static Map<String, String> getFieldMap(Occurrence occurrence) {
    HashMap<String, String> fieldMap = new HashMap<String, String>();

    Method[] methodList = occurrence.getClass().getMethods();
    for (Method method : methodList) {
      String methodName = method.getName();
      String fieldName;
      Object fieldValue;
      if (methodName.startsWith("get")) {
        fieldName = methodName.replace("_", "").substring(3);
      } else if (methodName.startsWith("is")) {
        fieldName = methodName.replace("_", "").substring(2);
      } else if (methodName.equals("isVetted")) {
        continue;
      } else {
        continue;
      }
      try {
        fieldValue = method.invoke(occurrence);
        /**
         * Fixes issue 114.
         * 
         * @see http://code.google.com/p/rebioma/issues/detail?id=114
         * 
         *      The method getClass_ incorrectly invokes getClass, which returns
         *      the Occurrence Class object instead of the value returned by
         *      getClass_. To correct this, we simply get the field value
         *      directly from the occurrence object.
         */
        if (fieldValue instanceof Class) {
          fieldValue = occurrence.getClass_();
        } else if (methodName.equals("getReviewed")) {
          fieldValue = fieldValue == null ? "Waiting" : (fieldValue
              .equals(true) ? "Positive" : "Negative");
        }

        if (fieldValue == null) {
          fieldValue = "";
        }
        fieldMap.put(fieldName, fieldValue.toString());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return fieldMap;
  }
}

package org.rebioma.server.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StringUtil {

  public static String capFirstLetter(String s) {
    return (s.charAt(0) + "").toUpperCase() + s.substring(1);
  }

  public static <C> boolean isString(Class<C> cl, String fieldName) {
    try {
      Field field = cl.getDeclaredField(fieldName);
      return field.getType().toString().equals(String.class.toString());
    } catch (SecurityException e) {

    } catch (NoSuchFieldException e) {

    }
    return false;
  }

  public static <C> boolean isType(Class<C> cl, String fieldName, Class<?> type) {
    try {
      Field field = cl.getDeclaredField(fieldName);
      return field.getType().toString().equals(type.toString());
    } catch (SecurityException e) {

    } catch (NoSuchFieldException e) {

    }
    return false;
  }

  public static void main(String args[]) {
    List<Integer> ids = new ArrayList<Integer>();
    ids.add(1);
    ids.add(2);
    System.out.println((ids instanceof Collection<?>));
  }
}

package org.rebioma.server.elasticsearch.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

import org.rebioma.client.bean.Occurrence;

/**
 * 
 * @author Mikajy
 *
 */
public class ReflectUtils {
	
	public static Method getSetterMethod(Class<?> clazz, Field field) throws NoSuchMethodException, SecurityException{
		String setterName = "set" + upperCaseFirstLetter(field.getName());
		Method setter = clazz.getMethod(setterName, field.getType());
		if(!void.class.isAssignableFrom(setter.getReturnType())){
			throw new NoSuchMethodException("Aucune fonction setter pour le champ " + clazz.getName() +"#"+ field.getName());
		}
		return setter;
	}
	
	public static Method getGetterMethod(Class<?> clazz, Field field) throws NoSuchMethodException, SecurityException{
		
		Method getter;
		try{
			String getterName = "get" + upperCaseFirstLetter(field.getName());
			getter = clazz.getMethod(getterName);
		}catch(NoSuchMethodException e){
			if(Boolean.class.isAssignableFrom(field.getType())){
				String getterName = "is" + upperCaseFirstLetter(field.getName());
				getter = clazz.getMethod(getterName);
			}else{
				throw e;
			}
		}
		Type[] parameterTypes = getter.getParameterTypes();
		if(parameterTypes == null || parameterTypes.length == 0){
			//un getter n'a pas de paramètre
			return getter;
		}else{
			throw new NoSuchMethodException("Aucune fonction getter pour le champ " + clazz.getName() +"#"+ field.getName());
		}
		
	}
	
	public static Occurrence mapRow(Map<String, Object> hitSource){
		Occurrence o = new Occurrence();
//		o.setAcceptedClass((String)hitSource.get("acceptedclass"));
//		o.setAcceptedFamily((String)hitSource.get("acceptedfamily"));
//		o.setAcceptedGenus((String)hitSource.get("acceptedgenus"));
//		o.setAcceptedKingdom((String)hitSource.get("acceptedKingdom"));
//		o.setAcceptedNameUsage((String)hitSource.get("acceptednameusage"));
//		o.setAcceptedNameUsageID((String)hitSource.get("acceptednameusageid"));
//		o.setAcceptedNomenclaturalCode((String)hitSource.get("acceptednomenclaturalcode"));
		
		Field[] fields = o.getClass().getDeclaredFields();
		for(Field field: fields){
			String setterName = "set" + upperCaseFirstLetter(field.getName());
			try {
				String hitSourceKey = field.getName().toLowerCase();
				if(hitSource.containsKey(hitSourceKey)){
					Method setter = o.getClass().getMethod(setterName, field.getType());
					Object value = hitSource.get(hitSourceKey);
					setter.setAccessible(true);
					setter.invoke(o, value);
				}
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return o;
	}
	
	private static String upperCaseFirstLetter(String input){
		String c = input.charAt(0) + "";
		String output = c.toUpperCase().concat(input.substring(1));
		return output;
	}
}

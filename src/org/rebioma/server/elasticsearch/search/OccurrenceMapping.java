package org.rebioma.server.elasticsearch.search;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.User;
import org.rebioma.server.elasticsearch.json.JsonFileUtility;
import org.rebioma.server.elasticsearch.utils.ReflectUtils;

/**
 * 
 * @author Mikajy
 *
 */
public class OccurrenceMapping {
	
	public static Occurrence asOccurrence(Map<String, Object> hitSource){
		Occurrence o = new Occurrence();
		Field[] fields = Occurrence.class.getDeclaredFields();
		for(Field f: fields){
			String hitSourceFieldKey = f.getName().toLowerCase();
			if(hitSource.containsKey(hitSourceFieldKey)){
				Object value = hitSource.get(hitSourceFieldKey);
				try {
					Method setter = ReflectUtils.getSetterMethod(Occurrence.class, f);
					if(!setter.isAccessible()) setter.setAccessible(true);
					setter.invoke(o, f.getType().cast(value));
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
		}
		return o;
	}
	
	public static XContentBuilder asXcontentBuilder(Occurrence o, User ownerUser) throws IOException{
		XContentBuilder source = XContentFactory.jsonBuilder();
		source.startObject();
		Field[] fields = o.getClass().getDeclaredFields();
		for(Field f: fields){
			if("serialVersionUID".equals(f.getName())){
				continue;
			}
			try{
				Method	getterMethod = ReflectUtils.getGetterMethod(Occurrence.class, f);
				if(!getterMethod.isAccessible()) getterMethod.setAccessible(true);
				Object value = getterMethod.invoke(o);
				if(value != null){
					source.field(f.getName().toLowerCase(), f.getType().cast(value));
				}
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String dataManager = ownerUser.getFirstName() + " " + ownerUser.getLastName().toUpperCase() + " - " + ownerUser.getInstitution() + " " + ownerUser.getEmail();
		source.field("data_manager", dataManager);
		source.field("owner_firstname", ownerUser.getFirstName());
		source.field("owner_lastname", ownerUser.getLastName());
		source.field("owner_institution", ownerUser.getInstitution());
		source.field("owner_email", ownerUser.getEmail());
		source.endObject();
		return source;
	}
	
	
	public static String getMappingString() throws IOException{
		String json = JsonFileUtility.getMappingContent(Occurrence.class);
		return json;
	}
	public static XContentBuilder getMapping() throws IOException{
		XContentBuilder builder = XContentFactory.jsonBuilder().startObject().humanReadable(true);
		builder.startObject("occurrence")
				.startObject("properties")
					.startObject("specificepithet")
						.field("type", "string")
						.field("analyzer", "english")
						.field("copy_to", "full_name")
//						.startObject("fields")
//							.startObject("std")
//								.field("type", "string")
//								.field("analyzer", "standard")
//							.endObject()
//						.endObject()
					.endObject()
					.startObject("scientificname")
						.field("type", "string")
						.field("analyzer", "english")
						.field("copy_to", "full_name")
//						.startObject("fields")
//							.startObject("std")
//								.field("type", "string")
//								.field("analyzer", "standard")
//							.endObject()
//						.endObject()
					.endObject()
					.startObject("kingdom")
						.field("type", "string")
						.field("analyzer", "standard")
						.field("copy_to", "biologic_path")
					.endObject()
					.startObject("phylum")
						.field("type", "string")
						.field("analyzer", "english")
						.field("copy_to", "biologic_path")
					.endObject()
					.startObject("class")
						.field("type", "string")
						.field("analyzer", "english")
						.field("copy_to", "biologic_path")
					.endObject()
					.startObject("order")
						.field("type", "string")
						.field("analyzer", "english")
						.field("copy_to", "biologic_path")
					.endObject()
					.startObject("family")
						.field("type", "string")
						.field("analyzer", "english")
						.field("copy_to", "biologic_path")
					.endObject()
					
					.startObject("acceptedspecies")
						.field("type", "string")
						.field("analyzer", "english")
						.field("copy_to", "full_name")
//						.startObject("fields")
//							.startObject("std")
//								.field("type", "string")
//								.field("analyzer", "standard")
//							.endObject()
//						.endObject()
					.endObject()
					
					.startObject("acceptedkingdom")
						.field("type", "string")
						.field("analyzer", "english")
						.field("copy_to", "accepted_biologic_path")
					.endObject()
					
					.startObject("acceptedphylum")
						.field("type", "string")
						.field("analyzer", "english")
						.field("copy_to", "accepted_biologic_path")
					.endObject()
					
					.startObject("acceptedclass")
						.field("type", "string")
						.field("analyzer", "english")
						.field("copy_to", "accepted_biologic_path")
					.endObject()
					
					.startObject("acceptedorder")
						.field("type", "string")
						.field("analyzer", "english")
						.field("copy_to", "accepted_biologic_path")
					.endObject()
					
					.startObject("acceptedsuborder")
						.field("type", "string")
						.field("analyzer", "english")
						.field("copy_to", "accepted_biologic_path")
					.endObject()
					
					.startObject("acceptedfamily")
						.field("type", "string")
						.field("analyzer", "english")
						.field("copy_to", "accepted_biologic_path")
					.endObject()
					
					.startObject("acceptedsubfamily")
						.field("type", "string")
						.field("analyzer", "english")
						.field("copy_to", "accepted_biologic_path")
					.endObject()
					
					.startObject("acceptedgenus")
						.field("type", "string")
						.field("analyzer", "english")
						.field("copy_to", "accepted_biologic_path")
					.endObject()
					
					.startObject("acceptedsubgenus")
						.field("type", "string")
						.field("analyzer", "english")
						.field("copy_to", "accepted_biologic_path")
					.endObject()
					
					.startObject("acceptedspecificepithet")
						.field("type", "string")
						.field("analyzer", "english")
						.field("copy_to", "full_name")
//						.startObject("fields")
//							.startObject("std")
//								.field("type", "string")
//								.field("analyzer", "standard")
//							.endObject()
//						.endObject()
					.endObject()
					.startObject("accepted_biologic_path")
						.field("type", "string")
						.field("analyzer", "english")
					.endObject()
					.startObject("biologic_path")
						.field("type", "string")
						.field("analyzer", "english")
					.endObject()
					.startObject("full_name")
						.field("type", "string")
						.field("analyzer", "english")
					.endObject()
				.endObject().endObject();
		return builder;
	}
}

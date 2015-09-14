/**
 * 
 */
package org.rebioma.server.elasticsearch.search;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.rebioma.client.bean.Taxonomy;
import org.rebioma.server.elasticsearch.json.JsonFileUtility;
import org.rebioma.server.elasticsearch.utils.ReflectUtils;

/**
 * @author Mikajy
 *
 */
public class TaxonomyMapping {
	
	public static Taxonomy asTaxonomy(Map<String, Object> hitSource){
		Taxonomy o = new Taxonomy();
		Field[] fields = Taxonomy.class.getDeclaredFields();
		for(Field f: fields){
			if(f.getType().isAssignableFrom(Date.class)){
				continue;
			}
			String hitSourceFieldKey = f.getName().toLowerCase();
			if(hitSource.containsKey(hitSourceFieldKey)){
				Object value = hitSource.get(hitSourceFieldKey);
				try {
					Method setter = ReflectUtils.getSetterMethod(Taxonomy.class, f);
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
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public static String getMappingString() throws IOException{
		String json = JsonFileUtility.getMappingContent(Taxonomy.class);
		return json;
	}
	
	public static XContentBuilder asXcontentBuilder(Taxonomy taxonomy) throws IOException{
		XContentBuilder source = XContentFactory.jsonBuilder();
		source.startObject();
		Field[] fields = taxonomy.getClass().getDeclaredFields();
		for(Field f: fields){
			if("serialVersionUID".equals(f.getName())){
				continue;
			}else if(f.getType().isAssignableFrom(List.class)){
				continue;
			}
			
			try{
				Method	getterMethod = ReflectUtils.getGetterMethod(Taxonomy.class, f);
				if(!getterMethod.isAccessible()) getterMethod.setAccessible(true);
				Object value = getterMethod.invoke(taxonomy);
				if(value != null){
					source.field(f.getName().toLowerCase(), f.getType().cast(value));
				}
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		source.endObject();
		return source;
	}
}

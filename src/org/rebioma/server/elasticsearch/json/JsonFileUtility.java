/**
 * 
 */
package org.rebioma.server.elasticsearch.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.rebioma.client.bean.Taxonomy;

/**
 * Une classe utilitaire pour manipuler les fichiers json qui se trouvent dans le même package
 * 
 * @author Mikajy
 *
 */
public class JsonFileUtility {
	
	public static String getSettingContent() throws IOException{
		String fileName = "rebioma.setting.json";
		return getFileContent(fileName);
	}
	
	public static String getMappingContent(Class<?> clazz) throws IOException{
//		String path = Occurrence.class.getPackage().getName().replace(".", "/");
		String fileName = clazz.getSimpleName().toLowerCase() + ".mapping.json";
		return getFileContent(fileName);
	}
	
	private static String getFileContent(String fileName) throws IOException{
		InputStream is = JsonFileUtility.class.getResourceAsStream(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while((line = br.readLine()) != null){
			sb.append(line.trim());
		}
		return sb.toString();
	}
	
	public static String generateDefaultMapping(Class<?> clazz){
		Field[] fields = clazz.getDeclaredFields();
		int count = 0;
		StringBuilder mapping = new StringBuilder("{\n");//debut racine
		mapping.append("\"").append(clazz.getSimpleName().toLowerCase()).append("\"").append(":\n");
		mapping.append("\t{\n");//debut class
		mapping.append("\t\t\"").append("properties").append("\"").append(":\n");
		mapping.append("\t\t\t{\n");//debut properties
		Set<String> types = new HashSet<String>();
		for(Field f: fields){
			String key = f.getName().toLowerCase();
			String esType = getEsType(f.getType());
			types.add(getEsType(f.getType()));
			
			mapping.append("\t\t\t\t\"" + key + "\":{\"type\": \"" + esType + "\"},\n");
		}
		mapping.append("\t\t\t}\n");//fin properties
		mapping.append("\t}\n");//fin class
		mapping.append("}");//fin racine
		String filePath = clazz.getPackage().getName().replace(".", "/");
		String fileName = clazz.getSimpleName().toLowerCase() + ".mapping.json.template";
//		FileWriter fileWriter = new FileWriter(fileName);
//		fileWriter.write(mapping.toString());
//		fileWriter.flush();
//		fileWriter.close();
//		System.out.println(count +" fields trouvé et mapping généré dans " + fileName);
		System.out.println(mapping.toString());
		return mapping.toString();
	}
	
	private static String getEsType(Class javaType){
//		String esType = "string";
//		
//		if(java.lang.Boolean.class.isAssignableFrom(javaType)){
//			esType = "boolean";
//		}else if(java.util.Date.class.isAssignableFrom(javaType)){
//			esType = "date";
//		}else if(java.lang.Integer.class.isAssignableFrom(javaType)){
//			esType = "integer";
//		}else{
//			esType = "string";
//		}
//		return esType;
		return javaType.getSimpleName().toLowerCase();
	}
	
	public static void main(String[] args){
		generateDefaultMapping(Taxonomy.class);
	}
	
	
}

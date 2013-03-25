package org.rebioma.server.util;

import org.rebioma.server.services.Revalidation;
import org.rebioma.server.services.RevalidationServiceImpl;

public class RevalidationUtil {
	 public static void main(String args[]) throws Exception {
		Revalidation revalidation = new RevalidationServiceImpl();
		long dateStart = System.currentTimeMillis();
		revalidation.revalidate(null);
		 //Map<String, List<Occurrence>> map =revalidation.revalidateAll();
		 //for(Map.Entry<String, List<Occurrence>> entry : map.entrySet()){
	//		System.out.println(entry.getKey() + " " + entry.getValue().size());
	//	}
		long dateFin = System.currentTimeMillis();
		long duration = (dateFin - dateStart)/1000;
		System.out.println("FIN REVALIDATION apres "+duration+" secondes d'execution.");
	 }
}

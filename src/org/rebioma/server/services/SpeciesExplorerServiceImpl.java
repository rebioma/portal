package org.rebioma.server.services;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.rebioma.client.bean.SpeciesTreeModel;
import org.rebioma.client.bean.Taxonomy;
import org.rebioma.client.services.SpeciesExplorerService;
import org.rebioma.server.util.ManagedSession;

import au.com.bytecode.opencsv.CSVReader;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SpeciesExplorerServiceImpl extends RemoteServiceServlet implements
		SpeciesExplorerService {


	/**occurrence
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final HashMap<String, String> LEVELS=new HashMap<String, String>(){
        {
            put("KINGDOM", "Kingdom");
            put("PHYLUM", "Phylum");
            put("CLASS", "Class");
            put("GENUS", "Genus");
            put("ORDER", "Order");
            put("FAMILY", "Family");
            put("ACCEPTEDSPECIES", "Species");
        }
	};

	public String[] buildSql(SpeciesTreeModel obj) {
		String[] tabs=new String[2];
		String ret="";
		String level="";
		String concerne="";
		String colonne="";
		String where="";
		String colonneSource=" ' ' ";
		String colonneReviewed="";
		String colonneSynonym="";
		String whereTaxonomy=" WHERE 1=1 ";
		
		if(obj==null || obj.get(SpeciesTreeModel.KINGDOM)==null || obj.get(SpeciesTreeModel.KINGDOM).toString().isEmpty()) {
			concerne="kingdom ";
			colonne+="AcceptedKingdom,";
			level=LEVELS.get("KINGDOM");
			//colonneSource=" getInfosKingdom(t.Kingdom)  ";
		}
		if(obj!=null && obj.get(SpeciesTreeModel.KINGDOM)!=null && !obj.get(SpeciesTreeModel.KINGDOM).toString().isEmpty()) {
			concerne="Phylum ";
			colonne+="AcceptedKingdom,";
			where+=" AND upper(Acceptedkingdom)=upper('"+obj.getKingdom()+"') ";
			whereTaxonomy+=" AND upper(kingdom)=upper('"+obj.getKingdom()+"') ";
			
			colonne+="AcceptedPhylum,";
			level=LEVELS.get("PHYLUM");
			//colonneSource=" getInfosPhylum(t.phylum) ";
		}
		if(obj!=null && obj.get(SpeciesTreeModel.PHYLUM)!=null && !obj.get(SpeciesTreeModel.PHYLUM).toString().isEmpty() ) {
			where+=" AND upper(AcceptedPhylum)=upper('"+obj.getPhylum()+"') ";
			whereTaxonomy+=" AND upper(Phylum)=upper('"+obj.getPhylum()+"') ";
			concerne="Class ";
			colonne+="AcceptedClass,";
			level=LEVELS.get("CLASS");
			//colonneSource=" getInfosClass(t.class) ";
		}
		if(obj!=null && obj.get(SpeciesTreeModel.CLASS_)!=null && !obj.get(SpeciesTreeModel.CLASS_).toString().isEmpty() ) {
			where+=" AND upper(Acceptedclass)=upper('"+obj.getClass_()+"') ";
			whereTaxonomy+=" AND upper(class)=upper('"+obj.getClass_()+"') ";
			concerne="order ";
			colonne+="Acceptedorder,";
			level=LEVELS.get("ORDER");
			//colonneSource=" getInfosGenus(t.genus) ";
		}
		if(obj!=null && obj.get(SpeciesTreeModel.ORDER)!=null && !obj.get(SpeciesTreeModel.ORDER).toString().isEmpty() ) {
			where+=" AND upper(Acceptedorder)=upper('"+obj.getOrder()+"') ";
			whereTaxonomy+=" AND upper(\"order\")=upper('"+obj.getOrder()+"') ";
			concerne="family ";
			colonne+="Acceptedfamily,";
			level=LEVELS.get("FAMILY");
			//colonneSource=" getInfosFamily(t.family) ";
		}
		if(obj!=null && obj.get(SpeciesTreeModel.FAMILY)!=null && !obj.get(SpeciesTreeModel.FAMILY).toString().isEmpty() ) {
			where+=" AND upper(Acceptedfamily)=upper('"+obj.getFamily()+"') ";
			whereTaxonomy+=" AND upper(family)=upper('"+obj.getFamily()+"') ";
			concerne="genus ";
			colonne+="Acceptedgenus,";
			level=LEVELS.get("GENUS");
			//colonneSource=" getInfosFamily(t.family) ";
		}
		if(obj!=null && obj.get(SpeciesTreeModel.GENUS)!=null && !obj.get(SpeciesTreeModel.GENUS).toString().isEmpty() ) {
			where+=" AND upper(Acceptedgenus)=upper('"+obj.getGenus()+"') ";
			whereTaxonomy+=" AND upper(genus)=upper('"+obj.getGenus()+"') ";
			concerne="species ";
			colonne+="Acceptedspecies,";
			level=LEVELS.get("ACCEPTEDSPECIES"); 
			//colonneSource=" getInfosAcceptedSpecies(t.AcceptedSpecies) ";
			
		}
		/*
		if(obj!=null && obj.get(SpeciesTreeModel.SUBCLASS)!=null && !obj.get(SpeciesTreeModel.SUBCLASS).toString().isEmpty() ) {
			where+=" AND `Order`='"+obj.getOrder()+"' ";
			concerne="`Order` ";
			colonne+="`Order`,";
		}
		*/
		
		//System.out.println(colonne);
		String concerneTaxonomy=concerne;
		
		if(concerneTaxonomy.equalsIgnoreCase("species "))
			concerneTaxonomy="accepted"+concerneTaxonomy;
		if(concerneTaxonomy.equalsIgnoreCase("order "))
			concerneTaxonomy="\"order\" ";
		String colonneUpper = colonne.replaceAll("Accepted", "upper(Accepted").replaceAll(",", "),");
		ret="SELECT DISTINCT t."+concerneTaxonomy+" as concerne, COALESCE( public,0) as publics, COALESCE(private,0) as privates, upper("+colonneSource+") as source  "+
		" from taxonomy t LEFT JOIN \n" +
		"(\n" +
		"SELECT sum(public)  as public  ,sum(private)  as private, upper(Accepted"+concerne+")  as concerne FROM(\n" +
		"SELECT DISTINCT "+colonne+" count(*) as private, 0 as public FROM Occurrence o WHERE o.Public=false \n" +
		where +
		"GROUP BY "+colonne.substring(0, colonne.length()-1) + " \n "+
		"UNION\n" +
		" SELECT DISTINCT " + colonneUpper +"  0 as private,count(*) as public FROM Occurrence o WHERE o.Public=true \n" +
		where +
		" GROUP BY " + colonneUpper.substring(0, colonneUpper.length()-1)  + " \n "+
		")tb\n" +
		"GROUP BY " +colonneUpper.substring(0, colonneUpper.length()-1)  + " \n "+
		")tt\n" +
		"ON upper(t." +concerneTaxonomy+") = upper(tt.concerne) " + whereTaxonomy + " ORDER BY t." +concerneTaxonomy;
		tabs[0]=ret;
		tabs[1]=level;
		return tabs;
	}
	@SuppressWarnings("deprecation")
	@Override
	public List<SpeciesTreeModel> getChildren(SpeciesTreeModel parent) {
		List<SpeciesTreeModel> listToReturn = new ArrayList<SpeciesTreeModel>();
		String temp[] = buildSql(parent);
		String sql=temp[0];
		System.out.println(sql);
		
		Session sess = null;
		
		Connection conn =null;
		Statement st=null;
		ResultSet rst=null;
		try {
			sess = ManagedSession.createNewSessionAndTransaction(); 
			conn=sess.connection();
			
			st = conn.createStatement();
			rst = st.executeQuery(sql);
			while(rst.next()) {
				String concerne = rst.getString("concerne");
				if(concerne == null){
					continue;
				}
				SpeciesTreeModel child1 = new SpeciesTreeModel();
				child1.setLabel(concerne);
				child1.setLevel(temp[1]);
				child1.setKingdom(rst.getString("concerne"));
				child1.setNbPrivateOccurence(rst.getInt("privates"));
				child1.setNbPublicOccurence(rst.getInt("publics"));
				child1.setSource(rst.getString("source"));
				if(child1.getSource()!=null && !child1.getSource().isEmpty())
					if(!child1.getSource().isEmpty() && child1.getSource().length()>2) child1.setSource(child1.getSource().substring(2));
				if(SpeciesTreeModel.KINGDOM.equals(temp[1])){
					child1.setKingdom(concerne);
				} else if(SpeciesTreeModel.PHYLUM.equals(temp[1])){
					child1.setKingdom(parent.getKingdom());
					child1.setPhylum(concerne);
				} else if(SpeciesTreeModel.CLASS_.equals(temp[1])){
					child1.setKingdom(parent.getKingdom());
					child1.setPhylum(parent.getPhylum());
					child1.setClass_(concerne);
				} else if(SpeciesTreeModel.SUBCLASS.equals(temp[1])){
					child1.setKingdom(parent.getKingdom());
					child1.setPhylum(parent.getPhylum());
					child1.setClass_(parent.getClass_());
					child1.setSubclass(concerne);
				} else if(SpeciesTreeModel.ORDER.equals(temp[1])){
					child1.setKingdom(parent.getKingdom());
					child1.setPhylum(parent.getPhylum());
					child1.setClass_(parent.getClass_());
					child1.setSubclass(parent.getSubclass());
					child1.setOrder(concerne);
				} else if(SpeciesTreeModel.SUPERFAMILY.equals(temp[1])){
					child1.setKingdom(parent.getKingdom());
					child1.setPhylum(parent.getPhylum());
					child1.setClass_(parent.getClass_());
					child1.setSubclass(parent.getSubclass());
					child1.setOrder(parent.getOrder());
					child1.setSuperfamily(concerne);
				} else if(SpeciesTreeModel.FAMILY.equals(temp[1])){
					child1.setKingdom(parent.getKingdom());
					child1.setPhylum(parent.getPhylum());
					child1.setClass_(parent.getClass_());
					child1.setSubclass(parent.getSubclass());
					child1.setOrder(parent.getOrder());
					child1.setSuperfamily(parent.getSuperfamily());
					child1.setFamily(concerne);
				}  else if(SpeciesTreeModel.GENUS.equals(temp[1])){
					child1.setKingdom(parent.getKingdom());
					child1.setPhylum(parent.getPhylum());
					child1.setClass_(parent.getClass_());
					child1.setSubclass(parent.getSubclass());
					child1.setOrder(parent.getOrder());
					child1.setSuperfamily(parent.getSuperfamily());
					child1.setFamily(parent.getFamily());
					child1.setGenus(concerne);
				}  else if(SpeciesTreeModel.ACCEPTEDSPECIES.equals(temp[1])){
					child1.setKingdom(parent.getKingdom());
					child1.setPhylum(parent.getPhylum());
					child1.setClass_(parent.getClass_());
					child1.setSubclass(parent.getSubclass());
					child1.setOrder(parent.getOrder());
					child1.setSuperfamily(parent.getSuperfamily());
					child1.setFamily(parent.getFamily());
					child1.setGenus(parent.getGenus());
					child1.setAcceptedspecies(concerne);
				} 
				//child1.setAuthorityName(child1.getLabel() + "Authority");
				//child1.setSource(child1.getLabel() + "Source");
				//child1.setReviewerName(child1.getLabel() + "Reviewer");
				//child1.setStatus(child1.getLabel() + "Status");
				//child1.setVernecularName(child1.getLabel() + "vernecularName");
				//child1.setSynonymisedTaxa(child1.getLabel() + "Synonymised Taxa");
				listToReturn.add(child1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(rst!=null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(st!=null) {
				try {
					st.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(conn!=null) {
				try {
					conn.close();
				} catch (SQLException e) {					
					e.printStackTrace();
				}
			}
			if(sess!=null)
				sess.close();
		}	
		return listToReturn;
		/*
		if (parent == null) {
			 
			SpeciesTreeModel root1 = new SpeciesTreeModel();
			root1.setLabel("Animalia");
			root1.setLevel("Kingdom");
			root1.setNbPrivateOccurence(20);
			root1.setNbPublicOccurence(15);
			root1.setAuthorityName("Animale");
			root1.setSource("Animalia source");
 
			SpeciesTreeModel root2 = new SpeciesTreeModel();
			root2.setLabel("Vegetalia");
			root2.setLevel("Kingdom");
			root2.setAuthorityName("Vegetal");
			root2.setSource("Vegetalia source");
			root2.setNbPrivateOccurence(45);
			root2.setNbPublicOccurence(52);
		
			String sql=buildSql(parent);
			List<SpeciesTreeModel> listToReturn = new ArrayList<SpeciesTreeModel>();
			
			return listToReturn;
		}
		SpeciesTreeModel child1 = new SpeciesTreeModel();
		String profondeur = "1";
		child1.setLabel(parent.getLabel() + profondeur);
		child1.setLevel(parent.getLevel() + profondeur);
		child1.setNbPrivateOccurence(parent.getNbPrivateOccurence());
		child1.setNbPublicOccurence(parent.getNbPublicOccurence());
		child1.setAuthorityName(child1.getLabel() + "Authority");
		child1.setSource(child1.getLabel() + "Source");
		child1.setReviewerName(child1.getLabel() + "Reviewer");
		child1.setStatus(child1.getLabel() + "Status");
		child1.setVernecularName(child1.getLabel() + "vernecularName");
		child1.setSynonymisedTaxa(child1.getLabel() + "Synonymised Taxa");
		
		profondeur = "2";
		SpeciesTreeModel child2 = new SpeciesTreeModel();
		child2.setLabel(parent.getLabel() + profondeur);
		child2.setLevel(parent.getLevel() + profondeur);
		child2.setNbPrivateOccurence(parent.getNbPrivateOccurence());
		child2.setNbPublicOccurence(parent.getNbPublicOccurence());
		child2.setAuthorityName(child2.getLabel() + "Authority");
		child2.setSource(child2.getLabel() + "Source");
		child2.setReviewerName(child2.getLabel() + "Reviewer");
		child2.setStatus(child2.getLabel() + "Status");
		child2.setVernecularName(child2.getLabel() + "vernecularName");
		child2.setSynonymisedTaxa(child2.getLabel() + "Synonymised Taxa");
		return Arrays.asList(child1, child2);
		*/
	}
	
	public void loadCsv(){
	    List<Taxonomy> listTaxonomy=new ArrayList<Taxonomy>();
		try{
			/*
			ColumnPositionMappingStrategy strat = new ColumnPositionMappingStrategy();
			strat.setType(Taxonomy.class);
			String[] columns = new String[] {"taId", "idAccepted", "nomenclaturalCode" ,"kingdom","kingdomSource","phylum","phylumSource","subPhylum","subPhylumSource","class_",
												"classSource", "subclass", "subclassSource" ,"superOrder","superOrderSource","order","orderSource","suborder","suborderSource","infraOrder",	
												"infraOrderSource", "superFamily", "superfamilySource" ,"family","familySource","subfamily","subfamilySource","genus","genusSource","subgenus",	
												"subgenusSource", "specificEpithet", "specificEpithetSource" ,"infraspecificRank","infraspecificEpithet","infraspecificEpithetSource","acceptedSpecies","verbatimSpecies","verbatimSpeciesSource","isMarine",	
												"isTerrestrial", "comments", "reviewedBy" ,"reviewDate","notes","status","changedBy","changeDate","validation","validatedBy","validationDate","stability"	
											}; // the fields to bind do in your JavaBean
			strat.setColumnMapping(columns);

			CsvToBean<Taxonomy> csv = new CsvToBean<Taxonomy>();
			 */
			InputStream pdInputStream = this.getClass().getResourceAsStream("taxonomy.csv");  
			// pass the reference to the inputstream reader for further processing  
			Reader read = new InputStreamReader(pdInputStream);   
			
			//List<Taxonomy> listTaxonomy = csv.parse(strat, reader);
			//System.out.println(listTaxonomy.size());
			CSVReader reader = new CSVReader(read);
		    String [] nextLine;
		    int i=0;
		    HashMap<Integer, String> mapColonne=new HashMap<Integer, String>();
		    while ((nextLine = reader.readNext()) != null) {
		        // nextLine[] is an array of values from the line
		    	Object o = new Taxonomy();
		    	for(int j=0;j< nextLine.length;j++) {
		    		if(i==0) {
		    			Taxonomy tax=new Taxonomy();
		    			String val=nextLine[j].replaceAll("_", "");
		    			if(val.equalsIgnoreCase("class"))
		    				val="Class_";
		    			mapColonne.put(Integer.valueOf(j), val);
		    		}else {
		    			java.beans.Statement stmt;
		    			Object value=null;
		    			if(Taxonomy.COLONNE_DOUBLE.contains(mapColonne.get(new Integer(j)))) {
		    				if(nextLine[j]==null || nextLine[j]=="" ||  nextLine[j].isEmpty()  )
		    					value=null;
		    				else 
		    					value=new Double(nextLine[j]);
		    			}
		    			else if(Taxonomy.COLONNE_DATE.contains(mapColonne.get(new Integer(j)))) {	    				
		    				if(nextLine[j]==null || nextLine[j]=="" )
		    					value=null;
		    				else {
		    					String daty=nextLine[j].split(" ")[0];
			    				try {
									DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
									value = (Date)formatter.parse(daty);
								} catch (Exception e) {
									//DateFormat formatter = new SimpleDateFormat("dd/M/yyyy");
									//value = (Date)formatter.parse(daty);
								}
		    				}	
		    				//System.out.println(nextLine[j] + " ... " +value);
		    			}else {
		    				value=nextLine[j];
		    			}
		    	    	stmt = new java.beans.Statement(o, "set"+mapColonne.get(new Integer(j)), new Object[]{value});
		    	    	stmt.execute();
		    		}
		    	}
		    	if(i>0) {
		    		((Taxonomy) o).setId(i+1);
			    	listTaxonomy.add((Taxonomy) o);
		    	}
		    	
		        i++;
		    }
		}catch(Exception e){
			throw new RuntimeException("Erreur de chargement du fichier taxonomy.csv", e);
		}
		
	    /*
	    for(Entry<Integer, String> entry : mapColonne.entrySet()){
	    		System.out.println(entry.getKey() + " " + entry.getValue());
	   	}
	   	*/
	    Session session=null;
		Transaction tx=null;
		try {
			session = ManagedSession.createNewSessionAndTransaction();
			//tx=session.beginTransaction();	
			
			session.createSQLQuery("DELETE FROM taxonomy").executeUpdate();
			
			for (Taxonomy taxonomy : listTaxonomy) {
				session.save(taxonomy);
			}
			ManagedSession.commitTransaction(session);
			//tx.commit();			
  	    }catch (Exception re) {			
  	      if(session!=null)ManagedSession.rollbackTransaction(session);
  	      throw re;
  	    } 
		finally {			
		     //if(session!=null) session.close();
		 }
	    
	}
	

}

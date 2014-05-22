package org.rebioma.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.rebioma.client.KmlUtil;
import org.rebioma.client.bean.KmlDbRow;
import org.rebioma.client.bean.ShapeFileInfo;
import org.rebioma.server.services.DBFactory;
import org.rebioma.server.services.ShapeFileService;
import org.rebioma.server.services.ShapeFileServiceImpl;
import org.rebioma.server.util.HibernateUtil;


public class KmlFileServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6949654865922950754L;
	
	private static final NumberFormat numberFormat; 
	
	private ShapeFileService shapeFileService = ShapeFileServiceImpl.getInstance();
	
	static{
		numberFormat = new DecimalFormat() ;
		numberFormat.setMaximumFractionDigits(2); // la tu auras au plus 2 chiffres apres la virgule
//		numberFormat.setMinimumFractionDigits(2);  // maintenant tout tes nombres auront 2 chiffres après la virgule
	}
	
	 private final Logger log = Logger.getLogger(KmlFileServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		final double gisTableSimplificationTolerance = 0.01d;//à dynamiser
		//on recupère les autres paramètres
		Map<String, List<Integer>> tableGidsMap = new HashMap<String, List<Integer>>();
		final String gidsSplitRegex = "\\" + KmlUtil.GIDS_ITEM_SEPARATOR;
		String tableParamName = KmlUtil.TABLE_PARAM_NAME;
		String tableGidsParamName = KmlUtil.GIDS_PARAM_NAME;
		String tableName = req.getParameter(tableParamName);
		String tableGidsParam = req.getParameter(tableGidsParamName);
		if(tableName  != null && tableName.trim() != ""){
			//on va decouper les gids et les transformer en integer
			List<Integer> gids = new ArrayList<Integer>();
			if(tableGidsParam != null && tableGidsParam.trim() != ""){
				String[] gidsStrs = tableGidsParam.split(gidsSplitRegex);
				for(String gidsStr: gidsStrs){
					try{
						gids.add(Integer.parseInt(gidsStr));
					}catch(NumberFormatException nfe){
						//log ?
					}
				}
			}
			if(tableGidsMap.containsKey(tableName)){
				tableGidsMap.get(tableName).addAll(gids);
			}else{
				tableGidsMap.put(tableName, gids);
			}
			//
			java.util.Collections.sort(gids);
			String kmlFileName = getKmlFileName(tableName, gids, gisTableSimplificationTolerance);

			//resp.setContentType("application/vnd.google-earth.kmz"); //'application/vnd.google-earth.kml+xml' pour un fichier kml
			resp.setContentType("application/vnd.google-earth.kmz");
			String kmzFileName = kmlFileName.replaceAll(".kml", "");
	        resp.setHeader("Content-Disposition", "attachment; filename=" + kmzFileName +".kmz");
			File kmlFile = new File(getTempPath(), kmlFileName);
			log.info("**************************************");
			log.info("Mise à disposition du fichier " + kmlFile.getAbsolutePath() + " pour un telechargement");
			log.info("**************************************");
			DBFactory.getFileValidationService().zipFiles(new File[] {kmlFile}, resp.getOutputStream());
		}
		//kmlFile.delete();
	}
	
	private String getKmlFileName(String tableName, List<Integer> gids, double gisSimplificationTolerance) throws IOException{
		ShapeFileInfo shapeFileInfo = shapeFileService.getShapeFileInfo(tableName);
		if(shapeFileInfo == null){
			throw new IllegalArgumentException("Erreur de recuperation des infos de la table [" + tableName + "] dans la table info_shape");
		}
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ")
			.append(shapeFileInfo.getNomChampGid()).append(" as ").append(KmlUtil.KML_GID_NAME).append(", ")
			.append(shapeFileInfo.getNomChampLibelle()).append(" as ").append(KmlUtil.KML_LABEL_NAME)
			.append(", ST_AsKML(ST_Simplify(" + shapeFileInfo.getNomChampGeometrique() + ", :tolerance)) as gisAsKmlResult ");
		sql.append(" FROM ").append(tableName)
			.append(" WHERE ").append(shapeFileInfo.getNomChampGid()).append(" IN (");
		
		String tolerance = numberFormat.format(gisSimplificationTolerance);
		tolerance = tolerance.replace(',', '_');
		StringBuilder fileNameSuffix = new StringBuilder();
		fileNameSuffix.append(tolerance).append("_")
			.append(gids.size());
		int idx = 0;
		for(Integer gid: gids){//on prefère utilise des OR plutot que un IN
			if(idx > 0){
				sql.append(",");
			}
			sql.append(gid);
			fileNameSuffix.append(gid);
			idx++;
		}
		sql.append(") ");
		String fileName = tableName + fileNameSuffix.toString();
		File file = new File(getTempPath() + "/" + fileName + ".kml");
		if(!file.exists()){
			log.info("**************************************");
			log.info("Création du fichier " + file.getAbsolutePath());
			log.info("**************************************");
			Session sess = HibernateUtil.getSessionFactory().openSession();
			SQLQuery sqlQuery = sess.createSQLQuery(sql.toString());
			sqlQuery.setDouble("tolerance", gisSimplificationTolerance);
			sqlQuery.addScalar(KmlUtil.KML_GID_NAME);
			sqlQuery.addScalar(KmlUtil.KML_LABEL_NAME);
			sqlQuery.addScalar("gisAsKmlResult");
			sqlQuery.setResultTransformer(Transformers.aliasToBean(KmlDbRow.class));
			List<KmlDbRow> kmlDbRows = sqlQuery.list();
			String kml = KmlUtil.getKMLString(kmlDbRows);
			writeKmlFile(kml, getTempPath(), fileName);
			log.info("**************************************");
			log.info("Fichier " + file.getAbsolutePath() + " Créé");
			log.info("**************************************");
		}else{
			log.info("**************************************");
			log.info("Le fichier " + file.getAbsolutePath() + " existe déjà");
			log.info("**************************************");
		}
		return fileName + ".kml";
	}
	
	private void writeKmlFile(String kml, String filePath, String fileName) throws IOException{
		File file = new File(filePath + "/" + fileName + ".kml");
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(kml);
		writer.close();
	}
	
	private String getTempPath(){
		String rootPath = getServletContext().getRealPath("/");
		String tempPath = rootPath + "temp";
		log.info("Temp directory: " + tempPath);
		return tempPath;
	}
	
}

package org.rebioma.server.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.rebioma.client.bean.KmlDbRow;
import org.rebioma.client.bean.ShapeFileInfo;
import org.rebioma.client.services.MapGisService;
import org.rebioma.server.util.HibernateUtil;
import org.rebioma.server.util.ManagedSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class MapGisServiceImpl extends RemoteServiceServlet implements
		MapGisService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1166479109921202488L;
	
	private ShapeFileService shapeFileService = ShapeFileServiceImpl.getInstance();

	@Override
	public List<Integer> findOccurrenceIdByGeom(String kml) {
		List<Integer> occurrenceIds = new ArrayList<Integer>();
		Session sess = HibernateUtil.getSessionFactory().openSession();
		SQLQuery sqlQuery = sess
				.createSQLQuery("SELECT ST_IsValid(ST_GeomFromKML(:kml)) as isValid ");
		sqlQuery.setParameter("kml", kml);
		Boolean isValide = (Boolean) sqlQuery.uniqueResult();
		if (isValide) {
			sqlQuery = sess
					.createSQLQuery("SELECT id FROM occurrence WHERE ST_Within(geom, ST_GeomFromKML(:kml))='t'");
			sqlQuery.setParameter("kml", kml);
			occurrenceIds = sqlQuery.list();
		} else {
			throw new RuntimeException("Le kml généré n'est pas valide \n ["
					+ kml + "]");
		}
		return occurrenceIds;
	}

	@Override
	public List<ShapeFileInfo> getShapeFileItems(ShapeFileInfo shapeFile) {
		List<ShapeFileInfo> infos = new ArrayList<ShapeFileInfo>();
		if (shapeFile == null) {
			// recuperer la liste des fichier shapes
			try {
				infos = shapeFileService.getListeShapeFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Session sess = null;
			try {
				sess = HibernateUtil.getSessionFactory().openSession();
				StringBuilder sqlBuilder = new StringBuilder();
				sqlBuilder.append("SELECT ")
							.append(shapeFile.getNomChampGid()).append(" as ").append("gid, ")
							.append(shapeFile.getNomChampLibelle()).append(" as ").append("name ")
							.append(" FROM ").append(shapeFile.getTableName())
							.append(" ORDER BY ").append(shapeFile.getNomChampLibelle());
				SQLQuery sqlQuery = sess.createSQLQuery(sqlBuilder.toString());
				sqlQuery.addScalar("gid");
				sqlQuery.addScalar("name");
				sqlQuery.setResultTransformer(Transformers
						.aliasToBean(KmlDbRow.class));
				List<KmlDbRow> kmlDbRows = sqlQuery.list();
				// recuperer les lignes d'un fichier shape
				for (KmlDbRow row : kmlDbRows) {
					ShapeFileInfo info = new ShapeFileInfo();
					info.setGid(row.getGid());
					info.setLibelle(row.getName());
					info.setTableName(shapeFile.getTableName());
					info.setNomChampGeometrique(shapeFile.getNomChampGeometrique());
					info.setNomChampGid(shapeFile.getNomChampGid());
					info.setNomChampLibelle(shapeFile.getNomChampLibelle());
					infos.add(info);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(sess!=null)
					sess.close();
			}
		}
		return infos;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> findOccurrenceIdsByShapeFiles(
			Map<String, List<Integer>> tableGidsMap) {
		StringBuilder sqlBuilder = new StringBuilder(
				"SELECT o.id FROM occurrence o ");
		StringBuilder sqlWhereBuilder = new StringBuilder();
		int index = 0;
		Map<String, List<Integer>> gidsParamMap = new HashMap<String, List<Integer>>();
		for (Entry<String, List<Integer>> entry : tableGidsMap.entrySet()) {
			// String paramName = "gids" + index;
			String tableName = entry.getKey();
			ShapeFileInfo tableInfo = shapeFileService.getShapeFileInfo(tableName);
			List<Integer> gids = entry.getValue();
			sqlBuilder.append(" JOIN ").append(tableName).append(" ON ");
			// sqlBuilder.append(tableName).append(".gid IN (:").append(paramName).append(") ");
			sqlBuilder.append("( ");
			int gidIdx = 0;
			for (Integer gid : gids) {
				if (gidIdx > 0) {
					sqlBuilder.append(" OR ");
				}
				sqlBuilder.append(tableName).append(".").append(tableInfo.getNomChampGid()).append("=").append(gid);
				gidIdx++;
			}
			sqlBuilder.append(") ");
			if (sqlWhereBuilder.length() == 0) {
				sqlWhereBuilder.append(" WHERE ");
			} else {
				sqlWhereBuilder.append(" OR ");
			}
			sqlWhereBuilder.append(" ST_Within(").append("o.geom,")
					.append(tableName).append(".").append(tableInfo.getNomChampGeometrique()).append(")='t' ");
			// gidsParamMap.put(paramName, gids);
			index++;
		}
		sqlBuilder.append(sqlWhereBuilder.toString());
		List<Integer> occurrenceIds = new ArrayList<Integer>();
		Session sess = HibernateUtil.getSessionFactory().openSession();
		SQLQuery sqlQuery = sess.createSQLQuery(sqlBuilder.toString());
		// for(Entry<String, List<Integer>> paramEntry:
		// gidsParamMap.entrySet()){
		// sqlQuery.setParameter(paramEntry.getKey(), paramEntry.getValue());
		// }
		occurrenceIds = sqlQuery.list();
		return occurrenceIds;
	}

	// @Override
	// public List<Integer> findOccurrenceIdByGeom(/*OverlayType overlayType,*/
	// List<LatLng> geomCoordonnees) {
	// List<Integer> occurrenceIds = new ArrayList<Integer>();
	// String kml = KmlGenerator.kmlFromCoords(/*overlayType,
	// */geomCoordonnees);
	// Session sess = HibernateUtil.getSessionFactory().openSession();
	// sess=HibernateUtil.getSessionFactory().openSession();
	// SQLQuery sqlQuery =
	// sess.createSQLQuery("SELECT ST_IsValid(ST_GeomFromKML(:kml)) as isValid ");
	// sqlQuery.setParameter("kml", kml);
	// sqlQuery.addEntity(Boolean.class);
	// Boolean isValide = (Boolean)sqlQuery.uniqueResult();
	// if(isValide){
	// sqlQuery =
	// sess.createSQLQuery("SELECT id FROM occurrence WHERE ST_Within(geom, ST_GeomFromKML(:kml))='t'");
	// sqlQuery.addEntity(Integer.class);
	// sqlQuery.setParameter("kml", kml);
	// occurrenceIds = sqlQuery.list();
	// }else{
	// throw new RuntimeException("Le kml généré n'est pas valide \n ["+ kml
	// +"]");
	// }
	// return occurrenceIds;
	// }
	public void launchBatch(String pathShape, String pathShp2pgsql) {
		System.out.println("START Batch " + new Date());
		System.out.println("pathshape " + pathShape + "   pathshp2pgsql "
				+ pathShp2pgsql);
		File dir = new File(pathShape);
		ArrayList<String> treatedFiles = new ArrayList<String>();
		for (File child : dir.listFiles()) {
			String extension = "";
			String filename = "";
			int i = child.getName().lastIndexOf('.');
			if (i > 0) {
				extension = child.getName().substring(i + 1);
				filename = child.getName().substring(0, i);
			}
			if (!extension.equalsIgnoreCase("shp"))
				continue;
			System.out.println("*** Start treating " + filename);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			// CommandLine cmdLine =
			// CommandLine.parse("C:\\Program Files (x86)\\PostgreSQL\\9.0\\bin\\shp2pgsql C:\\Users\\vahatra\\Downloads\\Region\\Region\\lim_region_aout06.shp lim_region_aout06_test rebioma");
			System.out.println(pathShp2pgsql + "   " + child.getAbsolutePath()
					+ "  " + filename);
			CommandLine cmdLine = CommandLine.parse(pathShp2pgsql + "   "
					+ child.getAbsolutePath() + "  " + filename);
			PumpStreamHandler psh = new PumpStreamHandler(bos, System.out);
			DefaultExecutor executor = new DefaultExecutor();
			executor.setStreamHandler(psh);
			try {

				executor.execute(cmdLine);

				String sql = bos.toString();

				Session session = null;

				try {
					session = ManagedSession.createNewSessionAndTransaction();

					// session.createSQLQuery("SET CLIENT_ENCODING TO LATIN1 ").executeUpdate();
					session.createSQLQuery("DROP TABLE  IF EXISTS " + filename)
							.executeUpdate();
					session.createSQLQuery(
							"DELETE FROM info_shape WHERE shapetable='"
									+ filename + "'").executeUpdate();
					session.createSQLQuery(
							"insert into info_shape(shapetable) values('"
									+ filename + "')").executeUpdate();
					session.createSQLQuery(sql).executeUpdate();
					session.createSQLQuery(
							"CREATE INDEX idx_" + filename + " ON " + filename
									+ " USING GIST(geom)").executeUpdate();

					ManagedSession.commitTransaction(session);
					// tx.commit();
				} catch (Exception re) {
					if (session != null)
						ManagedSession.rollbackTransaction(session);
					re.printStackTrace();
				} finally {
					// if(session!=null) session.close();
				}

				bos.close();
				System.out.println("*** End treating " + filename);
				treatedFiles.add(filename);
			} catch (ExecuteException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		// deleting files
		System.out.println("*** DELETING FILES ");
		for (String str : treatedFiles) {
			for (File child : dir.listFiles()) {
				if (child.getName().startsWith(str))
					child.delete();
			}
		}
		System.out.println("*** END DELETING FILES ");
		System.out.println("END batch");
	}

}

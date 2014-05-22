package org.rebioma.server.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.rebioma.client.bean.ShapeFileInfo;
import org.rebioma.server.util.ManagedSession;

public class ShapeFileServiceImpl implements ShapeFileService {
	
	private static ShapeFileServiceImpl instance = null;
	
	private ShapeFileServiceImpl(){
		
	}

	public static ShapeFileServiceImpl getInstance(){
		if(instance == null){
			instance = new ShapeFileServiceImpl();
		}
		return instance;
	}
	
	@Override
	public List<ShapeFileInfo> getListeShapeFile() throws Exception {
		List<ShapeFileInfo> infos = new ArrayList<ShapeFileInfo>();
		Session sess = null;

		Connection conn = null;
		Statement st = null;
		ResultSet rst = null;
		try {
			sess = ManagedSession.createNewSessionAndTransaction();
			conn = sess.connection();

			st = conn.createStatement();
			rst = st.executeQuery("SELECT shapetable, shapelabel, nom_champ_gid, nom_champ_libelle, nom_champ_geometrique FROM info_shape order by shapelabel ");
			while (rst.next()) {
				ShapeFileInfo info = new ShapeFileInfo();
				info.setLibelle(rst.getString("shapelabel"));
				info.setTableName(rst.getString("shapetable"));
				info.setNomChampGid(rst.getString("nom_champ_gid"));
				info.setNomChampLibelle(rst.getString("nom_champ_libelle"));
				info.setNomChampGeometrique(rst.getString("nom_champ_geometrique"));
				infos.add(info);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rst != null)
				rst.close();
			if (st != null)
				st.close();
			if (conn != null)
				conn.close();
			if (sess != null)
				sess.close();
		}

		return infos;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ShapeFileInfo getShapeFileInfo(String tableName) {
		ShapeFileInfo resultat = null;
		Session sess = null;
		try {
			sess = ManagedSession.createNewSessionAndTransaction();
			SQLQuery sqlQuery = sess.createSQLQuery("SELECT shapetable as tableName, " +
					"nom_champ_gid as nomChampGid, " +
					"nom_champ_libelle as nomChampLibelle, " +
					"nom_champ_geometrique as nomChampGeometrique " +
					"FROM info_shape WHERE shapetable=:shapetable");
			sqlQuery.setParameter("shapetable", tableName);
			sqlQuery.addScalar("tableName");
			sqlQuery.addScalar("nomChampGid");
			sqlQuery.addScalar("nomChampLibelle");
			sqlQuery.addScalar("nomChampGeometrique");
			sqlQuery.setResultTransformer(Transformers
					.aliasToBean(ShapeFileInfo.class));
//			Object result = sqlQuery.uniqueResult();
			List<ShapeFileInfo> list = sqlQuery.list();
			if(list != null && list.size() > 0){
				resultat = list.get(0);
			}
		}finally {
			if (sess != null)
				sess.close();
		}
		return resultat;
	}

}

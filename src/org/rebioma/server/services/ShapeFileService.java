package org.rebioma.server.services;

import java.util.List;

import org.rebioma.client.bean.ShapeFileInfo;

public interface ShapeFileService {
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	List<ShapeFileInfo> getListeShapeFile() throws Exception;
	/**
	 * Récuperer les informations utiles à propos d'un fichier shape
	 * @param tableName - le nom de la table qui contient les données d'un fichier shp
	 * @return {@link ShapeFileInfo} contenant les nom des champs de la table
	 */
	ShapeFileInfo getShapeFileInfo(String tableName);
}

package org.rebioma.client.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public class StatisticModel implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4549008653044458193L;

	public interface StatisticsModelProperties extends
	PropertyAccess<StatisticModel> {

		@Path("title")
		ModelKeyProvider<StatisticModel> key();
		
		ValueProvider<StatisticModel, Integer> statisticType();
		
		ValueProvider<StatisticModel, String> title();
		
		ValueProvider<StatisticModel, Integer> nbPrivateData();
		
		ValueProvider<StatisticModel, Integer> nbPublicData();
		ValueProvider<StatisticModel, Integer> nbReliable();
		
		ValueProvider<StatisticModel, Integer> nbAwaiting();
		
		ValueProvider<StatisticModel, Integer> nbQuestionable();
		ValueProvider<StatisticModel, Integer> nbInvalidated();
		
		ValueProvider<StatisticModel, Integer> nbTotal();
		
		
}
	
	private int idKey;
	private int statisticType;
	private String title;
	private int nbPrivateData;
	private int nbPublicData;
	private int nbReliable;
	private int nbAwaiting;
	private int nbQuestionable;
	private int nbInvalidated;
	
	

	private int nbTotal;
	
	public StatisticModel(){
		
	}
	
	public StatisticModel(int idkey, int statType,String title, int nbPrivateData, int nbPublicData,
			int nbReliable, int nbAwaiting, int nbQuestionable, int nbInvalidated){
		this.idKey = idkey;
		this.title = title;
		this.statisticType = statType;
		this.nbPrivateData = nbPrivateData;
		this.nbPublicData = nbPublicData;
		this.nbReliable = nbReliable;
		this.nbAwaiting = nbAwaiting;
		this.nbQuestionable = nbQuestionable;
		this.nbInvalidated = nbInvalidated;
		this.nbTotal = nbPrivateData+nbPublicData;
	}
	public int getIdKey() {
		return idKey;
	}
	public String getTitle() {
		return title;
	}
	public int getNbPrivateData() {
		return nbPrivateData;
	}
	public int getNbPublicData() {
		return nbPublicData;
	}
	public int getNbReliable() {
		return nbReliable;
	}
	public int getNbAwaiting() {
		return nbAwaiting;
	}
	public int getNbQuestionable() {
		return nbQuestionable;
	}
	public int getInvalidated() {
		return nbInvalidated;
	}
	public int getNbTotal() {
		return nbPrivateData+nbPublicData;
	}
	public void setIdKey(int idKey) {
		this.idKey = idKey;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setNbPrivateData(int nbPrivateData) {
		this.nbPrivateData = nbPrivateData;
	}
	public void setNbPublicData(int nbPublicData) {
		this.nbPublicData = nbPublicData;
	}
	public void setNbReliable(int nbReliable) {
		this.nbReliable = nbReliable;
	}
	public void setNbAwaiting(int nbAwaiting) {
		this.nbAwaiting = nbAwaiting;
	}
	public void setNbQuestionable(int nbQuestionable) {
		this.nbQuestionable = nbQuestionable;
	}
	
	
	public void setStatisticType(int statisticType) {
		this.statisticType = statisticType;
	}

	public int getStatisticType() {
		return statisticType;
	}
	
	public void setNbInvalidated(int nbInvalidated) {
		this.nbInvalidated = nbInvalidated;
	}

	public int getNbInvalidated() {
		return nbInvalidated;
	}
	
	
}

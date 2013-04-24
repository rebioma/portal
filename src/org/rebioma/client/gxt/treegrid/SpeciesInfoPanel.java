/**
 * 
 */
package org.rebioma.client.gxt.treegrid;

import org.apache.commons.lang.StringUtils;
import org.rebioma.client.bean.SpeciesTreeModel;

import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;

/**
 * @author Mikajy
 *
 */
public class SpeciesInfoPanel extends LayoutContainer {
	private static final String AUTHORITY_INFO_LABEL = "Authority :";
	private static final String STATUS_INFO_LABEL = "Status :";
	private static final String SYNONYMISED_TAXA_INFO_LABEL = "Synonymised taxa :";
	private static final String SOURCE_INFO_LABEL = "Source :";
	private static final String REVIEWER_NAME_INFO_LABEL = "Reviewed By :";
	private static final String VERNECULAR_NAME_INFO_LABEL = "Vernecular name :";
	
	protected Label authorityLabel = new Label(AUTHORITY_INFO_LABEL);
	protected Label statusLabel = new Label(STATUS_INFO_LABEL); 
	protected Label synomymisedTaxaLabel = new Label(SYNONYMISED_TAXA_INFO_LABEL); 
	protected Label sourceLabel = new Label(SOURCE_INFO_LABEL); 
	protected Label vernecularNameLabel = new Label(VERNECULAR_NAME_INFO_LABEL); 
	protected Label reviewerNameLabel = new Label(REVIEWER_NAME_INFO_LABEL); 
	
	protected HTML authorityHTML = new HTML();
	protected HTML statusHTML = new HTML(); 
	protected HTML synomymisedTaxaHTML = new HTML(); 
	protected HTML sourceHTML = new HTML(); 
	protected HTML vernecularNameHTML = new HTML(); 
	protected HTML reviewerNameHTML = new HTML(); 
	
	public SpeciesInfoPanel() {
		super();
		this.setStyleName("species-info-container");
	}
	@Override
	protected void onRender(Element parent, int index) {
		// TODO Auto-generated method stub
		super.onRender(parent, index);
		VerticalPanel mainVp = new VerticalPanel();
		
		HorizontalPanel authorityLigne = new HorizontalPanel();
		authorityLabel.setStyleName("specie-info-label");
		authorityHTML.setStyleName("specie-info-value");
		authorityLigne.add(authorityLabel);
		authorityLigne.add(authorityHTML);
		mainVp.add(authorityLigne);
		
		HorizontalPanel statusLigne = new HorizontalPanel();
		statusLabel.setStyleName("specie-info-label");
		statusHTML.setStyleName("specie-info-value");
		statusLigne.add(statusLabel);
		statusLigne.add(statusHTML);
		mainVp.add(statusLigne);

		HorizontalPanel synomymisedTaxaLigne = new HorizontalPanel();
		synomymisedTaxaLabel.setStyleName("specie-info-label");
		synomymisedTaxaHTML.setStyleName("specie-info-value");
		synomymisedTaxaLigne.add(synomymisedTaxaLabel);
		synomymisedTaxaLigne.add(synomymisedTaxaHTML);
		mainVp.add(synomymisedTaxaLigne);
		
		HorizontalPanel sourceLigne = new HorizontalPanel();
		sourceLabel.setStyleName("specie-info-label");
		sourceHTML.setStyleName("specie-info-value");
		sourceLigne.add(sourceLabel);
		sourceLigne.add(sourceHTML);
		mainVp.add(sourceLigne);
		
		HorizontalPanel vernecularNameLigne = new HorizontalPanel();
		vernecularNameLabel.setStyleName("specie-info-label");
		vernecularNameHTML.setStyleName("specie-info-value");
		vernecularNameLigne.add(vernecularNameLabel);
		vernecularNameLigne.add(vernecularNameHTML);
		mainVp.add(vernecularNameLigne);
		
		HorizontalPanel reviewerNameLigne = new HorizontalPanel();
		reviewerNameLabel.setStyleName("specie-info-label");
		reviewerNameHTML.setStyleName("specie-info-value");
		reviewerNameLigne.add(reviewerNameLabel);
		reviewerNameLigne.add(reviewerNameHTML);
		mainVp.add(reviewerNameLigne);
		add(mainVp);
	}
	public void updateInfo(SpeciesTreeModel model){
		if(model.getAuthorityName() != null){
			authorityHTML.setHTML(model.getAuthorityName());
		}else{
			authorityHTML.setHTML("");
		}
		if(model.getStatus() != null){
			statusHTML.setHTML(model.getStatus());
		}else{
			statusHTML.setHTML("");
		}
		if(model.getSynonymisedTaxa()!= null){
			synomymisedTaxaHTML.setHTML(model.getSynonymisedTaxa());
		}else{
			synomymisedTaxaHTML.setHTML("");
		}
		if(model.getSource()!= null){
			sourceHTML.setHTML(model.getSource());
		}else{
			sourceHTML.setHTML("");
		}
		if(model.getVernecularName()!= null){
			vernecularNameHTML.setHTML(model.getVernecularName());
		}else{
			vernecularNameHTML.setHTML("");
		}
		if(model.getReviewerName()!= null){
			reviewerNameHTML.setHTML(model.getReviewerName());
		}else{
			reviewerNameHTML.setHTML("");
		}
	}
	
	
}

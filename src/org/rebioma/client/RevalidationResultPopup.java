/**
 * 
 */
package org.rebioma.client;

import java.util.Map;
import java.util.Set;

import org.rebioma.client.bean.RevalidationResult;
import org.rebioma.client.i18n.AppConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Une instance de cette classe s'ouvre quand la revalidation est fini (reussi ou failure)
 * 
 * @author Mikajy
 *
 */
public class RevalidationResultPopup implements ClickHandler{
	
	final private VerticalPanel dialogVPanel;
	final private Button closeBtn;
	private RevalidationResult revalidationResult;
	private final DialogBox dialogBox;
	protected static final AppConstants constants = GWT.create(AppConstants.class);
	final HTML errorLabel;

	
	public RevalidationResultPopup(RevalidationResult revalidationResult) {
		super();
		// Create the popup dialog box
		this.revalidationResult = revalidationResult;
		dialogBox = new DialogBox();
		dialogBox.setAnimationEnabled(true);
		closeBtn = new Button(constants.Close());
		closeBtn.addClickHandler(this);
		// We can set the id of a widget by accessing its Element
		final HTML serverResponseLabel = new HTML();
		dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		if(this.revalidationResult != null){
			dialogVPanel.add(new HTML("Recapitulation de la r&eacute;validation"));
			dialogVPanel.add(new HTML("</br>"));
			Map<Integer, Integer> resultMap = getRevalidationResult().getResultMap(); 
			Set<Integer> keys = resultMap.keySet();
			for(Integer key: keys){
				dialogVPanel.add(new HTML("Revalidation cas " + 
						Integer.toString(key) + " :<b>" + resultMap.get(key) + " Occurences</b>"));
			}
		}
		errorLabel = new HTML();
		errorLabel.setStyleName("serverResponseLabelError");
		dialogVPanel.add(errorLabel);
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeBtn);
		dialogBox.setWidget(dialogVPanel);
	}
	
	public void show(){
		if(revalidationResult == null){
			dialogBox.setText("Erreur inconnue lors de la revalidation");
			GWT.log("Le resultat RevalidationResult est null!");
			errorLabel.setHTML("Le resultat RevalidationResult est null!");
		}else{
			if(revalidationResult.hasError()){
				GWT.log("Revalidation avec Erreur");
				dialogBox.setText("Revalidation effectuée mais avec Erreur!!");
				errorLabel.setHTML("Erreur: " + revalidationResult.getErrorMessage());
			}else{
				errorLabel.setHTML("");
				GWT.log("Revalidation success");
				dialogBox.setText("Revalidation success");
				dialogBox.setText(constants.RevalidationSuccess());
			}
		}
		dialogBox.center();
	}

	@Override
	public void onClick(ClickEvent event) {
		dialogBox.hide();
	}
	
	public RevalidationResult getRevalidationResult(){
		return revalidationResult;
	}
}

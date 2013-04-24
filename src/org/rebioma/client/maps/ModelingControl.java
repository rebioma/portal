package org.rebioma.client.maps;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.berkeley.mvz.rebioma.client.Model;
import edu.berkeley.mvz.rebioma.client.ModelService;
import edu.berkeley.mvz.rebioma.client.ModelSpec;

public class ModelingControl extends Button {
  private static final String SELECT_SPECIES = "Select Species...";
  private final DialogBox dialog;
  private final Map<String, Model> modelCache = new HashMap<String, Model>();
  private final int xOffset = 110;
  private final int yOffset = 5;

  public ModelingControl() {
    super("Models");
    dialog = new DialogBox();
    ModelService.Proxy.service().getModelNames(
        new AsyncCallback<List<String>>() {
          public void onFailure(Throwable caught) {
          }

          public void onSuccess(List<String> result) {
            loadListBox(result);
          }
        });
    addClickHandler(new ClickHandler() {
        public void onClick(ClickEvent event) {
          dialog.center();
          dialog.show();
        }
      });
  }

  public int getXOffset() {
    return xOffset;
  }

  public int getYOffset() {
    return yOffset;
  }

  protected void downloadModel(Model model) {
    Window.open(model.getDowloadUrl(), "", "");
  }

  protected void loadListBox(List<String> result) {
    if (result == null || result.isEmpty()) {
      Window.alert("No models available");
      return;
    }
    Collections.sort(result);
    final ListBox box = new ListBox();
    box.addItem(SELECT_SPECIES);
    for (String s : result) {
      box.addItem(s);
    }
    initDialogBox(box);
  }

  protected void previewModel(Model model, HTML previewImg) {
    previewImg.setHTML("<img src=\"" + model.getImageUrl()
        + "\" height=\"330\" width=\"176\">");
    previewImg.setVisible(true);
  }

  protected void viewModel(Model model) {
    Window.open(model.getHtmlLink(), "_blank", "");
  }

  private void initDialogBox(final ListBox box) {
    // Create a dialog box and set the caption text
    // final DialogBox dialogBox = new DialogBox();
    dialog.setText("Models");

    // Create a table to layout the content
    VerticalPanel dialogContents = new VerticalPanel();
    dialogContents.setSpacing(4);
    dialog.setWidget(dialogContents);

    // Add some text to the top of the dialog
    HTML details = new HTML(
        "To view future distribution models, select a species below:");
    dialogContents.add(details);
    dialogContents.setCellHorizontalAlignment(details,
        HasHorizontalAlignment.ALIGN_LEFT);

    final HTML image = new HTML("<img src=\"" + GWT.getModuleBaseURL()
        + "images/loading.gif\">");

    VerticalPanel topHp = new VerticalPanel();
    topHp.setHeight("330px");
    topHp.setSpacing(8);
    dialogContents.add(topHp);
    topHp.add(box);
    topHp.add(image);
    topHp
        .setCellHorizontalAlignment(image, HasHorizontalAlignment.ALIGN_CENTER);
    topHp.setCellVerticalAlignment(image, HasVerticalAlignment.ALIGN_MIDDLE);

    image.setVisible(false);

    final HTML previewImg = new HTML();
    topHp.add(previewImg);
    topHp.setCellHorizontalAlignment(previewImg,
        HasHorizontalAlignment.ALIGN_CENTER);
    previewImg.setVisible(false);

    // Add a close button at the bottom of the dialog
    Button closeButton = new Button("Close", new ClickHandler() {
      public void onClick(ClickEvent event) {
        dialog.hide();
      }
    });
    // Add a close button at the bottom of the dialog
    final Button downloadButton = new Button("Download", new ClickHandler() {
      public void onClick(ClickEvent event) {
        // image.setVisible(true);
        final String species = box.getItemText(box.getSelectedIndex());
        if (modelCache.containsKey(species)) {
          downloadModel(modelCache.get(species));
          return;
        } else {
          ModelSpec spec = ModelSpec.newInstance(species, null, null);
          ModelService.Proxy.service().getModel(spec,
              new AsyncCallback<Model>() {
                public void onFailure(Throwable caught) {
                }

                public void onSuccess(Model result) {
                  // image.setVisible(false);
                  modelCache.put(species, result);
                  downloadModel(result);
                }
              });
        }
      }
    });

    // Add a close button at the bottom of the dialog
    final Button viewButton = new Button("View");
    viewButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        // image.setVisible(true);
        final String species = box.getItemText(box.getSelectedIndex());
        if (modelCache.containsKey(species)) {
          viewModel(modelCache.get(species));
          return;
        }
        ModelSpec spec = ModelSpec.newInstance(species, null, null);
        ModelService.Proxy.service().getModel(spec, new AsyncCallback<Model>() {
          public void onFailure(Throwable caught) {
          }

          public void onSuccess(Model result) {
            // image.setVisible(false);
            modelCache.put(species, result);
            viewModel(result);
          }
        });
      }
    });

    box.addChangeHandler(new ChangeHandler() {
      public void onChange(ChangeEvent event) {
        viewButton.setEnabled(false);
        downloadButton.setEnabled(false);
        previewImg.setVisible(false);
        final String species = box.getItemText(box.getSelectedIndex());
        if (!species.equals(SELECT_SPECIES)) {
          if (modelCache.containsKey(species)) {
            previewModel(modelCache.get(species), previewImg);
            viewButton.setEnabled(true);
            downloadButton.setEnabled(true);
            image.setVisible(false);
            return;
          }
          image.setVisible(true);
          ModelSpec spec = ModelSpec.newInstance(species, null, null);
          ModelService.Proxy.service().getModel(spec,
              new AsyncCallback<Model>() {
                public void onFailure(Throwable caught) {
                }

                public void onSuccess(Model result) {
                  modelCache.put(species, result);
                  previewModel(result, previewImg);
                  image.setVisible(false);
                  viewButton.setEnabled(true);
                  downloadButton.setEnabled(true);
                }
              });
        } else {
          previewImg.setVisible(false);
          viewButton.setEnabled(false);
          downloadButton.setEnabled(false);

        }
      }
    });

    viewButton.setEnabled(false);
    downloadButton.setEnabled(false);
    HorizontalPanel hp = new HorizontalPanel();
    hp.setSpacing(8);
    hp.add(viewButton);
    // hp.add(downloadButton);
    hp.add(closeButton);
    dialogContents.add(hp);

    dialogContents.setCellHorizontalAlignment(closeButton,
        HasHorizontalAlignment.ALIGN_LEFT);

    // Return the dialog box
    // return dialogBox;
  }

}

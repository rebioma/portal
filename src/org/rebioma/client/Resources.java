package org.rebioma.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

public interface Resources extends ClientBundle {

    public Resources INSTANCE = GWT.create(Resources.class);

     @Source("org/rebioma/image/mf_logo.jpg")
     ImageResource MacArth_primary_logo();
     @Source("org/rebioma/image/cep_logo.jpg")
     ImageResource cepf_logo();
     @Source("org/rebioma/image/jrs_logo.jpg")
     ImageResource jrs_logo();
     @Source("org/rebioma/image/start_logo.jpg")
     ImageResource start_logo();
     @Source("org/rebioma/image/ffem_logo.jpg")
     ImageResource ffem_logo();
     @Source("org/rebioma/image/frb_logo.jpg")
     ImageResource frb_logo();
     @Source("org/rebioma/image/upload.png")
     ImageResource upload();
     @Source("org/rebioma/image/ajout.png")
     ImageResource add();
     @Source("org/rebioma/image/search.png")
     ImageResource search();
}

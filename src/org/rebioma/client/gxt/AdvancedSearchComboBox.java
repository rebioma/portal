/**
 * 
 */
package org.rebioma.client.gxt;

import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.data.client.loader.JsoReader;
import com.sencha.gxt.data.client.loader.ScriptTagProxy;
import com.sencha.gxt.data.client.writer.UrlEncodingWriter;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent.BeforeLoadHandler;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.form.ComboBox;

/**
 * @author Mikajy
 */
public class AdvancedSearchComboBox {
	
	interface Bundle extends ClientBundle {
	    @Source("AdvancedComboBox.css")
	    ExampleStyle css();
	  }
	
	  interface ExampleStyle extends CssResource {
		    String searchItem();
		  }

		  interface ExampleTemplate extends XTemplates {
		    @XTemplate("<div class='{style.searchItem}'><h3><span>{post.date:date(\"M/d/yyyy\")}<br />by {post.author}</span>{post.title}</h3>{post.excerpt}</div>")
		    SafeHtml render(Forum post, ExampleStyle style);
		  }

	  public interface Forum {
	    public String getTitle();

	    public String getTopicId();

	    public String getAuthor();
	    
	    public String getForumId();

	    public String getExcerpt();
	    
	    public String getPostId();

	    public Date getDate();

	  }

	  interface ForumCollection {
	    String getTotalCount();

	    List<Forum> getTopics();
	  }

	  interface ForumLoadConfig extends PagingLoadConfig {
	    String getQuery();

	    void setQuery(String query);

	    @Override
	    public int getOffset();

	    @Override
	    public void setOffset(int offset);
	  }

	  interface ForumListLoadResult extends PagingLoadResult<Forum> {
	    void setData(List<Forum> data);

	    @Override
	    public int getOffset();

	    @Override
	    public void setOffset(int offset);
	  }

	  interface ForumProperties extends PropertyAccess<Forum> {
	    ModelKeyProvider<Forum> topicId();

	    LabelProvider<Forum> title();
	  }
	  interface TestAutoBeanFactory extends AutoBeanFactory {
	    static TestAutoBeanFactory instance = GWT.create(TestAutoBeanFactory.class);

	    AutoBean<ForumCollection> dataCollection();

	    AutoBean<ForumListLoadResult> dataLoadResult();

	    AutoBean<ForumLoadConfig> loadConfig();
	  }

	  private ComboBox<Forum> combo;
	  
	  public Widget asWidget() {
		  String url = "http://www.sencha.com/forum/topics-remote.php";

	      ScriptTagProxy<ForumLoadConfig> proxy = new ScriptTagProxy<ForumLoadConfig>(url);
	      proxy.setWriter(new UrlEncodingWriter<ForumLoadConfig>(TestAutoBeanFactory.instance, ForumLoadConfig.class));

	      JsoReader<ForumListLoadResult, ForumCollection> reader = new JsoReader<ForumListLoadResult, ForumCollection>(
	          TestAutoBeanFactory.instance, ForumCollection.class) {
	        @Override
	        protected ForumListLoadResult createReturnData(Object loadConfig, ForumCollection records) {
	          PagingLoadConfig cfg = (PagingLoadConfig) loadConfig;
	          ForumListLoadResult res = TestAutoBeanFactory.instance.dataLoadResult().as();
	          res.setData(records.getTopics());
	          res.setOffset(cfg.getOffset());
	          res.setTotalLength(Integer.parseInt(records.getTotalCount()));
	          return res;
	        }
	      };
	      
	      PagingLoader<ForumLoadConfig, ForumListLoadResult> loader = new PagingLoader<ForumLoadConfig, ForumListLoadResult>(
	              proxy, reader);
          loader.useLoadConfig(TestAutoBeanFactory.instance.loadConfig().as());
          loader.addBeforeLoadHandler(new BeforeLoadHandler<ForumLoadConfig>() {
            @Override
            public void onBeforeLoad(BeforeLoadEvent<ForumLoadConfig> event) {
              String query = combo.getText();
              if (query != null && !query.equals("")) {
                event.getLoadConfig().setQuery(query);
              }
            }
          });
          
          ForumProperties props = GWT.create(ForumProperties.class);

          ListStore<Forum> store = new ListStore<Forum>(props.topicId());
          loader.addLoadHandler(new LoadResultListStoreBinding<ForumLoadConfig, Forum, ForumListLoadResult>(store));

          final Bundle b = GWT.create(Bundle.class);
          b.css().ensureInjected();

          final ExampleTemplate template = GWT.create(ExampleTemplate.class);

          ListView<Forum, Forum> view = new ListView<Forum, Forum>(store, new IdentityValueProvider<Forum>());
          view.setCell(new AbstractCell<Forum>() {

            @Override
            public void render(com.google.gwt.cell.client.Cell.Context context, Forum value, SafeHtmlBuilder sb) {
              sb.append(template.render(value, b.css()));
            }
          });
          

          ComboBoxCell<Forum> cell = new ComboBoxCell<Forum>(store, props.title(), view);

          combo = new ComboBox<Forum>(cell);
          combo.setLoader(loader);
          combo.setWidth(580);
          combo.setHideTrigger(true);
          combo.setPageSize(10);
          combo.addBeforeSelectionHandler(new BeforeSelectionHandler<Forum>() {
            
            @Override
            public void onBeforeSelection(BeforeSelectionEvent<Forum> event) {
              event.cancel();
              Forum f = combo.getListView().getSelectionModel().getSelectedItem();
              Window.open("http://sencha.com/forum/showthread.php?t=" + f.getTopicId() + "&p=" + f.getPostId(), null, null);
            }
          });
          
          combo.getElement().getStyle().setMargin(10, Unit.PX);
          
	      
	      return null;
	  }
	  
	  
	  
}

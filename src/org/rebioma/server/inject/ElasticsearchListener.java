/**
 * 
 */
package org.rebioma.server.inject;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.rebioma.server.elasticsearch.search.OccurrenceSearch;

/**
 * @author Mika
 *
 */
public class ElasticsearchListener  implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		OccurrenceSearch.getInstance().end();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		OccurrenceSearch.getInstance();
	}

}

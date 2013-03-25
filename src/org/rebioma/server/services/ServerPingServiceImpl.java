package org.rebioma.server.services;

import org.apache.log4j.Logger;
import org.rebioma.client.services.ServerPingService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ServerPingServiceImpl extends RemoteServiceServlet implements
		ServerPingService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5791874549747821092L;
	protected static final Logger log = Logger
			.getLogger(ServerPingServiceImpl.class);

	@Override
	public void ping() {
		log.info("Client ping me to keep session alive while revalidating");
	}
}

package dz.lab.finance.viz.websocket;

import java.util.HashSet;
import java.util.Set;

import javax.websocket.Endpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WebsocketServerConfig implements ServerApplicationConfig {

	private static final Log log = LogFactory.getLog(WebsocketServerConfig.class);
	
	public WebsocketServerConfig() {
		log.info("WebsocketServerConfig instantiated");
	}
	
	public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> endpointClasses) {
		log.info("No configuration for server endpoints to add");
		return null;
	}

	public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
		log.info("Adding annotated websocket endpoints");
		Set<Class<?>> s = new HashSet<Class<?>>();
        s.add(WebsocketServer.class);
        return s;
	}

}

package dz.lab.finance.viz;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.websocket.server.ServerContainer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ApplicationInitializer implements ServletContextListener {
	
	private static final Log log = LogFactory.getLog(ApplicationInitializer.class);
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServerContainer container = (ServerContainer) sce.getServletContext().getAttribute("javax.websocket.server.ServerContainer");
		log.debug("Creating kafka stream consumer");		
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
	}
	
}
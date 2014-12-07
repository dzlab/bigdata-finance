package dz.lab.finance.viz.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.server.standard.SpringConfigurator;

import com.google.common.eventbus.EventBus;

import dz.lab.finance.model.SessionEvent;
import dz.lab.finance.model.SessionEvent.TYPE;

@ServerEndpoint(value = "/websocket", configurator = SpringConfigurator.class)
public class WebsocketServer {
	private static final Log log = LogFactory.getLog(WebsocketServer.class);

	@Autowired
	public EventBus bus;
	
	private final Map<String, Session> sessions;

	public WebsocketServer() {		
		this.sessions = new HashMap<String, Session>();		
	}

	@OnOpen
	public void onOpen(Session session) {
		log.info("Connected ... " + session.getId());
		sessions.put(session.getId(), session);
		bus.post(new SessionEvent(TYPE.OPEN, session));
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		log.info("received: " + message);
		if (message.equals("quit")) {
			try {
				session.close(new CloseReason(CloseCodes.NORMAL_CLOSURE,
						"Game ended"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		log.info(String.format("Session %s closed because of %s",
				session.getId(), closeReason));
		sessions.remove(session.getId());
		bus.post(new SessionEvent(TYPE.CLOSE, session));
	}

}

package dz.lab.finance.viz.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

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
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import dz.lab.finance.viz.StreamConsumer;

@ServerEndpoint("/websocket")
public class WebsocketServer {
	private static final Log log = LogFactory.getLog(WebsocketServer.class);

	// @Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	private final Map<String, Session> sessions;
	private final StreamConsumer consumer;

	public WebsocketServer() {
		this.taskExecutor = new ThreadPoolTaskExecutor();
		this.sessions = new HashMap<String, Session>();
		this.consumer = new StreamConsumer();
		initialize();
	}

	public void initialize() {
		log.info("Creating ThreadPoolExecutor");
		this.taskExecutor.setBeanName("taskExecutor");
		this.taskExecutor.setCorePoolSize(4);
		this.taskExecutor.setMaxPoolSize(8);
		this.taskExecutor.setQueueCapacity(16);
		this.taskExecutor.initialize();
		taskExecutor.submit(consumer);
	}

	@OnOpen
	public void onOpen(Session session) {
		log.info("Connected ... " + session.getId());
		sessions.put(session.getId(), session);
		consumer.add(session);
	}

	@OnMessage
	public String onMessage(String message, Session session) {
		log.info("received: " + message);
		if (message.equals("quit")) {
			try {
				session.close(new CloseReason(CloseCodes.NORMAL_CLOSURE,
						"Game ended"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return message;
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		log.info(String.format("Session %s closed because of %s",
				session.getId(), closeReason));
		sessions.remove(session.getId());
		consumer.remove(session);
	}

}

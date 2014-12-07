package dz.lab.finance.model;

import javax.websocket.Session;

import lombok.ToString;

@ToString
public class SessionEvent {

	public static enum TYPE {OPEN, CLOSE}
	
	public TYPE type;
	public Session session;
	
	public SessionEvent(TYPE type, Session session) {
		this.type = type;
		this.session = session;
	}
	
}

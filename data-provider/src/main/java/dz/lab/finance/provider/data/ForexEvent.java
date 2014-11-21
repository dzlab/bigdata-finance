package dz.lab.finance.provider.data;

import java.math.BigDecimal;

public class ForexEvent {
	public final Subject subject;
	public final Context context;
	public final DirectObject directObject;
	
	public ForexEvent(String forex, String timestamp, BigDecimal conversion) {
		this.subject = new Subject(forex);
		this.context = new Context(timestamp);
		this.directObject = new DirectObject(conversion);
	}
	
	public static class Subject {
        public final String forex;
        
        public Subject(String forex) {
            this.forex = forex;
        }
    }
    
    public static class Context {
        public final String timestamp;
        
        public Context(String timestamp) {
            this.timestamp = timestamp;
        }
    }
    
    public static class DirectObject {
    	public final BigDecimal conversion;
    	
    	public DirectObject(BigDecimal conversion) {
    		this.conversion = conversion;
    	}
    }

	@Override
	public String toString() {
		return "ForexEvent [subject=" + subject.forex + ", context=" + context.timestamp
				+ ", directObject=" + directObject.conversion + "]";
	}
    
    
}

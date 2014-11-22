package dz.lab.finance.rule;

import java.math.BigDecimal;

public class Rule {
	
	public static enum Action {BUY, SELL}
	public static enum Comparaison {GRE, LE, LEQ, GREQ, EQ, NEQ}
	
	String forex;
	/**
	 * The date on milliseconds on which this rule should expire. <br/>
	 * If it is negative than the rule won't expire.
	 */
	private long expires;
	
	/**
	 * The reference value of this rule
	 */
	private BigDecimal value;
	
	/**
	 * The comparison between the conversion rate and this rule's value
	 */
	private Comparaison comparison;
	
	/**
	 * The action associated with this rule
	 */
	private Action action;
	
	public Rule(String forex, long expires, BigDecimal value, Comparaison comparison, Action action) {
		this.forex = forex;
		this.expires = expires;
		this.value = value;
		this.comparison = comparison;
		this.action = action;
	}
	
	public boolean match(String forex, BigDecimal conversion) {
		if(!this.forex.equals(forex.toLowerCase()))
			return false;
		boolean match = false;
		switch(comparison) {
			case GRE:
				match = (conversion.compareTo(value)>0 ? true: false);
				break;
			case LE:
				match = (conversion.compareTo(value)<0 ? true: false);
				break;
			case LEQ:
				match = (conversion.compareTo(value)<=0 ? true: false);
				break;
			case GREQ:
				match = (conversion.compareTo(value)>=0 ? true: false);
				break;
			case EQ:
				match = (conversion.compareTo(value)==0 ? true: false);
				break;
			case NEQ:
				match = (conversion.compareTo(value)!=0 ? true: false);
				break;
		}
		return match;
	}
	
	public boolean isExpired() {
		if(expires < -1) {
			return false;
		}
		long now = System.currentTimeMillis();
		if(now < expires) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Rule [forex=" + forex + ", expires=" + expires + ", value="
				+ value + ", comparaison=" + comparison + ", action=" + action
				+ "]";
	}
	
}

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
	long expires;
	
	BigDecimal value;
	Comparaison comparaison;
	Action action;
	
	public Rule(String forex, long expires, BigDecimal value, Comparaison comparaison, Action action) {
		this.forex = forex;
		this.expires = expires;
		this.value = value;
		this.comparaison = comparaison;
		this.action = action;
	}
	
	public boolean match(String forex, BigDecimal conversion) {
		return false;
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
				+ value + ", comparaison=" + comparaison + ", action=" + action
				+ "]";
	}
	
}

package dz.lab.finance.rule;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.apache.samza.task.WindowableTask;

import dz.lab.finance.rule.Rule.Action;
import dz.lab.finance.rule.Rule.Comparaison;

/**
 * Hello world!
 *
 */
public class ForexStreamTask implements StreamTask, InitableTask, WindowableTask {	
	
	private KeyValueStore<String, Integer> store;
	
	private Set<Rule> rules = Collections.newSetFromMap(new ConcurrentHashMap<Rule, Boolean>());
	private Set<String> ruleKeys = new HashSet<String>();
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(Config config, TaskContext context) {
		this.store = (KeyValueStore<String, Integer>) context.getStore("forex-engine");
		this.rules.add(new Rule("eurusd", System.currentTimeMillis() + 1000*60*5, new BigDecimal("1.24"), Comparaison.LE, Action.BUY));
		this.rules.add(new Rule("eurusd", -1, new BigDecimal("1.25"), Comparaison.GRE, Action.SELL));
	}

	@Override
	public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
			TaskCoordinator coordinator) throws Exception {
		// extract information from the current event
		Map<String, Object> event = (Map<String, Object>) envelope.getMessage();
		Map<String, Object> subject = (Map<String, Object>) event.get("subject");
		String forex = (String) subject.get("forex");
		Map<String, Object> directObject = (Map<String, Object>) event.get("directObject");
		BigDecimal conversion = new BigDecimal((String)directObject.get("conversion"));
		
		// check if any rule applies
		for(Rule rule: rules) {
			if(rule.isExpired()) {
				System.out.println("Expired rule: " + rule);
				rules.remove(rule);
				// send out information on expired rules
				collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "forex-rules-stats"), rule));
				continue;
			}
			if(!rule.match(forex, conversion)) {
				continue;
			}
			System.out.println("Matched rule: " + rule + " with (" + forex + ", " + conversion+")");
			String ruleKey = rule.toString();
			Integer count = store.get(ruleKey);
			if(count == null) {
				count = Integer.valueOf(0);
			}
			store.put(ruleKey, ++count);
			ruleKeys.add(ruleKey);
		}
	}

	@Override
	public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		Map<String, Integer> matches = new HashMap<String, Integer>();
	    for (String ruleKey : ruleKeys) {
	    	matches.put(ruleKey, store.get(ruleKey));
	    	store.delete(ruleKey);
	    }
		// send out the number of matches for the different rules
		collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "forex-rules-stats"), matches));		
	}
}

package dz.lab.finance.viz;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.websocket.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;

import dz.lab.finance.model.Constants;
import dz.lab.finance.model.ForexEvent;
import dz.lab.finance.model.SessionEvent;
import dz.lab.finance.util.KafkaProperties;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

/**
 * A Kafka consumer
 * @author dzlab
 */
public class StreamConsumer implements Runnable {
	
	private static final Log log = LogFactory.getLog(StreamConsumer.class);
	
	@Autowired
	public Gson gson;
	
	@Autowired
	public EventBus bus;
		
	private AtomicBoolean stop;
	private ConsumerConnector consumer;
	private Map<String, Integer> topicCountMap;
	private List<Session> sessions;
	private List<String> messages;
	
	public StreamConsumer()	{
		this.stop = new AtomicBoolean(false);
		this.topicCountMap = new HashMap<String, Integer>();
		ConsumerConfig config = createConfig();
		this.consumer = Consumer.createJavaConsumerConnector(config);
		this.sessions = Collections.synchronizedList(new LinkedList<Session>());
		this.messages = new LinkedList<String>();
	}

	public void subscribe(String stream) {		
		this.topicCountMap.put(stream, Integer.valueOf(1));		
	}
	
	@PostConstruct
	public void start() throws Exception {
	  this.bus.register(this);
	}
 
	@PreDestroy
	public void stop() throws Exception {
		this.bus.unregister(this);
	}
	
	@Subscribe
	public void handleSessionEven(SessionEvent event) {
		log.info("Handling session event: "+ event);
		switch (event.type) {
		case OPEN:  // Registering a session
			this.sessions.add(event.session);
			break;

		case CLOSE: // Un-Registering a session
			this.sessions.remove(event.session);
			break;
		}
	}

	private ConsumerConfig createConfig() {
		log.info("Loading kafka consumer configuration from 'kafka.properties'");
		Properties props = new Properties();
		props.put("zookeeper.connect", KafkaProperties.zkConnect);
	    props.put("group.id", KafkaProperties.groupId);
	    props.put("zookeeper.session.timeout.ms", "400"); 
 	    props.put("zookeeper.sync.time.ms", "200");
	    props.put("autocommit.interval.ms", "1000");
		ConsumerConfig config = new ConsumerConfig(props);
		return config;
	}
	
	public ConsumerIterator<byte[], byte[]> iterator() {
		log.info("Getting an iterator over the "+Constants.FOREX_STREAM+" stream.");
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		KafkaStream<byte[], byte[]> stream = consumerMap.get(Constants.FOREX_STREAM).get(0);
		ConsumerIterator<byte[], byte[]> it = stream.iterator();
		return it;
	}

	@Override
	public void run() {
		ConsumerIterator<byte[], byte[]> it = iterator();
		while(it.hasNext()) {
			try{
				byte[] bytes = it.next().message();
				
				String json = new String(bytes, "UTF-8");
				ForexEvent event = gson.fromJson(json, ForexEvent.class);
				log.info("Retrieved data from stream: "+event);
				if("eurusd".equals(event.subject.forex.toLowerCase()))
				{
					for(Session session: sessions) {
						String data = "{'label': 'eurusd', 'conversion': "+event.directObject.conversion+"}";
						session.getBasicRemote().sendText(data);
					}					
				}				
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		log.debug("Stopping consumer.");
	}
}

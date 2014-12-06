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

import javax.websocket.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;


public class StreamConsumer implements Runnable {
	
	private static final Log log = LogFactory.getLog(StreamConsumer.class);
	
	private static final String FOREX_EVENTS = "forex_events";
	
	
	
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
	
	public void add(Session session) {
		this.sessions.add(session);
	}
	
	public void remove(Session session) {
		this.sessions.remove(session);
	}

	private ConsumerConfig createConfig() {
		log.info("Loading kafka consumer configuration from 'kafka.properties'");
		Properties props = new Properties();
		InputStream input = getClass().getClassLoader().getResourceAsStream("kafka.properties");
		try
		{
			props.load(input);
		}
		catch(IOException exception)
		{
			exception.printStackTrace();
		}
		ConsumerConfig config = new ConsumerConfig(props);
		return config;
	}
	
	public ConsumerIterator<byte[], byte[]> iterator() {
		log.info("Getting an iterator over the "+FOREX_EVENTS+" stream.");
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		KafkaStream<byte[], byte[]> stream = consumerMap.get(FOREX_EVENTS).get(0);
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
				System.out.println("Retrieved data from stream: "+json);
				/*messages.add(json);
				if(messages.size() == 50) {
					messages.clear();
				}*/
				for(Session session: sessions) {
					session.getBasicRemote().sendText(json);
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
			
	}
}

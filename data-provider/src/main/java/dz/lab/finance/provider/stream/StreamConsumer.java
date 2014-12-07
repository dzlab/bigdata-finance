package dz.lab.finance.provider.stream;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jsoup.helper.StringUtil;

import com.google.gson.Gson;

import dz.lab.finance.provider.common.LifeCycle;
import dz.lab.finance.model.ForexEvent;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class StreamConsumer implements LifeCycle {
	
	private static final String FOREX_EVENTS = "forex_events";
	private static final String FOREX_RULES_STATS = "forex-rules-stats";
	
	public static void main(String[] args) throws InterruptedException {	
		StreamConsumer streamConsumer = new StreamConsumer();
		//streamConsumer.init(FOREX_EVENTS);
		streamConsumer.init(FOREX_RULES_STATS);
		streamConsumer.start();
		Thread.sleep(1000);
		streamConsumer.stop();		
	}
	
	private AtomicBoolean stop;
	private Gson gson;
	private ConsumerConnector consumer;
	private String stream;
	private Map<String, Integer> topicCountMap;
	
	public StreamConsumer()	{
		this.gson = new Gson();
		this.stop = new AtomicBoolean(false);
		this.topicCountMap = new HashMap<String, Integer>();
		ConsumerConfig config = createConfig();
		this.consumer = Consumer.createJavaConsumerConnector(config);
	}
	
	public void init(String stream) {
		this.stream = stream;
		topicCountMap.put(this.stream, Integer.valueOf(1));		
	}	
	
	public void consume(KafkaStream<byte[], byte[]> stream) {
		ConsumerIterator<byte[], byte[]> it;
		while(true) {
			it = stream.iterator();
			while(it.hasNext() && !stop.get()) {
				try{
					byte[] bytes = it.next().message();
					String json = new String(bytes, "UTF-8");
					if(this.stream.equals(FOREX_EVENTS)) {
						System.out.println(gson.fromJson(json, ForexEvent.class));						
					}else {
						System.out.println("Event: " + json + ": bytes length: " + bytes.length);
					}
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}	

	private ConsumerConfig createConfig() {
		Properties props = new Properties(); 
		props.put("zookeeper.connect", "192.168.2.72:2181");
		props.put("group.id", "group1");
		props.put("zookeeper.session.timeout.ms", "400");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "1000");
		
		return new ConsumerConfig(props);
	}

	@Override
	public void start() {
		this.stop.set(false);
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		KafkaStream<byte[], byte[]> stream = consumerMap.get(this.stream).get(0);
		
		consume(stream);
	}

	@Override
	public void stop() {
		this.stop.set(true);
	}

}

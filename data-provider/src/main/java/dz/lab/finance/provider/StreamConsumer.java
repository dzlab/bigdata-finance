package dz.lab.finance.provider;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jsoup.helper.StringUtil;

import com.google.gson.Gson;

import dz.lab.finance.provider.data.ForexEvent;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class StreamConsumer {
	
	private static final String STREAM = "forex_events";
	
	public static void main(String[] args) {
		Gson gson = new Gson();
		ConsumerIterator<byte[], byte[]> it;
		
		ConsumerConfig config = createConfig();
		ConsumerConnector consumer = Consumer.createJavaConsumerConnector(config);
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(STREAM, Integer.valueOf(1));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		KafkaStream<byte[], byte[]> stream = consumerMap.get(STREAM).get(0);
		while(true) {
			it = stream.iterator();
			while(it.hasNext()) {
				try{
					byte[] bytes = it.next().message();
					String json = new String(bytes, "UTF-8");
					System.out.println(gson.fromJson(json, ForexEvent.class));	
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static ConsumerConfig createConfig() {
		Properties props = new Properties(); 
		props.put("zookeeper.connect", "192.168.2.72:2181");
		props.put("group.id", "group1");
		props.put("zookeeper.session.timeout.ms", "400");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "1000");
		
		return new ConsumerConfig(props);
	}

}

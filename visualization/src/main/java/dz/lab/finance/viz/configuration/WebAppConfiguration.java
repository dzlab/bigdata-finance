package dz.lab.finance.viz.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dz.lab.finance.model.Constants;
import dz.lab.finance.viz.StreamConsumer;

@EnableWebMvc
@Configuration
@ComponentScan("dz.lab.finance.viz")
public class WebAppConfiguration {
	
	private static final Log log = LogFactory.getLog(WebAppConfiguration.class);

	@Bean
	@Description("Kafka producer")
	public Producer<String, String> producer() {
		log.info("Creating a Kafka producer");
		Properties props = new Properties();		
		try{
			InputStream in = getClass().getClassLoader().getResourceAsStream("kafka.properties");
			props.load(in);
		}catch(IOException e) {
			e.printStackTrace();
		}
        ProducerConfig config = new ProducerConfig(props);
        return new Producer<String, String>(config);
	}
	
	@Bean
	@Description("JSON serializer")
	public Gson gson() {
        return new GsonBuilder().create();
    }
	
	@Bean
	@Description("Spring's thread pool")
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor(); 
		taskExecutor.setBeanName("taskExecutor");
		taskExecutor.setCorePoolSize(4);
		taskExecutor.setMaxPoolSize(8);
		taskExecutor.setQueueCapacity(16);
		taskExecutor.initialize();
		return taskExecutor;
	}
	
	@Bean
	@Description("Kafka consumer")
	public StreamConsumer consumer() {
		log.info("Creating a Kafka consumer");
		StreamConsumer consumer = new StreamConsumer();
		consumer.subscribe(Constants.FOREX_STREAM);
		ThreadPoolTaskExecutor taskExecutor = taskExecutor();
		taskExecutor.submit(consumer);
		return consumer;
	}
	
	@Bean
	@Description("Internal event bus")
	public EventBus eventBus() { 
		return new EventBus(); 		
	}
}

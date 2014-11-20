package dz.lab.finance.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;

import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dagger.Module;
import dagger.Provides;

@Module(injects = App.class, library=true)
public class ApplicationModule {
	
	@Provides 
	@Singleton
    Gson provideGson() {
        return new GsonBuilder().create();
    }
	
	@Provides 
	@Singleton
	EventBus provideBus() { 
		return new EventBus(); 		
	}
	/*
	@Provides 
	@Singleton
    HistDataFileProcessor provideProcessor() {
        return new HistDataFileProcessor();
    }
	
	@Provides 
	@Singleton
	HistDataService provideDataService() {
		return new HistDataService();
	}
	*/
	@Provides 
	@Singleton
	ExecutorService provideExecutor() {
		int cores = Runtime.getRuntime().availableProcessors();
		return Executors.newFixedThreadPool(cores);
	}
	
	@Provides
	@Singleton
	Producer<String, String> provideProducer() {
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
}

package dz.lab.finance.provider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import com.google.common.eventbus.EventBus;

import dagger.Module;
import dagger.Provides;

@Module(injects = App.class, library=true)
public class ApplicationModule {
	
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
}

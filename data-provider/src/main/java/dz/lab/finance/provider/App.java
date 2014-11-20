package dz.lab.finance.provider;

import java.io.IOException;

import dagger.Module;
import dagger.ObjectGraph;

/**
 * Hello world!
 *
 */
public class App {
	
	@Module(includes = ApplicationModule.class, injects = {HistDataService.class,HistDataFileProcessor.class})
	static class AppModule {
		
	}
	
	public static void main(String[] args) throws IOException {
		
		ObjectGraph objectGraph = ObjectGraph.create(new AppModule());
		HistDataService service = objectGraph.get(HistDataService.class);
		HistDataFileProcessor processor = objectGraph
				.get(HistDataFileProcessor.class);
		processor.start();		
		service.start();
		System.out.println("Type enter to leave.");
		System.in.read();
		processor.stop();
		service.stop();
	}
}

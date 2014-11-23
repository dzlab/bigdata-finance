package dz.lab.finance.provider;

import java.io.IOException;

import dagger.Module;
import dagger.ObjectGraph;

/**
 * Hello world!
 *
 */
public class App {
	
	@Module(includes = ApplicationModule.class, injects = {ForexService.class,ForexFileProcessor.class})
	static class AppModule {
		
	}
	
	public static void main(String[] args) throws IOException {
		
		ObjectGraph objectGraph = ObjectGraph.create(new AppModule());
		ForexService service = objectGraph.get(ForexService.class);
		ForexFileProcessor processor = objectGraph
				.get(ForexFileProcessor.class);
		processor.start();		
		service.start();
		System.out.println("Type enter to leave.");
		System.in.read();
		processor.stop();
		service.stop();
	}
}

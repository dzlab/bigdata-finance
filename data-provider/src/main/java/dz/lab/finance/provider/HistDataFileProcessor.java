package dz.lab.finance.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.inject.Inject;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class HistDataFileProcessor implements LifeCycle {

	@Inject
	EventBus bus;
	@Inject
	ExecutorService executor;

	public HistDataFileProcessor() {
	}

	public void process(String filename) throws IOException {
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(filename);

			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (!entry.getName().endsWith("csv")) {
					continue;
				}
				System.out.println("entry: " + entry.getName());
				CSVReader reader = null;
				try {
					InputStream stream = zipFile.getInputStream(entry);	
					reader = new CSVReader(new InputStreamReader(stream), ';');
					String [] nextLine;
					while ((nextLine = reader.readNext()) != null) {
						String timestamp = nextLine[0];
						BigDecimal conversion = new BigDecimal(nextLine[1]);
						System.out.println(timestamp + ", " + conversion);
					}
					System.out.println("Finished processing: " + entry.getName());
				}finally {
					reader.close();
				}
				
			}		
		} finally {
			zipFile.close();
		}
	}

	@Subscribe
	public void handleFileDownload(FileDownloadedEvent event) {
		System.out.println("Handling " + event);
		final String filename = event.getFilename();
		executor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					process(filename);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});
	}

	@Override
	public void start() {
		bus.register(this);
	}

	@Override
	public void stop() {
		bus.unregister(this);
	}

}

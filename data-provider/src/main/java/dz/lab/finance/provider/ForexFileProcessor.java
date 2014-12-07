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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.producer.KeyedMessage;
import kafka.javaapi.producer.Producer;
import au.com.bytecode.opencsv.CSVReader;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;

import dz.lab.finance.model.ForexEvent;
import dz.lab.finance.provider.common.LifeCycle;
import dz.lab.finance.provider.data.FileDownloadedEvent;

public class ForexFileProcessor implements LifeCycle {
	private static Logger logger = LoggerFactory
			.getLogger(ForexFileProcessor.class);

	private static final String STREAM = "forex_events";
	
	@Inject
	Gson gson;

	@Inject
	EventBus bus;

	@Inject
	ExecutorService executor;

	@Inject
	Producer<String, String> producer;

	public ForexFileProcessor() {
	}

	private void handleCSVFileStream(String forex, InputStream stream) throws IOException {
		CSVReader reader = new CSVReader(new InputStreamReader(stream), ';');
		String[] nextLine;
		try {

			while ((nextLine = reader.readNext()) != null) {
				String timestamp = nextLine[0];
				String conversion = nextLine[1];
				ForexEvent event = new ForexEvent(forex, timestamp, conversion);
				KeyedMessage<String, String> data = new KeyedMessage<String, String>(
						STREAM, timestamp, gson.toJson(event));
				producer.send(data);
			}
		} finally {
			reader.close();
		}
	}

	/**
	 * Process a zip file
	 */
	public void process(String filename) throws IOException {
		String[] parts = filename.split("_");
		String forex = parts[3];
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(filename);

			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (!entry.getName().endsWith("csv")) {
					continue;
				}
				InputStream stream = zipFile.getInputStream(entry);
				handleCSVFileStream(forex, stream);
				logger.info("Finished processing: " + entry.getName());
			}
		}catch(Exception e) {
			e.printStackTrace();
		} finally {
			zipFile.close();
		}
	}

	@Subscribe
	public void handleFileDownload(FileDownloadedEvent event) {
		logger.info("Handling " + event);
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

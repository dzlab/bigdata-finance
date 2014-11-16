package dz.lab.finance.provider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

import com.google.common.eventbus.EventBus;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

public class HistDataService implements LifeCycle {
	private final RestAdapter restAdapter;
	private final HistDataEndpoint endpoint;
	private final Queue<HistDataForm> queries;
	@Inject EventBus bus;
	
	
	public HistDataService() {
		// Define the interceptor, add authentication headers
		RequestInterceptor requestInterceptor = new RequestInterceptor() {
			@Override
			public void intercept(RequestFacade request) {
				request.addHeader(
						"User-Agent",
						"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Dragon/33.1.0.0 Chrome/33.0.1750.152 Safari/537.36");				
				request.addHeader("Cookie",
						"__cfduid=dbf4a6f91ce6429129dec7554592334c21416137709; visited=yes");
				if(queries.isEmpty()) 
					return;
				// add a Referer header
				HistDataForm form = queries.poll();
				String datemonth = form.datemonth;
				String year = datemonth.substring(0, 4);
				String month = datemonth.substring(4, 6);
				request.addHeader(
						"Referer",
						"http://www.histdata.com/download-free-forex-historical-data/?/ninjatrader/tick-last-quotes/eurusd/"+year+"/"+month);
			}
		};
		this.restAdapter = new RestAdapter.Builder()
				.setEndpoint("http://www.histdata.com")
				.setRequestInterceptor(requestInterceptor).build();
		this.endpoint = restAdapter.create(HistDataEndpoint.class);
		this.queries = new ConcurrentLinkedQueue<>();
	}
	
	public void start() {
		bus.register(this);
		OkHttpClient client = new OkHttpClient();		
		HistDataCallback callback = new HistDataCallback(this);
		int year = 2014;
		for(int month=1; month<=1; month++) {
			String url = new StringBuilder("http://www.histdata.com/download-free-forex-historical-data/?/ninjatrader/tick-last-quotes/eurusd/").append(year).append('/').append(month).toString();
			Request request = new Request.Builder()
		      .url(url)
		      .build();
			client.newCall(request).enqueue(callback);			
		}
	}
	
	public void request(final String tk, final String date, final String datemonth, final String platform, final String timeframe, final String fxpair) {
		queries.add(new HistDataForm(tk, date, datemonth, platform, timeframe, fxpair));
		endpoint.getForex(tk, date, datemonth, platform, timeframe, fxpair, new Callback<Response>() {
			
			@Override
			public void success(Response t, Response response) {
				try {
					InputStream body = response.getBody().in();
					String tempDir = System.getProperty("java.io.tmpdir");
					String filename = new StringBuilder("HISTDATA_COM_NT_").append(fxpair).append(timeframe).append(datemonth).append(".zip").toString();
					File file = new File(tempDir, filename);					 
				    FileUtils.copyInputStreamToFile(body, file);	
				    bus.post(produceFileEvent(file.getAbsolutePath()));
					System.out.println("Successfully stored data to: " + file.getAbsolutePath());
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void failure(RetrofitError error) {
				System.out.println("Failed: ");
				error.printStackTrace();
			}
		});
	}
	
	

	private static interface HistDataEndpoint {
		@FormUrlEncoded
		@POST("/get.php")
		void getForex(@Field("tk") String tk, @Field("date") String date,
				@Field("datemonth") String datemonth,
				@Field("platform") String platform,
				@Field("timeframe") String timeframe,
				@Field("fxpair") String fxpair, Callback<Response> callback);
		
		/**
		 * Extract the value of the field 'tk' for a given year and month
		 * @param callback
		 */
		@GET("/download-free-forex-historical-data/?/ninjatrader/tick-last-quotes/eurusd/{year}/{month}")
		void getTk(@Path("year") int year, @Path("month") int month, Callback<Response> callback);
	}

	 
	public FileDownloadedEvent produceFileEvent(String filename) {
	    return new FileDownloadedEvent(filename);
	}

	@Override
	public void stop() {
		bus.unregister(this);
	}
}

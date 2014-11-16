package dz.lab.finance.provider;

import java.io.IOException;

import javax.inject.Inject;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class HistDataCallback implements Callback {
	
	HistDataService service;
		 
	public HistDataCallback(HistDataService service) {
		this.service = service;
	}

	@Override
	public void onFailure(Request request, IOException e) {
		e.printStackTrace();
	}

	@Override
	public void onResponse(Response response) throws IOException {
		String html = response.body().string();
		HistDataParser parser = new HistDataParser(html);
		String tk = parser.getTk();
		String date = parser.getDate();
		String datemonth = parser.getDatemonth();
		String platform = parser.getPlatform();
		String timeframe = parser.getTimeframe();
		String fxpair = parser.getFxpair();
		service.request(tk, date, datemonth, platform, timeframe, fxpair);
	}
	
}

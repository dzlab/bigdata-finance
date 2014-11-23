package dz.lab.finance.provider;

import java.io.IOException;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import dz.lab.finance.provider.parser.HistDataParser;

public class ForexFileRequest implements Callback {

	ForexService service;
	OkHttpClient client;

	public ForexFileRequest(ForexService service) {
		this.service = service;
		this.client = new OkHttpClient();
	}

	public void submit(String fxpair, int year, int month) {
		String url = new StringBuilder(
				"http://www.histdata.com/download-free-forex-historical-data/?/ninjatrader/tick-last-quotes/")
				.append(fxpair).append('/')
				.append(year).append('/')
				.append(month).toString();
		Request request = new Request.Builder().url(url).build();
		client.newCall(request).enqueue(this);
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
		service.handleForexForm(tk, date, datemonth, platform, timeframe, fxpair);
	}

}

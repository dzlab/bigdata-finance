package dz.lab.finance.provider;

import java.io.IOException;
import java.util.List;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import dz.lab.finance.provider.data.ForexPair;
import dz.lab.finance.provider.parser.ForexPairsPageParser;

/**
 * Performs an HTTP request to <a href="http://www.histdata.com/download-free-forex-data/?/ninjatrader/tick-last-quotes">histdata.com</a>
 * to extract the different available forex pairs
 */
public class ForexPairsRequest implements Callback {

	ForexService service;
	OkHttpClient client;

	public ForexPairsRequest(ForexService service) {
		this.service = service;
		this.client = new OkHttpClient();
	}
	
	public void submit() {
		String url = "http://www.histdata.com/download-free-forex-data/?/ninjatrader/tick-last-quotes";
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
		ForexPairsPageParser parser = new ForexPairsPageParser(html);
		List<ForexPair> pairs = parser.getForexPairs();
		service.handleForexPairs(pairs);
	}

}

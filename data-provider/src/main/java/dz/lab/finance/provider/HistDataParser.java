package dz.lab.finance.provider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class HistDataParser {
	private final Document doc;
	
	public HistDataParser(String html) {
		this.doc = Jsoup.parse(html);
	}
	
	public String getTk() {
		String tk = extract("tk");
		return tk;
	}
	
	public String getDate() {
		String date = extract("date");
		return date;
	}
	
	public String getDatemonth() {
		String datemonth = extract("datemonth");
		return datemonth;
	}
	
	public String getPlatform() {
		String platform = extract("platform");
		return platform;
	}
	
	public String getTimeframe() {
		String timeframe = extract("timeframe");
		return timeframe;
	}
	
	public String getFxpair() {
		String fxpair = extract("fxpair");		
		return fxpair;
	}

	private String extract(String id) {
		Elements els = doc.select("input#" + id);
		String value = els.get(0).attr("value");
		return value;
	}
	
}

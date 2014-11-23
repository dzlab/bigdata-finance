package dz.lab.finance.provider.parser;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import dz.lab.finance.provider.data.ForexPair;

/**
 * A parser class for http://www.histdata.com/download-free-forex-data/?/ninjatrader/tick-last-quotes
 */
public class ForexPairsPageParser {
	private final Elements tds;
	
	public static final Map<String, Integer> MONTHS = new HashMap<>();
	static {
		MONTHS.put("January", 1);
		MONTHS.put("February", 2);
		MONTHS.put("March", 3);
		MONTHS.put("April", 4);
		MONTHS.put("May", 5);
		MONTHS.put("June", 6);
		MONTHS.put("July", 7);
		MONTHS.put("August", 8);
		MONTHS.put("September", 9);
		MONTHS.put("October", 10);
		MONTHS.put("November", 11);
		MONTHS.put("December", 12);
	}
	
	public ForexPairsPageParser(String html) {
		this(Jsoup.parse(html));
	}
	
	public ForexPairsPageParser(Document doc) {		
		this.tds = doc.select("td");
	}
	
	/**
	 * 
	 * @return the {@link List} of extracted {@link ForexPair}s.
	 */
	public List<ForexPair> getForexPairs() {
		List<ForexPair> pairs = new LinkedList<>();
		for(Element el: tds) {
			if(!el.child(0).tagName().equals("a")) {
				continue;
			}
			
			// extract the Forex pair
			String fx = el.child(0).child(0).text().replace("/", "");
			
			// extract the starting date
			String html = el.html();
			int beginIndex = html.indexOf('(') + 1;
			int endIndex = html.indexOf(')');
			String[] yearmonth = html.substring(beginIndex, endIndex).split("/");
			int year = Integer.valueOf(yearmonth[0]);
			int month = MONTHS.get(yearmonth[1]);
			ForexPair fxpair = new ForexPair(fx, year, month);
			pairs.add(fxpair);			
		}
		return pairs;
	}
	
	public static void main(String[] args) throws IOException {
		Document doc = Jsoup.connect("http://www.histdata.com/download-free-forex-data/?/ninjatrader/tick-last-quotes").get();
		ForexPairsPageParser parser = new ForexPairsPageParser(doc);
		List<ForexPair> pairs = parser.getForexPairs();
		
		for(ForexPair fxpair: pairs) {
			System.out.println(fxpair);	
		}
	}
	
}

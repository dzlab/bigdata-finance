package dz.lab.finance.provider.data;

public class HistDataForm {

	String tk;
	String date;
	String datemonth;
	String platform;
	String timeframe;
	String fxpair;

	public HistDataForm(String tk, String date, String datemonth, String platform, String timeframe, String fxpair) {
		this.tk = tk;
		this.date = date;
		this.datemonth = datemonth;
		this.platform = platform;
		this.timeframe = timeframe;
		this.fxpair = fxpair;
	}
	
	public String getDatemonth() {
		return datemonth;
	}
}

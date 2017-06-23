package central;

import java.util.Date;

import core.GeoLocation;

public class LotEntry {
	public String name;
	public String url;
	public GeoLocation location;
	public Date arrivalTime;
	
	public LotEntry() {}
	
	public LotEntry(String name, String url, GeoLocation location) {
		this.name = name;
		this.url = url;
		this.location = location;
		this.arrivalTime = new Date();
	}
	
	public boolean isEntryValid() {
		return (new Date().getTime() - arrivalTime.getTime() < (1000 * 60 * 5 )); // 5 minutes
	}
}

package central;

import core.GeoLocation;

public class LotEntryRequest {
	public String name;
	public String url;
	public String token;
	public GeoLocation location;
	
	public LotEntryRequest() {}
	
	public LotEntryRequest(String name, String url, String token, GeoLocation location) {
		this.name = name;
		this.url = url;
		this.token = token;
		this.location = location;
	}
}

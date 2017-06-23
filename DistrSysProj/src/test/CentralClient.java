package test;

import org.restlet.resource.ClientResource;

import com.google.gson.Gson;

import central.LotEntryRequest;
import core.GeoLocation;

public class CentralClient {
	public static void main(String[] args) throws Exception {
		Gson gson = new Gson();
		
		new ClientResource("http://localhost:8080/lots").get().write(System.out);
		
		LotEntryRequest req = new LotEntryRequest("test", "http://somethi.ng/park123", "secretToken", new GeoLocation(0,0));
		new ClientResource("http://localhost:8080/lots").post(gson.toJson(req)).write(System.out);

		new ClientResource("http://localhost:8080/lots").get().write(System.out);
	
	}
}

package central;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.routing.Router;
import org.restlet.service.CorsService;

import com.google.gson.Gson;

import core.GeoLocation;

public class Deploy extends Application {
	List<String> tokens;
	Map<String, LotEntry> lots;
	Gson gson = new Gson();
	
	public static void main(String[] args) throws Exception {
		Component component = new Component();
		component.getServers().add(Protocol.HTTP, 8080);
		component.getClients().add(Protocol.HTTP);
		component.getDefaultHost().attach("/lots", new Deploy());
		component.start();
	}
	
	public Deploy() {
		CorsService corsService = new CorsService();         
	    corsService.setAllowedOrigins(new HashSet<String>(Arrays.asList("*")));
	    corsService.setAllowedCredentials(true);
	    getServices().add(corsService);
		
		tokens = new ArrayList<String>();
		lots = new HashMap<String, LotEntry>();
		
		tokens.add("SecretToken"); // tehe
	}
	
	public Restlet createInboundRoot() {
		Router r = new Router(getContext());
		r.attach("/list", new Restlet() {
			@Override
			public void handle(Request req, Response res) {
				if( req.getMethod().equals(Method.GET)) {
					//Return the best spot to javascript script
					double lat, lon;
					try {
						lat = Double.parseDouble(					
							req.getOriginalRef().getQueryAsForm().getFirst("latitude").getValue());						
						lon = Double.parseDouble(
							req.getOriginalRef().getQueryAsForm().getFirst("longitude").getValue());
					} catch(NullPointerException e) {
						lat = 53.307665;
						lon = -6.21679;
					}
					GeoLocation clientpos = new GeoLocation(lat, lon);
					List<LotEntry> nears = new ArrayList<LotEntry>();
					for(LotEntry e: lots.values()){
						if( e.isEntryValid() && e.location.distanceFrom(clientpos) < 30 /* ~50km */){
							nears.add(e);
						}
					}
					res.setEntity(gson.toJson(nears), MediaType.TEXT_PLAIN);
					
				} if( req.getMethod().equals(Method.POST)) {
					//Register a parking lots from a central server
					LotEntryRequest newEntry = gson.fromJson(req.getEntityAsText(), LotEntryRequest.class);
					if( tokens.contains(newEntry.token)) {
						lots.put(newEntry.name, new LotEntry(newEntry.name, newEntry.url, newEntry.location));
						res.setStatus(Status.SUCCESS_OK);
					} else {
						res.setStatus(Status.CLIENT_ERROR_FORBIDDEN, "Token not valid");
					}
				}
			}
		});
		r.attach("/list/{id_ParkingLot}", new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				String id = (String) request.getAttributes().get("id_ParkingLot");
				//POST Request: Get a specific parking lot
				if (request.getMethod().equals(Method.GET)){
					if(lots.containsKey(id)){
						response.setEntity(gson.toJson(lots.get(id)), MediaType.TEXT_PLAIN);
					}
					else{
						response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
					}
				}
				
				//Modify parking lot information
				if (request.getMethod().equals(Method.PUT)){
					if(lots.containsKey(id)){
						String json = request.getEntityAsText();
						LotEntryRequest L=gson.fromJson(json, LotEntryRequest.class);
						if( L.name.equals(id)){
							lots.replace(id, new LotEntry(L.name, L.url, L.location));
						}
						else{
							response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
						}
						
					}
					else{
						response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
					}
				}
				
				//Delete a parking lot
				if(request.getMethod().equals(Method.DELETE)){
					if(lots.containsKey(id)){
						lots.remove(id);
					}
					else{
						response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
					}
				}
				
				//Other Request: not handled
				else{
					response.setStatus(Status.CLIENT_ERROR_FORBIDDEN);
				}
			}
		});
		
		return r;
	}
}

package middle;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.resource.ClientResource;
import org.restlet.routing.Router;
import org.restlet.service.CorsService;

import com.google.gson.Gson;

import central.LotEntryRequest;
import core.GeoLocation;


public class Deploy extends Application{
	public SpotFactory factory;
	public Gson gson;
	
	
	public static void main(String[] args) throws Exception {
		String name = args[0];
		int port = Integer.parseInt(args[1]);
		double lat  = Double.parseDouble(args[2]);
		double lon  = Double.parseDouble(args[3]);
		int rmiPort = Integer.parseInt(args[4]);
		new Deploy(name, port, lat, lon, rmiPort);
	}
	
	public Deploy(String name, int port, double lat, double lon, int rmiPort) throws Exception {
		this.gson = new Gson();	
		this.factory=new SpotFactory();
		CorsService corsService = new CorsService();         
	    corsService.setAllowedOrigins(new HashSet<String>(Arrays.asList("*")));
	    corsService.setAllowedCredentials(true);
	    getServices().add(corsService);
	    	    
	    // init the rmi interface:
 		Registry registry = LocateRegistry.createRegistry(rmiPort); 		 		
 		registry.rebind("Factory", UnicastRemoteObject.exportObject(factory ,0));
 		 		
 		// init the rest interface:
 		Component component = new Component(); 
 		component.getServers().add(Protocol.HTTP, port);
 		component.getClients().add(Protocol.HTTP);
 		component.getDefaultHost().attach("", this);
 		component.start();
 		 		
 		// bind with centralserver:
 		ExecutorService pool = Executors.newFixedThreadPool(5);
 		Runnable thread = new Runnable() {
 			String name;
 			String token;
 			double lat;
 			double lon;
 			int port;
 			Gson gson;
 			
 			public void run() {
 				while(true) {
	 				LotEntryRequest req = new LotEntryRequest(name, "http://127.0.0.1:"+port+"/spot", token, new GeoLocation(lat, lon));
	 		 		new ClientResource("http://127.0.0.1:8080/lots/list").post(this.gson.toJson(req));
	 		 		try {
						Thread.sleep(1000 * 60 ); // every minute
					} catch (InterruptedException e) {
						// it's ok
					}
 				}
 			}
 			
 			public Runnable init(String name, String token, int port, double lat, double lon) {
 				this.name = name;
 				this.token = token;
 				this.port = port;
 				this.lat = lat;
 				this.lon = lon;
 				gson = new Gson();
 				return this;
 			}
 		}.init(name, "SecretToken", port, lat, lon);
 		pool.submit(thread);
	}
	
	public Restlet createInboundRoot() {
		Router r = new Router(getContext());
		r.attach("/spot", new Restlet() {
			public SpotFactory factory;
			public Restlet init(SpotFactory f) {
				this.factory = f;
				return this;
			}
			@Override						
			public void handle(Request req, Response res) {
				if( req.getMethod().equals(Method.GET)) {
					System.out.println(Arrays.toString(this.factory.getSpots().toArray()));
					res.setEntity(gson.toJson(this.factory.getSpots()), MediaType.TEXT_PLAIN);
				}
				if( req.getMethod().equals(Method.POST)) {
					boolean findspot=false;
					System.out.println(this.factory.getSpots());
					for(Spot s: this.factory.getSpots()){
						try {
							System.out.println(s.getStatus());
							if(s.getStatus()==Spot.FREE){
								findspot=true;
								s.setStatus(Spot.BOOKED);
								res.setEntity(gson.toJson(s), MediaType.TEXT_PLAIN);
								break;
							}
						} catch(RemoteException e) {
							e.printStackTrace();
						}
					}
					if(findspot==false){
						res.setStatus(Status.SUCCESS_NO_CONTENT);
					}
				}
			}
		}.init(factory));
		return r;
	}
}

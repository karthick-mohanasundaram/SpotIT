package middle;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SpotFactory implements SpotFactoryInterface{
	private Map<String, Spot> spots;
	
	public SpotFactory() throws Exception{
		this.spots = new HashMap<String, Spot>();
	}
	
	public SpotInterface createSpot(String code) throws RemoteException{
		Spot s = new Spot();
		this.spots.put(code, s);
		System.out.println(spots);
		SpotInterface exported = (SpotInterface) UnicastRemoteObject.exportObject(s,0);
		return exported;
	}
	
	public Collection<Spot> getSpots() {
		return spots.values();
	}
}

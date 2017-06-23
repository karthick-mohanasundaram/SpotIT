package middle;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

public interface SpotFactoryInterface extends Remote{
	public SpotInterface createSpot(String code) throws RemoteException;
	public Collection<Spot> getSpots() throws RemoteException;

}

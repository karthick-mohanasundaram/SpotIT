package middle;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SpotInterface extends Remote {
	public static int INIT = 0;
	public static int FREE = 1;
	public static int OCCUPIED = 2;
	public static int BOOKED = 3;
	public static int UNAVAIL = 4;
	public static int UNKNOWN = 5;
	public static int ERROR = 6;
	public int getStatus() throws RemoteException;
	public void setStatus(int status) throws RemoteException;
	
}

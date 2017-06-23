package middle;

import java.rmi.RemoteException;

public class Spot implements SpotInterface {
	public static int INIT = 0;
	public static int FREE = 1;
	public static int OCCUPIED = 2;
	public static int BOOKED = 3;
	public static int UNAVAIL = 4;
	public static int UNKNOWN = 5;
	public static int ERROR = 6;
	
	public static String getStatusString(int status) {
		if( status == INIT )
			return "init";
		if( status == FREE )
			return "free";
		if( status == OCCUPIED )
			return "occupied";
		if( status == BOOKED )
			return "booked";
		if( status == UNAVAIL )
			return "unavailable";
		if( status == UNKNOWN )
			return "unknown";
		if( status == ERROR )
			return "error";
		return null;
	}
	
	private int status;
	
	public Spot() {
		this.status = Spot.INIT;
	}
	
	public int getStatus() throws RemoteException {
		return status;
	}
	
	public String toString() {
		return "Status: " + getStatusString(this.status);
	}
	
	public void setStatus(int status) throws RemoteException {
		this.status = status;
	}
}

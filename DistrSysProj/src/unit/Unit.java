package unit;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import middle.Spot;
import middle.SpotFactoryInterface;
import middle.SpotInterface;

public class Unit implements UnitInterface {
	private SpotInterface s;
	private String code;
	private int sensorStatus;
	private int spotStatus;
	private boolean flag = true;
	private int rmiPort;
	
	public Unit(int rmiPort, String code) {
		this.rmiPort = rmiPort;
		try {
			bind(code);
		} catch(Exception e) {
			System.err.println("Rmi error:");
			e.printStackTrace();
		}
	}
	
	public void work() {
		workLoop();	
	}
	
	public int getRmi() {
		return rmiPort;
	}
	
	public void setSensorStatus(int status) {
		this.sensorStatus = status;
	}
	
	public String getCode() {
		return code;
	}
	
	public SpotInterface getSpot() {
		return s;
	}
	
	public void shutdown() {
		this.flag = false;
	}
	
	public void workLoop() {
		try {
			s.setStatus(Spot.FREE);
		} catch(RemoteException e) {
			e.printStackTrace();
		}
		while(flag) {
			try{ 
				this.spotStatus = s.getStatus();
				if( sensorStatus == Spot.OCCUPIED && spotStatus == Spot.BOOKED ) {
					s.setStatus( Spot.OCCUPIED );
				} else if( sensorStatus == Spot.OCCUPIED && spotStatus == Spot.FREE ) {
					s.setStatus( Spot.OCCUPIED );
				} else if( sensorStatus == Spot.FREE && spotStatus == Spot.OCCUPIED ) {
					s.setStatus( Spot.FREE );
				} else if( sensorStatus == Spot.ERROR ) {
					s.setStatus( Spot.ERROR );
				} else if( sensorStatus == Spot.INIT && spotStatus == Spot.ERROR) {
					s.setStatus( Spot.INIT );
				} else if( spotStatus == Spot.INIT ) {
					s.setStatus( sensorStatus );
				}
			} catch(RemoteException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// it's ok to go on
			}
		}
	}

	public void bind(String code) throws Exception {
		s = ((SpotFactoryInterface) LocateRegistry.getRegistry("127.0.0.1", rmiPort).lookup("Factory")).createSpot(code); // please gods of rmi be merciful
		this.spotStatus = s.getStatus();
		this.code = code;
	}
}

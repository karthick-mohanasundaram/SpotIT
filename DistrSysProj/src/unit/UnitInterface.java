package unit;

import java.io.Serializable;

import middle.SpotInterface;

public interface UnitInterface extends Serializable{
	public void work();
	public void setSensorStatus(int status);
	public String getCode();
	public SpotInterface getSpot();
	public void shutdown();
	public void workLoop();// private before
	public void bind(String code) throws Exception; //private before
	
}

package sim;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import middle.Spot;
import unit.Unit;

public class Cli {
	private Map<String, Unit> units;
	private BufferedReader reader;
	private PrintStream writer;
	private ExecutorService pool;
	private boolean flag = true;
	private int rmiPort;
	
	public static void main(String[] args) throws IOException {
		new Cli();
	}
	
	public Cli() throws IOException {

		units = new HashMap<String, Unit>();
		pool = Executors.newCachedThreadPool();
		
		reader = new BufferedReader(new InputStreamReader(System.in));
		writer = System.out;
		
		while(flag) {
			writer.print("Rmi port: ");
			String[] line = reader.readLine().split(" ");
			if( line.length > 0 && Integer.parseInt(line[0]) > 1024 ) {
				rmiPort = Integer.parseInt(line[0]);
				flag = false;
			} else {
				writer.println("Number not valid!");
			}
		}
		
		flag = true;
		while(flag) {
			writer.print("> ");
			String[] line = reader.readLine().split(" ");
			doCommand(line);
		}
	}

	private void doCommand(String[] command) throws RemoteException {
		if( command[0].equals("list") || command[0].equals("ls") || command[0].equals("l")) {
			if( units.size() > 0 ) {
				for(Unit u : units.values()) {
					writer.println("Code: " + u.getCode() + " status: " + Spot.getStatusString(u.getSpot().getStatus()));
				}
			} else {
				writer.println("No units!");
			}
		} else if( (command[0].equals("add") || command[0].equals("a")) && command.length > 1) {
			final Unit u = new Unit(this.rmiPort, command[1]);
			units.put(u.getCode(), u);
			pool.execute(new Runnable() {
				@Override
				public void run() {
					u.work();
				}
			});
		} else if( (command[0].equals("set") || command[0].equals("s")) && command.length > 2) {
			int newSensorStatus = 0;
			try {
				newSensorStatus = Integer.parseInt(command[2]);
			} catch(NumberFormatException e) {
				if( command[2].toLowerCase().equals("free") || command[2].equals("f"))
					newSensorStatus = Spot.FREE;
				if( command[2].toLowerCase().equals("occupied") || command[2].equals("o"))
					newSensorStatus = Spot.OCCUPIED;
				if( command[2].toLowerCase().equals("error") || command[2].equals("e"))
					newSensorStatus = Spot.ERROR;
			}
			Unit u = units.get(command[1]);
			if( u != null ) {
				u.setSensorStatus(newSensorStatus);
			} else {
				writer.println("No such Unit!");
			}
		} else if( ( command[0].toLowerCase().equals("delete") || command[0].toLowerCase().equals("del") || command[0].equals("d")) && command.length > 1) {
			Unit u = units.get(command[1]);
			if( u != null) {
				u.shutdown();
				units.remove(u.getCode());
			} else {
				writer.println("No such Unit!");
			}
		} else if( command[0].toLowerCase().equals("quit") || command[0].toLowerCase().equals("exit") || command[0].equals("q")) {
			for(Unit u : units.values()) {
				u.shutdown();
			}
			pool.shutdown();
			this.flag = false;
		}
	}
}

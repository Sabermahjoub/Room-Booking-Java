package RoomsServer;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import Rooms.Rooms;

public class RoomsServer {
	
	static String serverUrl = "rmi://localhost:1099/roomsService";
	static int port = 1099;

	public RoomsServer() {
	}
	
	public static void main(String[] args) {

		try {
			System.setProperty("java.security.policy", "./roomsPolicy.policy");

			System.setSecurityManager(new SecurityManager());
			LocateRegistry.createRegistry(port);
			Rooms roomsServices = new Rooms();
			Naming.rebind(serverUrl, roomsServices);
			
			System.out.println("Rooms Server is running !");

		}
		
		catch(Exception e) {
			e.printStackTrace();
		}	
	}

}

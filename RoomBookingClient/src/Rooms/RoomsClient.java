package Rooms;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class RoomsClient {

	public RoomsClient() {
	}
	
	static boolean verifyValidRight(String right) {
		List<String> allRights = Arrays.asList("administrator", "super administrator", "basic");
		for (String right_elt : allRights) {
			if (right.equals(right_elt)) return true;
		}
		System.out.println("You typed an invalid right/privilege !");
		return false;
		
	}
	
	static void printMenu(String right) {
		//Options menu
		// Afficher le menu des options
		System.out.println("********************************************************************");
		System.out.println("*                          Menu                                    *");
		System.out.println("********************************************************************");
		System.out.println("*  1. Book a room                                                  *");
		System.out.println("*  2. Cancel my reservation                                        *");
		System.out.println("*  3. Check availability of a room by slot                         *");
		System.out.println("*  4. Get available slots for a room                               *");
		System.out.println("*  5. Check availability of a room                                 *");
		System.out.println("*  6. Get nearest slot possible with at least one room available   *");
		if(right.equals("super administrator")) {
		    System.out.println("*  7. Get list of rooms available by slot (Super admin)            *");
		    System.out.println("*  8. Get list of all registered rooms (Super admin)               *");
		    System.out.println("*  9. Add a new room (Super admin)                                 *");
		}
		System.out.println("*  0. Quit                                                         *");
		System.out.println("********************************************************************");
    	System.out.print("> Type your choice: ");
	}
	
	static String bookARoom (InterfaceRoom stub, String right) {
        Scanner scanner = new Scanner(System.in);	
        System.out.println("> Type the room you'd like to reserve ...");
        String room = scanner.next();
        System.out.println("> Type your slot ...");
        String slot = scanner.next();
        try {
			 return stub.bookSlot(room, slot, right);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return "Error while trying to book";
		
	}
	
	static boolean checkRoomAvailibityBySlot(InterfaceRoom stub) {
        Scanner scanner = new Scanner(System.in);	
        System.out.println("> Type the room you'd like to check for availibity ... ");
        String room = scanner.next();
        System.out.println("> Type your desired slot to check ... ");
        String slot = scanner.next();
        try {
			 return stub.checkAvailibilityRoomSlot(room, slot);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	static String cancelReservation (InterfaceRoom stub) {
		Scanner scanner = new Scanner(System.in);
        System.out.println("> Type the room reserved you'd like to cancel ...");
        String room = scanner.next();
	    System.out.println("> Type your reserved slot ...");
	    String slot = scanner.next();
        try {
			 return stub.cancelReservation(room, slot);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return "Error while trying to cancel reservation";
	}
	
	static void getAvailableSlots(InterfaceRoom stub, int actualHour) {
		Scanner scanner = new Scanner(System.in);
        System.out.println("> Type the room you'd like to see if available for all possible slots ...");
        String room = scanner.next();
        try {
			 List<String> allSlots = stub.getAvailableSlots(room,actualHour);
			 if (allSlots.isEmpty()) System.out.println("There is no possible slot for the rest of the day to reserve the room : "+room);
			 else {
				 System.out.println("All slots available for the rest of the day for the room : "+room);
				 int i=1;
				 for (String slot : allSlots) {
					 System.out.println(i + " # "+slot);
					 i++;
				 }
			 }
			 
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	static void checkRoomAvailibity(InterfaceRoom stub) {
		Scanner scanner = new Scanner(System.in);
        System.out.println("> Type the room you'd like to see if not fully booked ...");
        String room = scanner.next();
		try {
			// If this method is true <-> We are certain there is a slot for today available to book this room 
			// (i.e the room is not fully booked)
			if(stub.checkAvailibilityRoom(room)) System.out.println("The room :"+room +" is available at some slot of the day ");
			// The room is fully booked.
			else System.out.println("The room :"+room+" is fully booked for today, no available slots ");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}
	
	static String getNearestAvailableSlotAnyRoom(InterfaceRoom stub, int actualHour) {
		try {
			return stub.getNearestAvailableSlotAnyRoom(actualHour);
			
		}
		catch(RemoteException e) {
			e.printStackTrace();
		}
		return "An error occured while trying to get the nearest availabel slot to book any room";
		
	}
	
	static void getListRegisteredRooms(InterfaceRoom stub, String right) {
		try {
			List<String> listRooms = stub.getAllRegisteredRooms(right);
			if (listRooms == null) System.out.println("No rooms available yet!");
			else {
				System.out.println("All registered rooms :");
				for (String roomInfo : listRooms) {
					System.out.println(roomInfo);
				}
				
				
			}
			
		}
		catch(RemoteException e) {
			e.printStackTrace();
		}
	}
	
	static void addNewRoom(InterfaceRoom stub, String right) {
        Scanner scanner = new Scanner(System.in);	
        String right_input = "";
        System.out.println("> Type the room name ... ");
        String roomName = scanner.next();
        List<String> user_rights = new ArrayList<String>();
        System.out.println("> Type the user rights required to book this room (type 'exit' to quit) ... ");
        while(!right_input.toLowerCase().equals("exit")) {
        	right_input = scanner.next();
        	if (right_input.equals("exit")) break;
        	if(verifyValidRight(right_input.toLowerCase())) user_rights.add(right_input);
        	else System.out.println("Try again !");
        }
        try {
        	System.out.println(stub.addNewRoom(roomName, user_rights, right));
        }
        catch(RemoteException e) {
        	e.printStackTrace();
        }
        

	}
	
	static void getAllAvailableRoomsBySlot(InterfaceRoom stub,String right) {
        Scanner scanner = new Scanner(System.in);	
        System.out.println("> Type your desired slot to check ... ");
        String slot = scanner.next();
        try {
        	List<String> listRooms = stub.getAllRoomsAvailableBySlot(slot, right);
        	if (listRooms.isEmpty()) {
        		System.out.println("No rooms available for this slot !");
        	}
        	if(listRooms == null) System.out.println("Not enough rights for this action");
        	else {
        		System.out.println("All rooms available for the slot : "+slot);
        		int i = 1;
        		for (String room : listRooms) {
        			System.out.println(i+ " # "+room);
        			i++;
        		}
        	}
        	
        }
        catch(RemoteException e) {
        	e.printStackTrace();
        }
		
	}
	
	public static void main (String[] args) throws MalformedURLException, RemoteException, NotBoundException {
		
		try {
			
			
			// RMI configuration
			System.setProperty("java.security.policy", "./clientPolicy.policy");
			if (System.getSecurityManager() == null) {
	            System.setSecurityManager(new SecurityManager());
	        }
			InterfaceRoom stub = (InterfaceRoom) Naming.lookup("rmi://localhost:1099/roomsService");
			
			// Initilizations 
			int menuChoice = -1;
	        Scanner scanner = new Scanner(System.in);	
	        
	        // Right/privilege input
	        System.out.println("Type your right ");
	        String right = scanner.nextLine();
	        while(!verifyValidRight(right.toLowerCase())) right = scanner.next();

			
			while (menuChoice != 0) {
				printMenu(right.toLowerCase());
				menuChoice = scanner.nextInt();
				if (menuChoice == 1) {
					String result_booking = bookARoom(stub,right.toLowerCase());
					System.out.println(result_booking);
				}
				if (menuChoice == 2) {
					String result_cancel = cancelReservation(stub);
					System.out.println(result_cancel);
				}
				if (menuChoice == 3) {
					boolean result_check = checkRoomAvailibityBySlot(stub);
					if(result_check) System.out.println("This room is available to book!");
					else System.out.println("Room is not available for this slot. Try another slot !");
					
				}
				if (menuChoice == 4) {
					LocalTime currentTime = LocalTime.now();
					getAvailableSlots(stub, currentTime.getHour());
				}
				if (menuChoice == 5) {
					checkRoomAvailibity(stub);
				}
				if (menuChoice == 6) {
					LocalTime currentTime = LocalTime.now();
					System.out.println(getNearestAvailableSlotAnyRoom(stub, currentTime.getHour()));
				}
				if (menuChoice == 7) {
					getAllAvailableRoomsBySlot(stub,right.toLowerCase());
				}
				if (menuChoice == 8) {
					getListRegisteredRooms(stub, right);
				}		
				if (menuChoice == 9) {
					addNewRoom(stub, right.toLowerCase());
				}	
			}
			System.out.println("You quit the menu.");
			
			
			// Tests
			//List<String> allSlots = stub.getAvailableSlots("A1", 20);
			/*String book1 = stub.bookSlot("A1", "20:00-21:00", "Super Administrator");
			String book2 = stub.bookSlot("B1", "20:00-21:00", "Super Administrator");
			String book3 = stub.bookSlot("A2", "20:00-21:00", "Super Administrator");
			String book4 = stub.bookSlot("C1", "20:00-21:00", "Super Administrator");

			System.out.println(book1);
			System.out.println(book2);
			System.out.println(book3);
			System.out.println(book4);*/
			
			// COULD BE EMPTY !
			/*List<String> allAvailableRooms = stub.getAllRoomsAvailableBySlot("20:00-21:00", "Super Administrator");
			for (String room : allAvailableRooms) {
				System.out.println(room);
			}

			String nearestSlot = stub.getNearestAvailableSlotAnyRoom(20);
			
			System.out.println(nearestSlot);
			if(allSlots.isEmpty()) {
				System.out.println("SLOTS EMPTY");
			}
			else {
				for (String slot : allSlots) {
					System.out.println(slot);
				}
			}*/
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}

package Rooms;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Rooms extends UnicastRemoteObject implements InterfaceRoom {
	private final List<Room> roomsList = Collections.synchronizedList(new ArrayList<>());
    private final ConcurrentHashMap<String, List<String> > roomsMap = new ConcurrentHashMap<>();
    
    @Override
    public synchronized String addNewRoom(String roomName, List<String> rights, String user_right) {
    	if(!rights.contains("super administrator")) rights.add("super administrator");
    	if(user_right.equals("super administrator")) {
    	Room room = new Room(roomName, rights);
    	roomsList.add(room);
    	for(String key : roomsMap.keySet()) {
    		roomsMap.get(key).add(roomName);
    	}
    	return "Room : "+roomName+" added successfully";
    	}
    	else return "Not enough rights to add a new room ";
    		
    }
    
    String getSlotFormat(int hour) {
    	String hour1, hour2, hourFormat1, hourFormat2;
		if(hour == 23) {
			hourFormat1 = "23:00";
			hourFormat2 = "00:00";
		}
		hour1 = Integer.toString(hour);
		hour2 = Integer.toString(hour+1);
		
		if(hour1.length() < 2) hourFormat1 = "0"+hour1+":"+"00";
		else hourFormat1 = hour1+":"+"00";
		
		if(hour2.length() < 2) hourFormat2 = "0"+hour2+":"+"00";
		else hourFormat2 = hour2+":"+"00";
		
		return hourFormat1+"-"+hourFormat2;
    	
    }

    void initliazeRooms() {
    	Room room1 = new Room("A1", Arrays.asList("administrator", "super administrator"));
    	roomsList.add(room1);
    	Room room2 = new Room("A2", Arrays.asList("administrator", "super administrator"));
    	roomsList.add(room2);
    	Room room3 = new Room("B1",  Arrays.asList("basic","administrator", "super administrator") );
    	roomsList.add(room3);
    	Room room4 = new Room("C1", Arrays.asList("super administrator"));
    	roomsList.add(room4);
    }

	public Rooms() throws RemoteException{
		this.initliazeRooms();
        /*List<String> roomNames = new ArrayList<>();
        roomNames.add("A1");
        roomNames.add("A2");
        roomNames.add("B1");
        roomNames.add("C1");*/

		for (int i =0 ; i<= 23 ; i++) {
			this.roomsMap.put(this.getSlotFormat(i), new ArrayList<>(Arrays.asList("A1", "A2", "B1", "C1")));
		}
	}
	
	public String getRoomRights(String roomName) {
		String rights= "";
		for(Room room : roomsList) {
			if(room.getRoomName() == roomName) {
				for (String right : room.getRights()) {
					rights += right+" ";
				}
				return rights;
			}
		}
		return "";
	}
	
	public boolean checkIfBookerHasRights(String roomName, String booker_right) {
		if (!roomsList.isEmpty()) {
			for (Room room : roomsList) {
			    if (room.getRoomName().equals(roomName) ) {
			        for(String right : room.getRights()) {
			        	if(right.equals(booker_right)) return true;
			        }
			    }
			}
		}
		return false;
	}

	@Override
	public synchronized List<String> getAvailableSlots(String roomName, int actualHour) throws RemoteException {
		List<String> slotsAvailable = new ArrayList<String> ();
        String startSlot = this.getSlotFormat(actualHour);
        for (String key : roomsMap.keySet()) {
		    int hourStart = Integer.parseInt(key.substring(0,2));
            if (hourStart >= actualHour) {
            	if (checkAvailibilityRoomSlot(roomName,key)) slotsAvailable.add(key);
            }
        }
		
		return slotsAvailable;
	}
	
	// This functionality checks if a room has been fully booked (not available for the whole day)
	public synchronized boolean checkIfRoomIsFullyBooked(String roomName) {
		synchronized(roomsList) {  // Additional synchronization block
			for(String slot : roomsMap.keySet()) {
				List<String> allAvailableRooms = roomsMap.get(slot);
				if(allAvailableRooms.contains(roomName)) return false;
			}
			this.makeRoomFullyBooked(roomName);
			return true;
		}
	}
	
	// Make a room fully booked for the whole day  (Super admin)
	public void makeRoomFullyBooked(String roomName) {
		for(Room room : roomsList) {
			if(room.getRoomName().equals(roomName)) room.setAvailibility(true);
		}
	}
	
	void getAllAvailableRoomsBySlot() {
		for (String key : roomsMap.keySet()) {
			System.out.println(key);
			for (String room : roomsMap.get(key)) {
				System.out.print(room + " ");
			}
		}
	}

	@Override
	public synchronized String bookSlot(String roomName, String slot, String right) throws RemoteException {
		if (!checkAvailibilityRoomSlot(roomName,slot)) return "Room is not available ! ";
		if (!checkIfBookerHasRights(roomName,right)) return "Not enough rights to book this room ";
		//Room is available
		List<String> rooms = roomsMap.get(slot);

	    // Ensure the slot exists in the map
	    if (rooms == null) return "Slot does not exist!";

	    // Remove the room from the list and return success if removal occurred
	    if (rooms.remove(roomName)) {
	        return "Room " + roomName + " is successfully booked for " + slot;
	    } else {
	        return "Room " + roomName + " was not available for booking in slot " + slot;
	    }
	   
	}

	// A functionality that enables client to cancel his reservation. 
	@Override
	public synchronized String cancelReservation(String roomName, String slot) throws RemoteException {
		List<String> allRoomsBySlot = roomsMap.get(slot);
		
		// Room is not reserved ! Cannot cancel reservation for a room not booked.		
		if (allRoomsBySlot.contains(roomName)) return "Room "+roomName+" "+"is not reserved yet for slot : "+slot;
		
		allRoomsBySlot.add(roomName);
		return "Reservation for room "+roomName+" cancelled successfully !";
	
	}
	
	// Checks if a room is ever available for the whole day.
	@Override
	public synchronized boolean checkAvailibilityRoom(String roomName) throws RemoteException {
		for(Room room : roomsList) {
			if(room.getRoomName().equals(roomName)) return room.getAvailibility();
		}
		return false;
	}

	// A functionality that checks if a specific room is available to book for a specific slot
	@Override
	public synchronized boolean checkAvailibilityRoomSlot(String roomName, String slot) throws RemoteException {
		List<String> rooms = this.roomsMap.get(slot);
		for(String room_name : rooms) {
			if(room_name.equals(roomName)) return true;
		}
		return false;
	}

	
	//   A functionality that : 
	// * return the next temporally nearest slot with at least one room still available to book.
	@Override
	public synchronized String getNearestAvailableSlotAnyRoom(int actualHour) throws RemoteException {
		List<Integer> allHoursAvailable = new ArrayList<Integer> ();
		String startSlot = this.getSlotFormat(actualHour);
        for (String key : roomsMap.keySet()) {
		    int hourStart = Integer.parseInt(key.substring(0,2));
            if (hourStart >= actualHour && !roomsMap.get(key).isEmpty() ) {
            	allHoursAvailable.add(hourStart);
            }
        }
		if(allHoursAvailable.isEmpty()) return  "No available slot ";	
		else {
			int minHour = allHoursAvailable.get(0);
			for (int hour : allHoursAvailable) {
				if (hour < minHour) minHour = hour;
			}
			return this.getSlotFormat(minHour);
		}
	}

	// A functionality to get all available rooms by specifying a slot (Only Super admins can have access to this)
	@Override
	public synchronized List<String> getAllRoomsAvailableBySlot(String slot, String right) throws RemoteException {
		if(right.toLowerCase().equals("super administrator")) return roomsMap.get(slot);
		else return null;
	}

	@Override
	public synchronized List<String> getAllRegisteredRooms(String right) throws RemoteException {
		if (this.roomsList.isEmpty()) return null;
		List<String> listRoomsStringFormat = new ArrayList<String>();
		for (Room room : roomsList) {
			listRoomsStringFormat.add(room.toString());
		}
		return listRoomsStringFormat;
	}

}

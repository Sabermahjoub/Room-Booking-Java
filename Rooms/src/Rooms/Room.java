package Rooms;

import java.util.ArrayList;
import java.util.List;

public class Room {
	String roomName; // id of a room 
	List<String> rights = new ArrayList<String>();
	boolean free;

	public Room(String roomName, List<String> rights) {
		this.roomName = roomName;
		this.rights = rights;
		this.free = true;
	}
	
	public Room(String roomName, List<String> rights, boolean free) {
		this.roomName = roomName;
		this.rights = rights;
		this.free = free;
	}
	
	void setAvailibility(boolean free) {
		this.free = free;
	}	
	
	void setAvailibility() {
		this.free = !this.free;
	}
	
	String getRoomName() {
		return roomName;
	}
	
	List<String> getRights() {
		return rights;
	}
	
	boolean getAvailibility() {
		return free;
	}
	
	public String toString() {
		String str = "rights : ";
		for(String right : this.getRights()) {
			str += right+" ,";
		}
		if(free) str+= " free.";
		else str+= " not free";
		return roomName + " , "+str + " ";
	}

}

package Rooms;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface InterfaceRoom extends Remote {
	
	List<String> getAvailableSlots(String roomName, int actualHour) throws RemoteException;
	String bookSlot(String roomName, String slot, String right) throws RemoteException;
	String cancelReservation(String roomName, String slot) throws RemoteException;
	boolean checkAvailibilityRoomSlot(String roomName, String slot) throws RemoteException;
	boolean checkAvailibilityRoom(String roomName) throws RemoteException;
    String getNearestAvailableSlotAnyRoom(int actualHour) throws RemoteException;
    List<String> getAllRoomsAvailableBySlot(String slot, String right ) throws RemoteException;
    List<String> getAllRegisteredRooms(String right) throws RemoteException;
    String addNewRoom (String roomName, List<String> rights, String user_right) throws RemoteException;

}

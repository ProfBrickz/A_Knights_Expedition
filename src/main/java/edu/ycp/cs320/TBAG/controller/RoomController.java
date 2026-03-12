package edu.ycp.cs320.TBAG.controller;


import edu.ycp.cs320.TBAG.model.Room;
import edu.ycp.cs320.TBAG.model.RoomConnection;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class RoomController {
   //HashMap :: room ID : Room
   private HashMap <String, Room> roomList = new HashMap<String, Room>();




   public RoomController(HashMap <String, Room> roomList) {
       this.roomList = roomList;
   }


   public RoomController(){
       //does nothing because roomList would already be blank
   }


   //creates a new room and saves it to a hashMap of rooms
   public void makeRoom(String id, String name, String description, HashMap<String, RoomConnection> roomConnections) {
       Room room = new Room(id, name, description, roomConnections);
       roomList.put(id, room);
   }


   //adds a connection from fromID to toID, accessed by a keyword of key
   public void addRoomConnection(String fromID, String toID, String key) {
       roomList.get(fromID).setConnection(toID, key);
   }


   public Boolean isValidDirection(String roomID, String direction) {
      
       if (roomList.get(roomID).getRoomConnections().containsKey(direction)) return true;
       return false;
   }
}




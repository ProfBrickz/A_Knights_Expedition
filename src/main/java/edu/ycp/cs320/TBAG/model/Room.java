package edu.ycp.cs320.TBAG.model;


import java.util.HashMap;


public class Room {


private String id, roomDescription, roomName;
//Hashmap:: String : Direction, RoomConnection : Pointer to next room
private HashMap <String, RoomConnection> roomConnections = new HashMap<String, RoomConnection>();


   public Room(String id, String name, String description, HashMap<String, RoomConnection> roomConnections) {
       this.id = id;
       roomName = name;
       roomDescription = description;
       this.roomConnections = roomConnections;
   }


   public Room(){
       id = "NULL";
       roomName = "Null";
       roomDescription = "NULL";
   }


   public String getId() {
       return id;
   }


   public String getName() {
       return roomName;
   }


   public String getDescription() {
       return roomDescription;
   }


   public HashMap<String, RoomConnection> getRoomConnections() {
       return roomConnections;
   }


   public void setDescription(String description){
       roomDescription = description;
   }


   public void setName(String name){
       roomName = name;
   }


   //links rooms together, id = destination, key = direction/keyword
   public void setConnection(String id, String key){
       RoomConnection temp = new RoomConnection(id);
       roomConnections.put(key, temp);
   }


   //same as other, but will accept a description if wanted
   public void setConnection(String id, String key, String description){
       RoomConnection temp = new RoomConnection(id, description);
       roomConnections.put(key, temp);
   }
  
}




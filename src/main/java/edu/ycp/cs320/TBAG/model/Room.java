package edu.ycp.cs320.TBAG.model;

import java.util.HashMap;

public class Room {

	private String id, roomDescription, roomName;
	//Hashmap:: String : Direction, RoomConnection : Pointer to next room
	private HashMap<String, RoomConnection> roomConnections = new HashMap<String, RoomConnection>();

	public Room(String id, String name, String description, HashMap<String, RoomConnection> roomConnections) {
		this.id = id;
		roomName = name;
		roomDescription = description;
		this.roomConnections = roomConnections;
	}

	//creates a room with no connections
	public Room(String id, String name, String description) {
		this.id = id;
		roomName = name;
		roomDescription = description;
	}

	public Room() {
		id = "NULL";
		roomName = "Null";
		roomDescription = "NULL";
	}

	public String getID() {
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

	public void setDescription(String description) {
		roomDescription = description;
	}

	public void setName(String name) {
		roomName = name;
	}

	//links rooms together, room = destination, key = direction/keyword
	public void setConnection(Room room, String key) {
		roomConnections.put(key.toLowerCase(), new RoomConnection(room));
	}

	//same as other, but will accept a description if wanted
	public void setConnection(Room room, String key, String description) {
		roomConnections.put(key, new RoomConnection(room, description));
	}
}




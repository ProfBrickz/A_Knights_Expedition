package edu.ycp.cs320.TBAG.controller;


import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.NPC;
import edu.ycp.cs320.TBAG.model.Room;
import edu.ycp.cs320.TBAG.model.RoomConnection;

import java.util.HashMap;


public class RoomController {
	//HashMap :: room ID : Room
	private HashMap<String, Room> roomList = new HashMap<String, Room>();


	public RoomController(HashMap<String, Room> roomList) {
		this.roomList = roomList;
	}

	//creates a new room and saves it to a hashMap of rooms
	public void makeRoom(String id, String name, String description, HashMap<String, RoomConnection> roomConnections) {
		Room room = new Room(id, name, description, roomConnections);
		roomList.put(id, room);
	}

	//adds a connection from fromID to toID, accessed by a keyword of key
	public void addRoomConnection(Room fromRoom, Room toRoom, String key) {
		fromRoom.setConnection(toRoom, key);
	}

	public Boolean isValidDirection(Room room, String direction) {
		if (room.getRoomConnections().containsKey(direction)) return true;
		return false;
	}

	public NPC getNPCByName(Room room, String npcName) {
		for (NPC npc : room.getNpcs().values()) {
			if (npc.getName().equals(npcName)) return npc;
		}

		return null;
	}

	//load room function: takes in database of rooms + ID of room to access it and build it locally
	//will need to be redone with database once that is added
	public void loadRoom(String id, HashMap<String, Room> loadList) {
		Room temp = loadList.get(id);
		makeRoom(temp.getID(), temp.getName(), temp.getDescription(), temp.getRoomConnections());
	}

	//makeshift database for the set of rooms for the demo
	public void loadDemo() {
		Room start = new Room("0", "Shore", "You find yourself on a shore with a shipwreck");
		Room center = new Room("1", "Center", "You walk a bit until you spot a crossroads");
		Room left = new Room("2", "Cave entrance", "You find the entrance to a cave blocked by a boulder");
		Room top = new Room("3", "Mountains", "You find yourself looking up at a towering mountain");
		Room right = new Room("4", "Jungle", "You stumble into a densely packed grove of trees");

		start.setConnection(center, "NORTH");
		start.getInventory().addItem(new Item("0", "Sword", "a sword", 10));
		start.getInventory().addItem(new Item("1", "Old book", "a book", 3));

		center.setConnection(top, "NORTH");
		center.setConnection(right, "EAST");
		center.setConnection(start, "SOUTH");
		center.setConnection(left, "WEST");
		center.getInventory().addItem(new Item("1", "Old book", "a book", 3));
		center.getInventory().addItem(new Item("2", "Stick", "a stick", 1));
		NPC merchant = new NPC("0", "Merchant");
		merchant.getInventory().addItem(new Item("3", "Potion", "potion", 2));
		center.addNPC(merchant);

		left.setConnection(center, "EAST");

		top.setConnection(center, "SOUTH");

		right.setConnection(center, "WEST");

		HashMap<String, Room> map = new HashMap<String, Room>();

		roomList.put(start.getID(), start);
		roomList.put(center.getID(), center);
		roomList.put(left.getID(), left);
		roomList.put(top.getID(), top);
		roomList.put(right.getID(), right);
	}
}







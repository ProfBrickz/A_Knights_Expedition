package edu.ycp.cs320.TBAG.controller;


import edu.ycp.cs320.TBAG.model.NPC;
import edu.ycp.cs320.TBAG.model.Room;

import java.util.HashMap;


public class RoomController {
	//HashMap :: room ID : Room
	private final HashMap<Integer, Room> roomList;


	public RoomController(HashMap<Integer, Room> roomList) {
		this.roomList = roomList;
	}

	//adds a connection from fromID to toID, accessed by a keyword of key
	public void addRoomConnection(Room fromRoom, Room toRoom, String key) {
		fromRoom.setConnection(toRoom, key);
	}

	public Boolean isValidDirection(Room room, String direction) {
		return room.getRoomConnections().containsKey(direction);
	}

	public NPC getNPCByName(Room room, String npcName) {
		for (NPC npc : room.getNpcs().values()) {
			if (npc.getName().equals(npcName)) return npc;
		}

		return null;
	}

	public NPC getNPCByNameCaseInsensitive(Room room, String npcName) {
		npcName = npcName.toLowerCase();

		for (NPC npc : room.getNpcs().values()) {
			if (npc.getName().toLowerCase().equals(npcName)) return npc;
		}

		return null;
	}
}





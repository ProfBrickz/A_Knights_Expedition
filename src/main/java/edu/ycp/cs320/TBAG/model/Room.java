package edu.ycp.cs320.TBAG.model;

import java.util.HashMap;

public class Room {
	private String id, roomDescription, roomName;
	private String assetName;
	//Hashmap:: String : Direction, RoomConnection : Pointer to next room
	private HashMap<String, RoomConnection> roomConnections = new HashMap<>();
	private HashMap<String, Enemy> enemies = new HashMap<>();
	private Inventory inventory = new Inventory();
	private HashMap<String, NPC> npcs = new HashMap<>();

	public Room(
		String id,
		String name,
		String description,
		HashMap<String, RoomConnection> roomConnections,
		HashMap<String, Enemy> enemies,
		HashMap<String, NPC> npcs
	) {
		this.id = id;
		roomName = name;
		roomDescription = description;
		this.roomConnections = roomConnections;
		this.enemies = enemies;
		this.npcs = npcs;
	}

	public Room(
		String id,
		String name,
		String description,
		HashMap<String, RoomConnection> roomConnections
	) {
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

	public String getAssetName() {
		return assetName;
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

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	//links rooms together, room = destination, key = direction/keyword
	public void setConnection(Room room, String key) {
		roomConnections.put(key.toLowerCase(), new RoomConnection(room));
	}

	//same as other, but will accept a description if wanted
	public void setConnection(Room room, String key, String description) {
		roomConnections.put(key, new RoomConnection(room, description));
	}

	public HashMap<String, Enemy> getEnemies() {
		return enemies;
	}

	public Enemy addEnemy(Enemy enemy) {
		return enemies.put(enemy.getId(), enemy);
	}

	public Enemy removeEnemy(Enemy enemy) {
		return enemies.remove(enemy.getId());
	}

	public Inventory getInventory() {
		return inventory;
	}

	public HashMap<String, NPC> getNpcs() {
		return npcs;
	}

	public NPC addNPC(NPC npc) {
		return npcs.put(npc.getId(), npc);
	}

	public NPC removeNPC(NPC npc) {
		return npcs.remove(npc.getId());
	}
}



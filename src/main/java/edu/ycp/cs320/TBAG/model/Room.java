package edu.ycp.cs320.TBAG.model;

import java.util.HashMap;

public class Room {
	private final Integer id;
	private String roomDescription, roomName;
	private String assetName;
	//Hashmap:: String : Direction, RoomConnection : Pointer to next room
	private HashMap<String, RoomConnection> roomConnections = new HashMap<>();
	private HashMap<Integer, Enemy> enemies = new HashMap<>();
	private final Inventory inventory = new Inventory();
	private HashMap<Integer, NPC> npcs = new HashMap<>();

	public Room(
		Integer id,
		String name,
		String description,
		String assetName,
		HashMap<String, RoomConnection> roomConnections
	) {
		this.id = id;
		this.roomName = name;
		this.roomDescription = description;

		if (roomConnections != null) this.roomConnections = roomConnections;

		if (assetName == null || assetName.isEmpty()) this.assetName = "fixIt.png";
		else this.assetName = assetName;
	}

	public Room(
		Integer id,
		String name,
		String description,
		HashMap<String, RoomConnection> roomConnections
	) {
		this(id, name, description, null, roomConnections);
	}

	//creates a room with no connections
	public Room(Integer id, String name, String description, String assetName) {
		this(id, name, description, assetName, null);
	}

	public Room(Integer id, String name, String description) {
		this(id, name, description, (String) null);
	}


	public Integer getID() {
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

	public void setRoomConnections(HashMap<String, RoomConnection> roomConnections) {
		this.roomConnections = roomConnections;
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

	public HashMap<Integer, Enemy> getEnemies() {
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

	public HashMap<Integer, NPC> getNpcs() {
		return npcs;
	}

	public NPC addNPC(NPC npc) {
		return npcs.put(npc.getId(), npc);
	}

	public NPC removeNPC(NPC npc) {
		return npcs.remove(npc.getId());
	}
}



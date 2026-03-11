package edu.ycp.cs320.TBAG.model;

public class Player {
	public Player() {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public Room getRoom() {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public void getRoom(Room room) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	// Old code, in case you need to reuse it

//	private Integer roomID;
//	private Integer coins;
//	private String state; // "NORMAL" or "BATTLE"
//
//	// Inventory: item name -> quantity (simple version until Item class exists)
//	private HashMap<String, Integer> inventory;
//
//	public Player() {
//		this.roomID = 1;
//		this.coins = 0;
//		this.state = "NORMAL";
//		this.inventory = new HashMap<>();
//	}
//
//	public Player(Integer roomID) {
//		this.roomID = roomID;
//		this.coins = 0;
//		this.state = "NORMAL";
//		this.inventory = new HashMap<>();
//	}
//
//	// --- Movement ---
//	public Boolean move(String direction) {
//		// TODO: hook up to Room/RoomConnection logic
//		return false;
//	}
//
//	// --- Item actions (stubbed until Item class exists) ---
//	public void pickupItem(String itemName) {
//		inventory.put(itemName, inventory.getOrDefault(itemName, 0) + 1);
//	}
//
//	public void dropItem(String itemName) {
//		if (inventory.containsKey(itemName)) {
//			int count = inventory.get(itemName) - 1;
//			if (count <= 0) inventory.remove(itemName);
//			else inventory.put(itemName, count);
//		}
//	}
//
//	public void useItem(String itemName) {
//		// TODO: implement use logic
//	}
//
//	public String inspectItem(String itemName) {
//		return inventory.containsKey(itemName) ? itemName + " (x" + inventory.get(itemName) + ")" : "Item not found.";
//	}
//
//	// --- Combat stubs ---
//	public void die() {
//		// TODO: handle player death
//	}
//
//	public void flee() {
//		this.state = "NORMAL";
//	}
//
//	// --- Getters & Setters ---
//	public Integer getRoomID() { return roomID; }
//	public void setRoomID(Integer roomID) { this.roomID = roomID; }
//
//	public Integer getCoins() { return coins; }
//	public void setCoins(Integer coins) { this.coins = coins; }
//
//	public String getState() { return state; }
//	public void setState(String state) { this.state = state; }
//
//	public HashMap<String, Integer> getInventory() { return inventory; }
//	public void setInventory(HashMap<String, Integer> inventory) { this.inventory = inventory; }
}

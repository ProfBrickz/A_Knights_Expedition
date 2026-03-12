package edu.ycp.cs320.TBAG.model;

public class Player {
	private Room room;

	public Player() {
		this.room = null;
	}

	public Player(Room room) {
		this.room = room;
	}

	// --- Getters & Setters ---
	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

}

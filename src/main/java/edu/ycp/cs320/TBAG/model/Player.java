package edu.ycp.cs320.TBAG.model;

public class Player extends BattleEntity {
	private Room room;

	public Player(Integer maxHealth, Integer health) {
		super(maxHealth, health);

		this.room = null;
	}

	public Player(Integer maxHealth, Integer health, Room room) {
		super(maxHealth, health);

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

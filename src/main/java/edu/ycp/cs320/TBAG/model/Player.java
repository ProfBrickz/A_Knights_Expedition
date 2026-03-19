package edu.ycp.cs320.TBAG.model;

import java.util.ArrayList;

public class Player extends BattleEntity {
	private Room room;
	private ArrayList<Armor> armor;
	private PlayerState playerState;
	private final Inventory inventory = new Inventory();

	public Player(Integer maxHealth, Integer health) {
		super(maxHealth, health);

		this.room = null;

		throw new UnsupportedOperationException("TODO - implement");
	}

	public Player(Integer maxHealth, Integer health, PlayerState playerState, Room room) {
		super(maxHealth, health);

		this.room = room;

		throw new UnsupportedOperationException("TODO - implement");
	}

	// --- Getters & Setters ---
	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public Inventory getInventory() {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public PlayerState getState() {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public void setState() {
		throw new UnsupportedOperationException("TODO - implement");
	}
}

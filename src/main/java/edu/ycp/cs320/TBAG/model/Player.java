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
		this.armor = new ArrayList<>();
		this.playerState = PlayerState.EXPLORING;
	}

	public Player(Integer maxHealth, Integer health, PlayerState playerState, Room room) {
		super(maxHealth, health);

		this.room = room;
		this.armor = new ArrayList<>();
		this.playerState = playerState;
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
		return playerState;
	}

	public void setState() {
		if (playerState == null || playerState == PlayerState.BATTLE) {
			playerState = PlayerState.EXPLORING;
		} else {
			playerState = PlayerState.BATTLE;
		}
	}
}

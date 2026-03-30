package edu.ycp.cs320.TBAG.model;

import java.util.ArrayList;

public class Player extends BattleEntity {
	private Room room;
	private ArrayList<Armor> armor;
	private PlayerState playerState;
	private final Inventory inventory = new Inventory();
	private Integer coins;
	// The current NPC the player is talking to (if any)
	private NPC currentNPC = null;

	public Player(Integer maxHealth, Integer health) {
		super(maxHealth, health);

		this.room = null;
		this.armor = new ArrayList<>();
		this.playerState = PlayerState.EXPLORING;
		this.coins = 0;
	}

	public Player(Integer maxHealth, Integer health, PlayerState playerState, Room room) {
		super(maxHealth, health);

		this.room = room;
		this.armor = new ArrayList<>();
		this.playerState = playerState;
		this.coins = 0;
	}

	// --- Getters & Setters ---
	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public ArrayList<Armor> getArmor() {
		return armor;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public PlayerState getState() {
		return playerState;
	}

	public void setState(PlayerState newState) {
		playerState = newState;
	}

	public Integer getCoins(){
		return coins;
	}

	public void setCoins(Integer newCoins){
		coins = newCoins;
	}

	public NPC getCurrentNPC() {
		return currentNPC;
	}

	public void setCurrentNPC(NPC currentNPC) {
		this.currentNPC = currentNPC;
	}
}

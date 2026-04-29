package edu.ycp.cs320.TBAG.model;

import edu.ycp.cs320.TBAG.controller.Command;

import java.util.ArrayList;

public class Player extends BattleEntity {
	private Room room;
	private final ArrayList<Armor> armor;
	private PlayerState playerState;
	private Integer coins;
	// The current NPC the player is talking to (if any)
	private NPC currentNPC = null;
	private Command lastCommand = null;
	private Boolean confirming = false;

	public Player(Integer maxHealth, Integer health) {
		super(maxHealth, health);

		this.room = null;
		this.armor = new ArrayList<>();
		this.playerState = PlayerState.EXPLORING;
		this.coins = 0;
	}

	public Player(
		Integer maxHealth,
		Integer health,
		PlayerState playerState,
		Room room,
		Integer coins,
		Command lastCommand,
		Boolean confirming
	) {
		super(maxHealth, health);

		this.room = room;
		this.armor = new ArrayList<>();
		this.playerState = playerState;
		this.coins = coins;
		this.lastCommand = lastCommand;
		this.confirming = confirming;
	}

	public Player(
		Integer maxHealth,
		Integer health,
		PlayerState playerState,
		Room room,
		Integer coins
	) {
		super(maxHealth, health);

		this.room = room;
		this.armor = new ArrayList<>();
		this.playerState = playerState;
		this.coins = coins;
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

	public PlayerState getState() {
		return playerState;
	}

	public void setState(PlayerState newState) {
		playerState = newState;
	}

	public Integer getCoins() {
		return coins;
	}

	public void setCoins(Integer newCoins) {
		coins = newCoins;
	}

	public NPC getCurrentNPC() {
		return currentNPC;
	}

	public void setCurrentNPC(NPC currentNPC) {
		this.currentNPC = currentNPC;
	}

	public Command getLastCommand() {
		return lastCommand;
	}

	public void setLastCommand(Command lastCommand) {
		this.lastCommand = lastCommand;
	}

	public Boolean getConfirming() {
		return confirming;
	}

	public void setConfirming(Boolean confirming) {
		this.confirming = confirming;
	}

	private Enemy currentEnemy = null;

	public Enemy getCurrentEnemy() {
		return currentEnemy;
	}

	public void setCurrentEnemy(Enemy enemy) {
		this.currentEnemy = enemy;
	}

	public Player copy() {
		return new Player(
			getMaxHealth(),
			getHealth(),
			playerState,
			room.copy(),
			coins,
			lastCommand,
			confirming
		);
	}
}

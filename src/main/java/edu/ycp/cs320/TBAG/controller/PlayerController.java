package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.*;

public class PlayerController {
	private final BattleEntityController battleEntityController;
	private Player player;

	public PlayerController(Player player, BattleEntityController battleEntityController) {
		this.player = player;

		throw new UnsupportedOperationException("TODO - implement");
	}

	public Boolean move(String direction) {
		if (player == null || player.getRoom() == null) {
			return false;
		}

		Room currentRoom = player.getRoom();
		RoomConnection connection = currentRoom.getRoomConnections().get(direction);

		if (connection != null) {
			Room nextRoom = connection.getRoom();
			player.setRoom(nextRoom);
			return true;
		}

		return false;
	}

	// Throws IllegalArgumentException if armor is not active
	public void equipArmor(Armor armor) throws IllegalArgumentException {
		throw new UnsupportedOperationException("TODO - implement");
	}

	// Throws IllegalArgumentException if armor is not active
	public void unequipArmor(Armor armor) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	// Adds defence of all equipped armor
	public void getDefence() {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public void die(Integer startingMaxHealth, Integer startingHealth, Room startingRoom) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public void attack(Enemy enemy, WeaponAbility weaponAbility) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	// Throws IllegalArgumentException if armor is active
	public void defend(Armor armor) throws IllegalArgumentException {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public void heal(HealingItem healingItem) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public Boolean flee() {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public String inspectItem(Item item) {
		throw new UnsupportedOperationException("TODO - implement");
	}
}

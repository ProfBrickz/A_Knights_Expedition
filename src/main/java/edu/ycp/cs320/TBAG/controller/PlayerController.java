package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.*;

public class PlayerController {
	private final BattleEntityController battleEntityController;
	private final Player player;

	public PlayerController(Player player, BattleEntityController battleEntityController) {
		this.player = player;
		this.battleEntityController = battleEntityController;
	}

	public Boolean move(String direction) {
		if (player == null || player.getRoom() == null) {
			return false;
		}

		Room currentRoom = player.getRoom();
		RoomConnection connection = currentRoom.getRoomConnections().get(direction);

		if (connection != null && connection.getRoom() != null) {
			Room nextRoom = connection.getRoom();
			player.setRoom(nextRoom);
			return true;
		}

		return false;
	}

	// Throws IllegalArgumentException if armor is not active
	public void equipArmor(Armor armor) throws IllegalArgumentException {
		if (player == null || armor == null) {
			throw new IllegalArgumentException("Player and armor must not be null");
		}

		if (player.getArmor().contains(armor)) {
			throw new IllegalArgumentException("Armor is already equipped");
		}

		player.getArmor().add(armor);
	}

	// Throws IllegalArgumentException if armor is not active
	public void unequipArmor(Armor armor) {
		if (player == null || armor == null) {
			throw new IllegalArgumentException("Player and armor must not be null");
		}

		if (!player.getArmor().contains(armor)) {
			throw new IllegalArgumentException("Armor is not equipped");
		}

		player.getArmor().remove(armor);
	}

	// Adds defence of all equipped armor
	public void getDefence() {
		if (player == null || player.getArmor() == null) {
			return;
		}

		// Placeholder: total defense can be calculated once Armor exposes defense accessors.
	}

	public void die(Integer startingMaxHealth, Integer startingHealth, Room startingRoom) {
		if (player == null) {
			return;
		}

		if (startingMaxHealth != null) {
			player.setMaxHealth(startingMaxHealth);
		}

		if (startingHealth != null) {
			player.setHealth(startingHealth);
		}

		player.setRoom(startingRoom);

		if (player.getState() != PlayerState.EXPLORING) {
			player.setState(PlayerState.EXPLORING);
		}
	}

	public void attack(Enemy enemy, WeaponAbility weaponAbility) {
		if (player == null || enemy == null || battleEntityController == null) {
			return;
		}

		battleEntityController.attack(player, enemy, weaponAbility);
	}

	// Throws IllegalArgumentException if armor is active
	public void defend(Armor armor) throws IllegalArgumentException {
		if (player == null || armor == null) {
			throw new IllegalArgumentException("Player and armor must not be null");
		}

		if (player.getArmor().contains(armor)) {
			throw new IllegalArgumentException("Armor is already equipped");
		}

		if (battleEntityController == null) {
			return;
		}

		battleEntityController.defend(player, armor);
	}

	public void heal(HealingItem healingItem) {
		if (player == null || healingItem == null || battleEntityController == null) {
			return;
		}

		battleEntityController.heal(player, healingItem);
	}

	public Boolean flee() {
		if (player == null) {
			return false;
		}

		if (player.getState() == PlayerState.BATTLE) {
			player.setState(PlayerState.EXPLORING);
			return true;
		}

		return false;
	}

	public String inspectItem(Item item) {
		if (item == null) {
			return null;
		}

		return item.getDescription();
	}
}

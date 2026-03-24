package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Controller for the TBAG game.
 */
public class GameEngine {
	private Player player;
	private HashMap<String, Room> rooms;

	private final PlayerController playerController;
	private final RoomController roomController;
	private final BattleEntityController battleEntityController = new BattleEntityController();
	private final InventoryController inventoryController = new InventoryController();
	private final String defaultRoom = "0";


	// Constructor

	/**
	 * Initializes the player, rooms, and associated controllers.
	 * Loads a demo room if no rooms are provided.
	 * Sets the player's starting room to "0" if not already set.
	 */
	public GameEngine(Player player, HashMap<String, Room> rooms) {
		this.player = player;
		this.rooms = rooms;
		this.playerController = new PlayerController(this.player, this.battleEntityController);
		this.roomController = new RoomController(this.rooms);

		if (this.rooms.isEmpty()) {
			roomController.loadDemo();
		}

		// Set default room
		if (this.player.getRoom() == null) {
			this.player.setRoom(rooms.get(defaultRoom));
		}
	}


	// Getters

	/**
	 * Returns the player object.
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Returns the map of rooms.
	 */
	public HashMap<String, Room> getRooms() {
		return rooms;
	}

	/**
	 * Returns the player controller, for testing.
	 */
	public PlayerController getPlayerController() {
		return playerController;
	}

	/**
	 * Returns the room controller, for testing.
	 */
	public RoomController getRoomController() {
		return roomController;
	}

	/**
	 * Returns the battle entity controller, for testing.
	 */
	public BattleEntityController getBattleEntityController() {
		return battleEntityController;
	}

	/**
	 * Returns the inventory controller, for testing.
	 */
	public InventoryController getInventoryController() {
		return inventoryController;
	}


	// Input command

	/**
	 * Processes user input commands.
	 * Routes commands to the appropriate handler based on the Command enum.
	 */
	public String inputCommand(String command, ArrayList<String> arguments) {
		command = command.trim().toLowerCase();

		String output = "";

		if (command.equals(Command.MOVE.getCommand())) {
			output = this.move(arguments);
		} else if (command.equals(Command.LOOK.getCommand())) {
			output = this.look(arguments);
		} else if (command.equals(Command.INVENTORY.getCommand())) {
			output = this.inventory(arguments);
		} else if (command.equals(Command.INSPECT_ITEM.getCommand())) {
			output = this.inspectItem(arguments);
		} else if (command.equals(Command.SEARCH.getCommand())) {
			output = this.search(arguments);
		} else if (command.equals(Command.PICKUP.getCommand())) {
			output = this.pickupItem(arguments);
		} else if (command.equals(Command.DROP.getCommand())) {
			output = this.dropItem(arguments);
		} else if (command.equals(Command.RESTART.getCommand())) {
			output = this.restart(arguments);
		} else {
			output = "Sorry, command not recognized.\n";
		}

		return output + "\n";
	}


	// Commands

	/**
	 * Handles the "move" command.
	 * Validates direction and updates player position.
	 */
	private String move(ArrayList<String> arguments) {
		String error = validateCommandFormat(Command.MOVE, arguments);
		if (error != null) return error;

		String direction = arguments.get(0).toLowerCase();

		if (!this.roomController.isValidDirection(player.getRoom(), direction)) {
			return "Invalid direction for this room\n";
		}

		Boolean successfulMove = this.playerController.move(direction);
		if (!successfulMove) {
			return "Move failed, either player, or player.getRoom() does not exist\n";
		}

		return this.player.getRoom().getDescription() + "\n";
	}

	/**
	 * Handles the "look" command.
	 * Returns the description of the current room.
	 */
	private String look(ArrayList<String> arguments) {
		String error = validateCommandFormat(Command.LOOK, arguments);
		if (error != null) return error;

		return this.player.getRoom().getDescription() + "\n";
	}

	/**
	 * Handles the "inventory" command.
	 * Lists all items in the player's inventory with quantities.
	 */
	private String inventory(ArrayList<String> arguments) {
		String error = validateCommandFormat(Command.INVENTORY, arguments);
		if (error != null) return error;

		return getInventoryString(player.getInventory(), "Your Inventory", "Empty");
	}

	/**
	 * Handles the "inspect-item" command.
	 * Checks if the item exists in the inventory and returns inspection details.
	 */
	private String inspectItem(ArrayList<String> arguments) {
		String error = validateCommandFormat(Command.INSPECT_ITEM, arguments);
		if (error != null) return error;

		String itemName = arguments.get(0);
		Item item = inventoryController.getItemByName(player.getInventory(), itemName);
		if (item == null) return "You do not have a " + itemName + " in your inventory.\n";

		return playerController.inspectItem(item) + "\n";
	}

	/**
	 * Handles the "search" command.
	 * Checks if the room has any items and returns them.
	 */
	private String search(ArrayList<String> arguments) {
		String error = validateCommandFormat(Command.SEARCH, arguments);
		if (error != null) return error;

		return getInventoryString(player.getRoom().getInventory(), "You found", "Nothing!");
	}

	/**
	 * Handles the "pickup" command.
	 * Checks if the item is in the room and adds it to the player's inventory.
	 */
	private String pickupItem(ArrayList<String> arguments) {
		String error = validateCommandFormat(Command.PICKUP, arguments);
		if (error != null) return error;

		String itemName = arguments.get(0);
		Item item = inventoryController.getItemByName(player.getRoom().getInventory(), itemName);
		if (item == null) return "This room does not have a " + itemName + ".\n";

		inventoryController.removeItem(player.getRoom().getInventory(), item.getId(), item.getAmount());
		inventoryController.addItem(player.getInventory(), item, item.getAmount());

		String output = "You picked up ";

		if (item.getAmount() == 1) {
			output += "a " + itemName;
		} else {
			output += item.getAmount() + " " + itemName + "s";
		}
		output += ".\n";

		return output;
	}

	/**
	 * Handles the 'drop' command.
	 * Checks if the player has the item and drops it in the current room.
	 */
	private String dropItem(ArrayList<String> arguments) {
		String error = validateCommandFormat(Command.PICKUP, arguments);
		if (error != null) return error;

		String itemName = arguments.get(0);
		Item item = inventoryController.getItemByName(player.getInventory(), itemName);
		if (item == null) return "This room does not have a " + itemName + ".\n";

		inventoryController.removeItem(player.getInventory(), item.getId(), item.getAmount());
		inventoryController.addItem(player.getRoom().getInventory(), item, item.getAmount());

		String output = "You dropped ";
		if (item.getAmount() == 1) {
			output += "a " + itemName;
		} else {
			output += item.getAmount() + " " + itemName + "s";
		}
		output += ".\n";

		return output;
	}

	private String restart(ArrayList<String> arguments) {
		String error = validateCommandFormat(Command.RESTART, arguments);
		if (error != null) return error;

		rooms.clear();
		roomController.loadDemo();

		player.getInventory().getItems().clear();
		player.getArmor().clear();
		player.setState(PlayerState.EXPLORING);

		// Set default room
		player.setRoom(rooms.get(defaultRoom));

		return "Restarted game.\n";
	}


	// Utility methods

	/**
	 * Makes a string list of items in an inventory.
	 */
	private String getInventoryString(Inventory inventory, String startingPhrase, String emptyPhrase) {
		StringBuilder itemList = new StringBuilder(startingPhrase);
		itemList.append(":\n");

		if (inventory.getItems().isEmpty()) {
			itemList
				.append(emptyPhrase)
				.append("\n");
			return itemList.toString();
		}

		for (Item item : inventory.getItems().values()) {
			itemList.append("- ")
				.append(item.getAmount())
				.append(" x ")
				.append(item.getName());
			if (item.getAmount() > 1) itemList.append("s");

			itemList.append("\n");
		}

		return itemList.toString();
	}

	/**
	 * Validates the command format by comparing argument count to expected format.
	 * Returns an error message if the format is invalid.
	 */
	private String validateCommandFormat(Command command, ArrayList<String> arguments) {
		if (arguments.size() != command.getArguments().size()) {
			return "Invalid " + command.getCommand() + " command. Must be in the format:\n" + command.getFormat() + "\n";
		}

		return null;
	}
}

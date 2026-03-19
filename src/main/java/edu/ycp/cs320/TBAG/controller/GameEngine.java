package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.Player;
import edu.ycp.cs320.TBAG.model.Room;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Controller for the TBAG game.
 */
public class GameEngine {
	private final Player player;
	private final HashMap<String, Room> rooms;
	private final PlayerController playerController;
	private final RoomController roomController;
	private final BattleEntityController battleEntityController = new BattleEntityController();
	private final InventoryController inventoryController = new InventoryController();

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
			this.player.setRoom(rooms.get("0"));
		}
	}

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
	 * Processes user input commands.
	 * Routes commands to the appropriate handler based on the Command enum.
	 */
	public String inputCommand(String command, ArrayList<String> arguments) {
		command = command.trim().toLowerCase();

		if (command.equals(Command.MOVE.getCommand())) {
			return this.move(arguments);
		} else if (command.equals(Command.LOOK.getCommand())) {
			return this.look(arguments);
		} else if (command.equals(Command.INVENTORY.getCommand())) {
			return this.inventory(arguments);
		} else if (command.equals(Command.INSPECT_ITEM.getCommand())) {
			return this.inspectItem(arguments);
		} else {
			return "Sorry, command not recognized.\n";
		}
	}

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

		StringBuilder inventory = new StringBuilder("Inventory:\n");

		for (Item item : player.getInventory().getItems().values()) {
			inventory.append("- ");

			if (item.getAmount() > 1) {
				inventory
					.append(item.getAmount())
					.append(" x ");
			}

			inventory
				.append(item.getName())
				.append("\n");
		}

		return inventory.toString();
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
		if (item == null) return "You do not have" + itemName + " in your inventory.\n";

		return playerController.inspectItem(item) + "\n";
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

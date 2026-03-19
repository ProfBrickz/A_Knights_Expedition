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

	public GameEngine(Player player, HashMap<String, Room> rooms) {
		this.player = player;
		this.rooms = rooms;
		this.playerController = new PlayerController(this.player, this.battleEntityController);
		this.roomController = new RoomController(this.rooms);

		if (this.rooms.isEmpty()) {
			roomController.loadDemo();
		}

		if (this.player.getRoom() == null) {
			this.player.setRoom(rooms.get("0"));
		}
	}

	public Player getPlayer() {
		return player;
	}

	public HashMap<String, Room> getRooms() {
		return rooms;
	}

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

	private String look(ArrayList<String> arguments) {
		String error = validateCommandFormat(Command.LOOK, arguments);
		if (error != null) return error;

		return this.player.getRoom().getDescription() + "\n";
	}

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

	private String inspectItem(ArrayList<String> arguments) {
		String error = validateCommandFormat(Command.INSPECT_ITEM, arguments);
		if (error != null) return error;

		String itemName = arguments.get(0);
		Item item = inventoryController.getItemByName(player.getInventory(), itemName);
		if (item == null) return "You do not have" + itemName + " in your inventory.\n";

		return playerController.inspectItem(item) + "\n";
	}

	private String validateCommandFormat(Command command, ArrayList<String> arguments) {
		if (arguments.size() != command.getArguments().size()) {
			return "Invalid " + command.getCommand() + " command. Must be in the format:\n" + command.getFormat() + "\n";
		}

		return null;
	}
}

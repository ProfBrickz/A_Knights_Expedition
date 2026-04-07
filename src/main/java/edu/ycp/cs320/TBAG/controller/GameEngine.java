package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Controller for the TBAG game.
 */
public class GameEngine {
	private final Player player;
	private final HashMap<Integer, Room> rooms;

	private final PlayerController playerController;
	private final RoomController roomController;
	private final BattleEntityController battleEntityController = new BattleEntityController();
	private final InventoryController inventoryController = new InventoryController();
	private final NPCController npcController = new NPCController(inventoryController);
	private final Integer defaultRoom = 0;


	// Constructor

	/**
	 * Initializes the player, rooms, and associated controllers.
	 * Loads a demo room if no rooms are provided.
	 * Sets the player's starting room to "0" if not already set.
	 */
	public GameEngine(Player player, HashMap<Integer, Room> rooms) {
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


	// Input command

	/**
	 * Processes user input commands.
	 * Routes commands to the appropriate handler based on the Command enum.
	 */
	public String inputCommand(String commandName, ArrayList<String> arguments) {
		commandName = commandName.trim().toLowerCase();

		String output = "";

		for (Command command : Command.values()) {
			if (command.getName().equals(commandName)) {
				output = command.run(this, arguments);
				break;
			}
		}
		if (output.isEmpty()) output = "Sorry, command not recognized.\n";

		return output + "\n";
	}


	// Commands

	/**
	 * Handles the "move" command.
	 * Validates direction and updates player position.
	 */
	public String move(ArrayList<String> arguments) {
		String direction = arguments.get(0).toLowerCase();

		if (!this.roomController.isValidDirection(player.getRoom(), direction)) {
			return "Invalid direction for this room\n";
		}

		Boolean successfulMove = this.playerController.move(direction);
		if (!successfulMove) {
			return "Move failed, either player, or the room does not exist\n";
		}

		return this.player.getRoom().getDescription() + "\n";
	}

	/**
	 * Handles the "look" command.
	 * Returns the description of the current room.
	 */
	public String look(ArrayList<String> arguments) {
		return this.player.getRoom().getDescription() + "\n";
	}

	/**
	 * Handles the "inventory" command.
	 * Lists all items in the player's inventory with quantities.
	 */
	public String inventory(ArrayList<String> arguments) {
		return getInventoryString(player.getInventory(), "Your Inventory", "Empty");
	}

	/**
	 * Handles the "inspect-item" command.
	 * Checks if the item exists in the inventory and returns inspection details.
	 */
	public String inspectItem(ArrayList<String> arguments) {
		String itemName = arguments.get(0);
		Item item = inventoryController.getItemByNameCaseInsensitive(player.getInventory(), itemName);
		if (item == null) return "You do not have a " + itemName + " in your inventory.\n";

		return playerController.inspectItem(item) + "\n";
	}

	/**
	 * Handles the "search" command.
	 * Checks if the room has any items and returns them.
	 */
	public String search(ArrayList<String> arguments) {
		return getInventoryString(player.getRoom().getInventory(), "You found", "Nothing!");
	}

	/**
	 * Handles the "pickup" command.
	 * Checks if the item is in the room and adds it to the player's inventory.
	 */
	public String pickupItem(ArrayList<String> arguments) {
		Room playerRoom = player.getRoom();

		String itemName = arguments.get(0).toLowerCase();
		Item item = inventoryController.getItemByNameCaseInsensitive(playerRoom.getInventory(), itemName);
		if (item == null) return "This room does not have a " + itemName + ".\n";

		inventoryController.removeItem(playerRoom.getInventory(), item, item.getAmount());
		inventoryController.addItem(player.getInventory(), item, item.getAmount());

		return "You picked up "
			+ getItemFormat(item)
			+ ".\n";
	}

	public String pickupAllItems(ArrayList<String> arguments) {
		Room playerRoom = player.getRoom();

		if (playerRoom.getInventory().getItems().isEmpty()) {
			return "You did not pick anything up from this room.\n";
		}

		StringBuilder output = new StringBuilder("You picked up:\n");

		Iterator<Item> itemIterator = playerRoom.getInventory().getItems().values().iterator();

		while (itemIterator.hasNext()) {
			Item item = itemIterator.next();

			//inventoryController.removeItem(playerRoom.getInventory(), item.getId(), item.getAmount());
			itemIterator.remove();
			inventoryController.addItem(player.getInventory(), item, item.getAmount());

			output
				.append(item.getAmount())
				.append(" x ")
				.append(item.getName())
				.append("\n");
		}

		return output.toString();
	}

	/**
	 * Handles the 'drop' command.
	 * Checks if the player has the item and drops it in the current room.
	 */
	public String dropItem(ArrayList<String> arguments) {
		String itemName = arguments.get(0).toLowerCase();
		Item item = inventoryController.getItemByNameCaseInsensitive(player.getInventory(), itemName);
		if (item == null) return "You do not have a " + itemName + ".\n";

		inventoryController.removeItem(player.getInventory(), item, item.getAmount());
		inventoryController.addItem(player.getRoom().getInventory(), item, item.getAmount());

		return "You dropped "
			+ getItemFormat(item)
			+ ".\n";
	}

	public String dropAllItems(ArrayList<String> arguments) {
		if (player.getInventory().getItems().isEmpty()) {
			return "You do not have anything to drop.\n";
		}

		Room playerRoom = player.getRoom();
		StringBuilder output = new StringBuilder("You dropped:\n");

		Iterator<Item> itemIterator = player.getInventory().getItems().values().iterator();

		while (itemIterator.hasNext()) {
			Item item = itemIterator.next();

			itemIterator.remove();
			inventoryController.addItem(playerRoom.getInventory(), item, item.getAmount());

			output
				.append(item.getAmount())
				.append(" x ")
				.append(item.getName())
				.append("\n");
		}

		return output.toString();
	}

	public String wallet(ArrayList<String> arguments) {
		String output = "You have " + player.getCoins() + " coin";
		if (player.getCoins() != 1) output += "s";

		return output + ".\n";
	}

	public String talkToNPC(ArrayList<String> arguments) {
		Room playerRoom = player.getRoom();

		String npcName = arguments.get(0);
		NPC npc = roomController.getNPCByNameCaseInsensitive(playerRoom, npcName);
		if (npc == null) return npcName + " is not in this room.\n";

		player.setCurrentNPC(npc);
		player.setState(PlayerState.TALKING_TO_NPC);

		return npc.getGreeting() + "\n";
	}

	public String leaveNPC(ArrayList<String> arguments) {
		NPC npc = player.getCurrentNPC();
		if (npc == null) return "You are not currently talking to an NPC.\n";

		String goodbye = npc.getGoodbye() + "\n";

		player.setCurrentNPC(null);
		player.setState(PlayerState.EXPLORING);

		return goodbye;
	}

	public String searchShop(ArrayList<String> arguments) {
		NPC npc = player.getCurrentNPC();

		if (npc.getInventory().getItems().isEmpty()) return "I am not selling anything.\n";

		StringBuilder output = new StringBuilder("I am selling:\n");

		for (Item item : npc.getInventory().getItems().values()) {
			output
				.append("- ")
				.append(item.getAmount())
				.append(" x ")
				.append(item.getName())
				.append(" for ")
				.append(item.getPrice() * item.getAmount())
				.append(" coins\n");
		}

		return output.toString();
	}

	public String buyItem(ArrayList<String> arguments) {
		NPC npc = player.getCurrentNPC();
		if (npc == null) return "You are not currently talking to an NPC.\n";

		String itemName = arguments.get(1).toLowerCase();
		Item item = inventoryController.getItemByNameCaseInsensitive(npc.getInventory(), itemName);
		if (item == null) return "I am not selling any " + itemName + "s.\n";

		Integer amount = null;
		try {
			amount = Integer.parseInt(arguments.get(0));
		} catch (NumberFormatException ignored) {
		}
		if (amount == null) return arguments.get(0) + " is not a valid amount.\n";
		if (player.getCoins() < item.getPrice() * amount) {
			return "You are too poor to buy " + amount + " x " + item.getName() + ".\n";
		}

		npcController.buy(npc, player, item, amount);

		return "You bought " + amount + " x " + item.getName() + ", -" + item.getPrice() * amount + " coins.\n";
	}

	public String sellItem(ArrayList<String> arguments) {
		NPC npc = player.getCurrentNPC();
		if (npc == null) return "You are not currently talking to an NPC.\n";

		String itemName = arguments.get(1).toLowerCase();
		Item item = inventoryController.getItemByNameCaseInsensitive(player.getInventory(), itemName);
		if (item == null) return "You do not have any " + itemName + " to sell.\n";

		Integer amount = null;
		try {
			amount = Integer.parseInt(arguments.get(0));
		} catch (NumberFormatException ignored) {
		}
		if (amount == null) return arguments.get(0) + " is not a valid amount.\n";
		if (item.getAmount() < amount) {
			return "You do not have " + amount + " of " + item.getName() + ".\n";
		}

		npcController.sell(player, item, amount);

		return "You sold " + amount + " x " + item.getName() + ", +" + item.getValue() * amount + " coins.\n";
	}

	public String sellAllItem(ArrayList<String> arguments) {
		NPC npc = player.getCurrentNPC();
		if (npc == null) return "You are not currently talking to an NPC.\n";

		String itemName = arguments.get(0).toLowerCase();
		Item item = inventoryController.getItemByNameCaseInsensitive(player.getInventory(), itemName);
		if (item == null) return "You do not have any " + itemName + " to sell.\n";

		Integer amount = item.getAmount();

		npcController.sell(player, item, amount);

		return "You sold " + amount + " x " + item.getName() + ", +" + item.getValue() * amount + " coins.\n";
	}

	public String restart(ArrayList<String> arguments) {
		rooms.clear();
		roomController.loadDemo();

		player.getInventory().getItems().clear();
		player.getArmor().clear();
		player.setState(PlayerState.EXPLORING);
		player.setCoins(0);
		ItemCatalog.addBaseItemsToInventory(player.getInventory());

		// Set default room
		player.setRoom(rooms.get(defaultRoom));

		return "Restarted game.\n";
	}

	public String help(ArrayList<String> arguments) {
		StringBuilder output = new StringBuilder("Available commands:\n");

		for (Command cmd : Command.values()) {
			output.append("- ").append(cmd.getFormat()).append("\n");
		}

		return output.toString();
	}


	// Utility methods

	private String getItemFormat(Item item) {
		String output = "";

		output += item.getAmount()
			+ " "
			+ item.getName();

		if (item.getAmount() > 1) output += "s";

		return output;
	}

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

	public String validateCommand(Command command, ArrayList<String> arguments) {
		String error = validateCommandState(command);
		if (error != null) return error;

		error = validateCommandFormat(command, arguments);
		return error;
	}

	/**
	 * Validates the command format by comparing argument count to expected format.
	 * Returns an error message if the format is invalid.
	 */
	private String validateCommandFormat(Command command, ArrayList<String> arguments) {
		if (arguments.size() != command.getArguments().size()) {
			return "Invalid " + command.getName() + " command. Must be in the format:\n" + command.getFormat() + "\n";
		}

		return null;
	}

	private String validateCommandState(Command command) {
		if (!command.getAllowedPlayerStates().contains(player.getState())) {
			return "You are not allowed to use " + command.getName() + " while " + player.getState().getName() + ".\n";
		}

		return null;
	}
}

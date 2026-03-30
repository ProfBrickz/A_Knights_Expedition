package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
	private final NPCController npcController = new NPCController(inventoryController);
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

		if (command.equals(Command.MOVE.getName())) {
			output = move(arguments);
		} else if (command.equals(Command.LOOK.getName())) {
			output = look(arguments);
		} else if (command.equals(Command.INVENTORY.getName())) {
			output = inventory(arguments);
		} else if (command.equals(Command.INSPECT_ITEM.getName())) {
			output = inspectItem(arguments);
		} else if (command.equals(Command.SEARCH.getName())) {
			output = search(arguments);
		} else if (command.equals(Command.PICKUP.getName())) {
			output = pickupItem(arguments);
		} else if (command.equals(Command.PICKUP_ALL.getName())) {
			output = pickupAllItems(arguments);
		} else if (command.equals(Command.DROP.getName())) {
			output = dropItem(arguments);
		} else if (command.equals(Command.DROP_ALL.getName())) {
			output = dropAllItems(arguments);
		} else if (command.equals(Command.WALLET.getName())) {
			output = wallet(arguments);
		} else if (command.equals(Command.TALK_TO.getName())) {
			output = talkToNPC(arguments);
		} else if (command.equals(Command.LEAVE.getName())) {
			output = leaveNPC(arguments);
		} else if (command.equals(Command.SEARCH_SHOP.getName())) {
			output = searchShop(arguments);
		} else if (command.equals(Command.BUY.getName())) {
			output = buyItem(arguments);
		} else if (command.equals(Command.SELL.getName())) {
			output = sellItem(arguments);
		} else if (command.equals(Command.SELL_ALL.getName())) {
			output = sellAllItem(arguments);
		} else if (command.equals(Command.RESTART.getName())) {
			output = restart(arguments);
		} else if (command.equals(Command.HELP.getName())) {
			output = help(arguments);
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
		String error = validateCommand(Command.MOVE, arguments);
		if (error != null) return error;

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
	private String look(ArrayList<String> arguments) {
		String error = validateCommand(Command.LOOK, arguments);
		if (error != null) return error;

		return this.player.getRoom().getDescription() + "\n";
	}

	/**
	 * Handles the "inventory" command.
	 * Lists all items in the player's inventory with quantities.
	 */
	private String inventory(ArrayList<String> arguments) {
		String error = validateCommand(Command.INVENTORY, arguments);
		if (error != null) return error;

		return getInventoryString(player.getInventory(), "Your Inventory", "Empty");
	}

	/**
	 * Handles the "inspect-item" command.
	 * Checks if the item exists in the inventory and returns inspection details.
	 */
	private String inspectItem(ArrayList<String> arguments) {
		String error = validateCommand(Command.INSPECT_ITEM, arguments);
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
		String error = validateCommand(Command.SEARCH, arguments);
		if (error != null) return error;

		return getInventoryString(player.getRoom().getInventory(), "You found", "Nothing!");
	}

	/**
	 * Handles the "pickup" command.
	 * Checks if the item is in the room and adds it to the player's inventory.
	 */
	private String pickupItem(ArrayList<String> arguments) {
		String error = validateCommand(Command.PICKUP, arguments);
		if (error != null) return error;

		Room playerRoom = player.getRoom();

		String itemName = arguments.get(0);
		Item item = inventoryController.getItemByName(playerRoom.getInventory(), itemName);
		if (item == null) return "This room does not have a " + itemName + ".\n";

		inventoryController.removeItem(playerRoom.getInventory(), item.getId(), item.getAmount());
		inventoryController.addItem(player.getInventory(), item, item.getAmount());

		return "You picked up "
			+ getItemFormat(item)
			+ ".\n";
	}

	private String pickupAllItems(ArrayList<String> arguments) {
		String error = validateCommand(Command.PICKUP_ALL, arguments);
		if (error != null) return error;

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
	private String dropItem(ArrayList<String> arguments) {
		String error = validateCommand(Command.DROP, arguments);
		if (error != null) return error;

		String itemName = arguments.get(0);
		Item item = inventoryController.getItemByName(player.getInventory(), itemName);
		if (item == null) return "You do not have a " + itemName + ".\n";

		inventoryController.removeItem(player.getInventory(), item.getId(), item.getAmount());
		inventoryController.addItem(player.getRoom().getInventory(), item, item.getAmount());

		return "You dropped "
			+ getItemFormat(item)
			+ ".\n";
	}

	private String dropAllItems(ArrayList<String> arguments) {
		String error = validateCommand(Command.DROP_ALL, arguments);
		if (error != null) return error;

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

	private String wallet(ArrayList<String> arguments) {
		String output = "You have " + player.getCoins() + " coin";
		if (player.getCoins() != 1) output += "s";

		return output += ".\n";
	}

	private String talkToNPC(ArrayList<String> arguments) {
		String error = validateCommand(Command.TALK_TO, arguments);
		if (error != null) return error;

		Room playerRoom = player.getRoom();

		String npcName = arguments.get(0);
		NPC npc = roomController.getNPCByName(playerRoom, npcName);
		if (npc == null) return npcName + " is not in this room.\n";

		player.setCurrentNPC(npc);
		player.setState(PlayerState.TALKING_TO_NPC);

		return npc.getGreeting() + "\n";
	}

	private String leaveNPC(ArrayList<String> arguments) {
		String error = validateCommand(Command.LEAVE, arguments);
		if (error != null) return error;

		NPC npc = player.getCurrentNPC();
		if (npc == null) return "You are not currently talking to an NPC.\n";

		String goodbye = npc.getGoodbye() + "\n";

		player.setCurrentNPC(null);
		player.setState(PlayerState.EXPLORING);

		return goodbye;
	}

	private String searchShop(ArrayList<String> arguments) {
		String error = validateCommand(Command.SEARCH_SHOP, arguments);
		if (error != null) return error;

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

	private String buyItem(ArrayList<String> arguments) {
		String error = validateCommand(Command.BUY, arguments);
		if (error != null) return error;

		NPC npc = player.getCurrentNPC();
		if (npc == null) return "You are not currently talking to an NPC.\n";

		String itemName = arguments.get(0);
		Item item = inventoryController.getItemByName(npc.getInventory(), itemName);
		if (item == null) return "I am not selling any " + itemName + "s.\n";

		Integer amount = null;
		try {
			amount = Integer.parseInt(arguments.get(1));
		} catch (NumberFormatException _) {
		}
		if (amount == null) return arguments.get(1) + " is not a valid amount.\n";
		if (player.getCoins() < item.getPrice() * amount) {
			return "You are too poor to buy " + amount + " x " + item.getName() + ".\n";
		}

		npcController.buy(npc, player, item.getId(), amount);

		return "You bought " + amount + " x " + item.getName() + ", -" + item.getPrice() * amount + " coins.\n";
	}

	private String sellItem(ArrayList<String> arguments) {
		String error = validateCommand(Command.SELL, arguments);
		if (error != null) return error;

		NPC npc = player.getCurrentNPC();
		if (npc == null) return "You are not currently talking to an NPC.\n";

		String itemName = arguments.get(0);
		Item item = inventoryController.getItemByName(player.getInventory(), itemName);
		if (item == null) return "You do not have any " + itemName + " to sell.\n";

		Integer amount = null;
		try {
			amount = Integer.parseInt(arguments.get(1));
		} catch (NumberFormatException _) {
		}
		if (amount == null) return arguments.get(1) + " is not a valid amount.\n";
		if (item.getAmount() < amount) {
			return "You do not have " + amount + " of " + item.getName() + ".\n";
		}

		npcController.sell(player, item, amount);

		return "You sold " + amount + " x " + item.getName() + ", +" + item.getValue() * amount + " coins.\n";
	}

	private String sellAllItem(ArrayList<String> arguments) {
		String error = validateCommand(Command.SELL_ALL, arguments);
		if (error != null) return error;

		NPC npc = player.getCurrentNPC();
		if (npc == null) return "You are not currently talking to an NPC.\n";

		String itemName = arguments.get(0);
		Item item = inventoryController.getItemByName(player.getInventory(), itemName);
		if (item == null) return "You do not have any " + itemName + " to sell.\n";

		Integer amount = item.getAmount();

		npcController.sell(player, item, amount);

		return "You sold " + amount + " x " + item.getName() + ", +" + item.getValue() * amount + " coins.\n";
	}

	private String restart(ArrayList<String> arguments) {
		String error = validateCommand(Command.RESTART, arguments);
		if (error != null) return error;

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

	private String help(ArrayList<String> arguments) {
		String error = validateCommand(Command.HELP, arguments);
		if (error != null) return error;

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

	private String validateCommand(Command command, ArrayList<String> arguments) {
		String error = validateCommandState(command, arguments);
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

	private String validateCommandState(Command command, ArrayList<String> arguments) {
		if (!command.getAllowedPlayerStates().contains(player.getState())) {
			return "You are not allowed to use " + command.getName() + " while " + player.getState().getName() + ".\n";
		}

		return null;
	}
}

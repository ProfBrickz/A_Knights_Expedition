package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.*;
import edu.ycp.cs320.TBAG.persist.Database;
import edu.ycp.cs320.TBAG.persist.DatabaseProvider;
import edu.ycp.cs320.TBAG.persist.DerbyDatabase;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Controller for the TBAG game.
 */
public class GameEngine {
	private final Database database;
	private Player player;
	private final InventoryController inventoryController = new InventoryController();
	private final NPCController npcController = new NPCController(inventoryController);

	// Constructor
	public GameEngine(Database database) {
		DatabaseProvider.setInstance(database);
		this.database = DatabaseProvider.getInstance();

		getPlayer();
	}

	/**
	 * Initializes the player, rooms, and associated controllers.
	 * Loads a demo room if no rooms are provided.
	 * Sets the player's starting room to "0" if not already set.
	 */
	public GameEngine() {
		this(new DerbyDatabase());
	}


	// Getters and setters
	public String getDialog() {
		StringBuilder output = new StringBuilder();

		ArrayList<String> dialog = database.getDialog();

		for (String text : dialog) {
			output
				.append(text)
				.append("\n\n");
		}

		return output.toString();
	}

	public void addDialog(String text) {
		database.addDialog(text);
	}

	public Player getPlayer() {
		player = database.getPlayer();
		player.getInventory().addItems((database.getItemsForPlayer()));

		return player;
	}

	public ArrayList<String> getCommandHistory() {
		return database.getCommandHistory();
	}

	public void addCommandToHistory(String command) {
		database.addCommandToHistory(command);
	}

	// Input command

	/**
	 * Processes user input commands.
	 * Routes commands to the appropriate handler based on the Command enum.
	 */
	public String inputCommand(String commandName, ArrayList<String> arguments) {
		commandName = commandName.trim().toLowerCase();

		player = this.database.getPlayer();
		player.getInventory().addItems((this.database.getItemsForPlayer()));

		for (Command command : Command.values()) {
			if (!command.getName().equals(commandName)) continue;

			String error = validateCommand(command, arguments);
			if (error != null) return error;

			Boolean confirming = player.getConfirming();

			if (!confirming) {
				player.setLastCommand(command);
			}

			String output = command.run(this, arguments);

			database.setLastCommand(command);
			return output;
		}
		return "Sorry, command not recognized.";
	}


	// Commands

	/**
	 * Handles the "move" command.
	 * Validates direction and updates player position.
	 */
	public String move(ArrayList<String> arguments) {
		String direction = arguments.get(0).toLowerCase();

		player.getRoom().setRoomConnections(database.getConnectionsForRoom(player.getRoom()));
		RoomController roomController = new RoomController(new HashMap<>());
		PlayerController playerController = new PlayerController(player, new BattleEntityController());

		if (!roomController.isValidDirection(player.getRoom(), direction)) {
			return "Invalid direction for this room";
		}

		Boolean successfulMove = playerController.move(direction);
		if (!successfulMove) {
			return "Move failed, either player, or the room does not exist";
		}

		database.setPlayerRoom(player.getRoom().getId());

		return player.getRoom().getDescription();
	}

	/**
	 * Handles the "look" command.
	 * Returns the description of the current room.
	 */
	public String look(ArrayList<String> arguments) {
		Room playerRoom = player.getRoom();
		StringBuilder output = new StringBuilder(playerRoom.getDescription());

		if (!playerRoom.getNpcs().isEmpty()) {
			if (!output.isEmpty()) output.append("\n");
			output.append("You see:\n");
		}
		for (NPC npc : playerRoom.getNpcs().values()) {
			output
				.append("- ")
				.append(npc.getName())
				.append("\n");
		}

		return output.toString();
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
		PlayerController playerController = new PlayerController(player, new BattleEntityController());

		String itemName = arguments.get(0);
		Item item = inventoryController.getItemByNameCaseInsensitive(player.getInventory(), itemName);
		if (item == null) return "You do not have a " + itemName + " in your inventory.";

		return playerController.inspectItem(item);
	}

	/**
	 * Handles the "search" command.
	 * Checks if the room has any items and returns them.
	 */
	public String search(ArrayList<String> arguments) {
		Room playerRoom = player.getRoom();

		playerRoom.getInventory().addItems(database.getItemsForRoom(playerRoom));

		return getInventoryString(playerRoom.getInventory(), "You found", "Nothing!");
	}

	/**
	 * Handles the "pickup" command.
	 * Checks if the item is in the room and adds it to the player's inventory.
	 */
	public String pickupItem(ArrayList<String> arguments) {
		Room playerRoom = player.getRoom();
		playerRoom.getInventory().addItems(database.getItemsForRoom(playerRoom));

		String itemName = arguments.get(0).toLowerCase();
		Item item = inventoryController.getItemByNameCaseInsensitive(playerRoom.getInventory(), itemName);
		if (item == null) return "This room does not have a " + itemName + ".";

		database.removeItemFromRoom(playerRoom, item);
		database.addItemToPlayer(item);

		return "You picked up " + getItemFormat(item) + ".";
	}

	public String pickupAllItems(ArrayList<String> arguments) {
		Room playerRoom = player.getRoom();
		playerRoom.getInventory().addItems(database.getItemsForRoom(playerRoom));

		if (playerRoom.getInventory().getItems().isEmpty()) {
			return "You did not pick anything up from this room.";
		}

		StringBuilder output = new StringBuilder("You picked up:\n");

		for (Item item : playerRoom.getInventory().getItems().values()) {
			database.removeItemFromRoom(playerRoom, item);
			database.addItemToPlayer(item);

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
		Room playerRoom = player.getRoom();
		playerRoom.getInventory().addItems(database.getItemsForRoom(playerRoom));

		String itemName = arguments.get(0).toLowerCase();
		Item item = inventoryController.getItemByNameCaseInsensitive(player.getInventory(), itemName);
		if (item == null) return "You do not have a " + itemName + ".";

		database.removeItemFromPlayer(item);
		database.addItemToRoom(playerRoom, item);

		return "You dropped " + getItemFormat(item) + ".";
	}

	public String dropAllItems(ArrayList<String> arguments) {
		if (player.getInventory().getItems().isEmpty()) {
			return "You do not have anything to drop.";
		}

		Room playerRoom = player.getRoom();
		playerRoom.getInventory().addItems(database.getItemsForRoom(playerRoom));

		StringBuilder output = new StringBuilder("You dropped:\n");

		for (Item item : player.getInventory().getItems().values()) {
			database.removeItemFromPlayer(item);
			database.addItemToRoom(playerRoom, item);

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

		return output + ".";
	}

	public String talkToNPC(ArrayList<String> arguments) {
		HashMap<Integer, NPC> npcs = database.getNPCsForRoom(player.getRoom());
		String npcName = arguments.get(0);
		NPC currentNPC = null;

		for (NPC npc : npcs.values()) {
			if (npc.getName().equalsIgnoreCase(npcName)) {
				currentNPC = npc;
			}
		}

		if (currentNPC == null) return npcName + " is not in this room.";

		database.setPlayerNPC(currentNPC);
		database.setPlayerState(PlayerState.TALKING_TO_NPC);

		return currentNPC.getGreeting();
	}

	public String leaveNPC(ArrayList<String> arguments) {
		NPC npc = database.getNpcForPlayer();
		if (npc == null) return "You are not currently talking to an NPC.";

		String goodbye = npc.getGoodbye();

		database.setPlayerNPC(null);
		database.setPlayerState(PlayerState.EXPLORING);

		return goodbye;
	}

	public String searchShop(ArrayList<String> arguments) {
		NPC npc = database.getNpcForPlayer();
		HashMap<Integer, Item> npcItems = database.getItemsForNPC(npc);

		if (npcItems == null || npcItems.isEmpty()) return "I am not selling anything.";

		StringBuilder output = new StringBuilder("I am selling:\n");

		for (Item item : npcItems.values()) {
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
		NPC npc = database.getNpcForPlayer();
		HashMap<Integer, Item> npcItems = database.getItemsForNPC(npc);


		if (npc == null) return "You are not currently talking to an NPC.";
		if (npcItems == null) return "I am not selling anything.";

		String itemName = arguments.get(1).toLowerCase();
		for (Item item : npcItems.values()) {
			if (!item.getName().equals(itemName)) continue;

			Integer amount = null;
			try {
				amount = Integer.parseInt(arguments.get(0));
			} catch (NumberFormatException ignored) {
			}

			if (amount == null) return arguments.get(0) + " is not a valid amount.";
			if (player.getCoins() < item.getPrice() * amount) {
				return "You are too poor to buy " + amount + " x " + item.getName() + ".";
			}

			database.setPlayerCoins(player.getCoins() - (item.getPrice() * amount));
			database.addItemToPlayer(item);
			return "You bought " + amount + " x " + item.getName() + ", -" + item.getPrice() * amount + " coins.";
		}

		return "I am not selling any " + itemName + "s.";
	}

	public String sellItem(ArrayList<String> arguments) {
		NPC npc = database.getNpcForPlayer();
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
		database.removeItemFromPlayer(item);
		database.setPlayerCoins(player.getCoins()+(item.getValue()*amount));

		return "You sold " + amount + " x " + item.getName() + ", +" + item.getValue() * amount + " coins.\n";
	}

//	public String sellAllItem(ArrayList<String> arguments) {
//		NPC npc = player.getCurrentNPC();
//		if (npc == null) return "You are not currently talking to an NPC.\n";
//
//		String itemName = arguments.get(0).toLowerCase();
//		Item item = inventoryController.getItemByNameCaseInsensitive(player.getInventory(), itemName);
//		if (item == null) return "You do not have any " + itemName + " to sell.\n";
//
//		Integer amount = item.getAmount();
//
//		npcController.sell(player, item, amount);
//
//		return "You sold " + amount + " x " + item.getName() + ", +" + item.getValue() * amount + " coins.\n";
//	}

	public String restart(ArrayList<String> arguments) {
		if (!player.getConfirming()) {
			database.setConfirming(true);
			return "Are you sure (yes or no)?\n";
		}

		Boolean success = database.reset();

		if (!success) return "Reset failed, try again.\n";

		return "Restarted game.\n";
	}

	public String yes(ArrayList<String> arguments) {
		Command lastCommand = player.getLastCommand();

		String output = lastCommand.run(this, arguments);

		database.setConfirming(false);

		return output;
	}

	public String no(ArrayList<String> arguments) {
		Command lastCommand = player.getLastCommand();

		database.setConfirming(false);

		return lastCommand.getName() + " command canceled.\n";
	}

	public String help(ArrayList<String> arguments) {
		StringBuilder output = new StringBuilder("Available commands:\n");

		for (Command cmd : Command.values()) {
			output.append("- ").append(cmd.getFormat()).append("\n");
		}

		return output.toString();
	}

	public String clear(ArrayList<String> arguments) {
		database.clearDialog();

		return null;
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
		String error = validatePlayerState(command);
		if (error != null) return error;

		error = validateConfirming(command);
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
			return "Invalid "
				+ command.getName()
				+ " command. Must be in the format:\n"
				+ command.getFormat();
		}

		return null;
	}

	private String validatePlayerState(Command command) {
		if (!command.getAllowedPlayerStates().contains(player.getState())) {
			return "You are not allowed to use "
				+ command.getName()
				+ " while "
				+ player.getState().getName()
				+ ".";
		}

		return null;
	}

	private String validateConfirming(Command command) {
		Boolean confirming = player.getConfirming();
		Boolean isConfirmationCommand = command == Command.YES || command == Command.NO;

		if (confirming && !isConfirmationCommand) {
			return "You can not use " + command.getName() + " while confirming a command use yes or no.";
		} else if (!confirming && isConfirmationCommand) {
			return "There is nothing to confirm.";
		}

		return null;
	}
}

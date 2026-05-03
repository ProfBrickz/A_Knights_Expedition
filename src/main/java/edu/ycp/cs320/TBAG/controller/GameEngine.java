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
	private final RoomController roomController = new RoomController();

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
				.append("\n");
		}
		output.append("=> ");

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

	public HashMap<Integer, Enemy> getEnemies() {
		return database.getEnemiesForRoom(player.getRoom());
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
			if (error != null) return error + "\n";

			Boolean confirming = player.getConfirming();

			if (!confirming) {
				player.setLastCommand(command);
			}

			String output = command.run(this, arguments);
			if (output.endsWith("\n")) {
				output = output.substring(0, output.length() - 1);
			}

			database.setLastCommand(command);
			return output + "\n";
		}

		return "Sorry, command not recognized.\n";
	}


	// Commands

	/**
	 * Handles the "move" command.
	 * Validates direction and updates player position.
	 */
	public String move(ArrayList<String> arguments) {
		Room playerRoom = player.getRoom();
		String direction = arguments.get(0).toLowerCase();

		playerRoom.setRoomConnections(database.getConnectionsForRoom(playerRoom));
		PlayerController playerController = new PlayerController(player, new BattleEntityController());

		if (!roomController.isValidDirection(playerRoom, direction)) {
			return "Invalid direction for this room";
		}

		Boolean successfulMove = playerController.move(direction);
		if (!successfulMove) {
			return "Move failed, either player, or the room does not exist";
		}
		playerRoom = player.getRoom();

		HashMap<Integer, Enemy> enemies = database.getEnemiesForRoom(playerRoom);
		playerRoom.addEnemies(enemies);

		database.setPlayerRoom(playerRoom.getId());

		String output = playerRoom.getDescription();

		//  ENTER BATTLE IF NEEDED
		if (roomController.hasAliveEnemies(playerRoom)) {
			Enemy enemy = playerRoom.getEnemies().values().iterator().next();

			player.setCurrentEnemy(enemy);   // required field
			player.setState(PlayerState.BATTLE);
			database.setPlayerState(PlayerState.BATTLE);

			output += "\nYou encountered a " + enemy.getName() + "! Prepare for battle.\n";
		}

		return output;
	}

	/**
	 * Handles the "look" command.
	 * Returns the description of the current room.
	 */
	public String look(ArrayList<String> arguments) {
		Room playerRoom = player.getRoom();
		playerRoom.addNPCs(database.getNPCsForRoom(playerRoom));
		StringBuilder output = new StringBuilder(playerRoom.getDescription());

		WeaponAbility attack = new WeaponAbility(0, 10, "Player attack");
		System.out.println("other:" + attack);
		System.out.println("Items:" + database.getAbilitiesForWeapon(inventoryController.getWeapons(player.getInventory()).get(3)).get(3));

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

	public String search(ArrayList<String> arguments) {
		if (player.getState().equals(PlayerState.TALKING_TO_NPC)) {
			return searchShop(arguments);
		} else {
			return searchRoom(arguments);
		}
	}

	/**
	 * Handles the "search" command.
	 * Checks if the room has any items and returns them.
	 */
	public String searchRoom(ArrayList<String> arguments) {
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
		player.setState(PlayerState.TALKING_TO_NPC);
		database.setPlayerState(PlayerState.TALKING_TO_NPC);

		return currentNPC.getGreeting();
	}

	public String leaveNPC(ArrayList<String> arguments) {
		NPC npc = database.getNpcForPlayer();
		if (npc == null) return "You are not currently talking to an NPC.";

		String goodbye = npc.getGoodbye();

		database.setPlayerNPC(null);
		player.setState(PlayerState.EXPLORING);
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
		if (npc == null) return "You are not currently talking to an NPC.";

		HashMap<Integer, Item> npcItems = database.getItemsForNPC(npc);
		npc.getInventory().addItems(npcItems);

		String itemName = arguments.get(1).toLowerCase();
		Item item = inventoryController.getItemByNameCaseInsensitive(npc.getInventory(), itemName);
		if (item == null) return "I am not selling any " + itemName + "s.";

		Integer amount = null;
		try {
			amount = Integer.parseInt(arguments.get(0));
		} catch (NumberFormatException ignored) {
		}
		if (amount == null) return arguments.get(0) + " is not a valid amount.";
		if (player.getCoins() < item.getPrice() * amount) {
			return "You are too poor to buy " + amount + " x " + item.getName() + ".";
		}

		npcController.buy(npc, player, item, amount);
		database.setPlayerCoins(player.getCoins());
		database.addItemToPlayer(item);

		return "You bought " + amount + " x " + item.getName() + ", -" + item.getPrice() * amount + " coins.";
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
		database.setPlayerCoins(player.getCoins());

		return "You sold " + amount + " x " + item.getName() + ", +" + item.getValue() * amount + " coins.\n";
	}

	public String sellAllItem(ArrayList<String> arguments) {
		NPC npc = database.getNpcForPlayer();
		if (npc == null) return "You are not currently talking to anyone.\n";

		String itemName = arguments.get(0).toLowerCase();
		Item item = inventoryController.getItemByNameCaseInsensitive(player.getInventory(), itemName);
		if (item == null) return "You do not have any " + itemName + " to sell.\n";

		Integer amount = item.getAmount();

		npcController.sell(player, item, amount);
		database.removeItemFromPlayer(item);
		database.setPlayerCoins(player.getCoins());

		return "You sold " + amount + " x " + item.getName() + ", +" + item.getValue() * amount + " coins.\n";
	}

	public String attack(ArrayList<String> args) {
		Room playerRoom = player.getRoom();
		Enemy enemy = database.getEnemiesForRoom(player.getRoom()).get(0);
		if (enemy == null) return "No enemy to attack.\n";

		PlayerController pc = new PlayerController(player, new BattleEntityController());
		EnemyController ec = new EnemyController(new BattleEntityController());

		//WeaponAbility attack = new WeaponAbility(0, 10, "Player attack");
		int max = -1; // or 0 if you prefer

		for (Integer itemId : inventoryController.getWeapons(player.getInventory()).keySet()) {
			if (itemId >= 0 && itemId <= 4) {
				if (itemId > max) {
					max = itemId;
				}
			}
		}
		WeaponAbility attack = database.getAbilitiesForWeapon(inventoryController.getWeapons(player.getInventory()).get(max)).get(max);
		System.out.println("Items:" + database.getAbilitiesForWeapon(inventoryController.getWeapons(player.getInventory()).get(max)));
//		System.out.println("Items:" + max);
		String desc = attack.getAttackDescription();
		pc.attack(enemy, attack);
		database.setEnemyHealth(playerRoom, enemy, enemy.getHealth());


		if (!roomController.hasAliveEnemies(playerRoom)) {
			endBattle(true);
			return desc + "\nYou defeated the enemy!\n";
		}

		for (Integer itemId : inventoryController.getWeapons(enemy.getInventory()).keySet()) {
			if (itemId >= 0 && itemId <= 40) {
				if (itemId > max) {
					max = itemId;
				}
			}
		}
		WeaponAbility enemyAttack = new WeaponAbility(9, 15, "wacks with his cool stick");
		if (max == 30) {
			enemyAttack.setDamage(5);
			enemyAttack.setAttackDescription("pokes with their crude spear");
		} else if (max == 31) {
			enemyAttack.setDamage(7);
			enemyAttack.setAttackDescription("slashes with their sharp claws");
		} else {
			enemyAttack.setDamage(15);
			enemyAttack.setAttackDescription("wacks with his cool stick");
		}
		//WeaponAbility enemyAttack = database.getAbilitiesForWeapon(inventoryController.getWeapons(enemy.getInventory()).get(max)).get(max);
		String enemyTurn = ec.takeTurn(enemy, player, enemyAttack);
		database.setPlayerHealth(player.getHealth());

		if (player.getHealth() <= 0) {
			endBattle(false);
			return desc + "\nYou were defeated!\n";
		}

		return "You " + desc + "\nAttack Success!\n" + enemyTurn;
	}

	public String defend(ArrayList<String> args) {
		PlayerController pc = new PlayerController(player, new BattleEntityController());
		EnemyController ec = new EnemyController(new BattleEntityController());

		Armor armor = new Armor(0, "Defense", "Temporary defense", 5, true, 0);
		pc.defend(armor);

		//Enemy enemy = player.getCurrentEnemy();

		Enemy enemy = database.getEnemiesForRoom(player.getRoom()).get(0);
		int max = -1;
		for (Integer itemId : database.getItemsForEnemy(enemy).keySet()) {
			if (itemId >= 0 && itemId <= 40) {
				if (itemId > max) {
					max = itemId;
				}
			}
		}
		WeaponAbility enemyAttack = new WeaponAbility(9, 15, "wacks with his cool stick");
		if (max == 30) {
			enemyAttack.setDamage(5);
			enemyAttack.setAttackDescription("pokes with their crude spear");
		} else if (max == 31) {
			enemyAttack.setDamage(7);
			enemyAttack.setAttackDescription("slashes with their sharp claws");
		} else {
			enemyAttack.setDamage(15);
			enemyAttack.setAttackDescription("wacks with his cool stick");
		}
//		WeaponAbility enemyAttack = database.getAbilitiesForWeapon(inventoryController.getWeapons(enemy.getInventory()).get(max)).get(max);
		String enemyTurn = ec.takeTurn(enemy, player, enemyAttack);
//		System.out.println("Enemy:" + enemy);
//		System.out.println("Enemy max:" + max);
//		System.out.println("Enemy:" + inventoryController.getWeapons(enemy.getInventory()));
//		System.out.println("Enemy attack:" + database.getAbilitiesForWeapon(inventoryController.getWeapons(enemy.getInventory()).get(max)).get(max));
//		System.out.println("Enemy taking turn:" + enemyTurn);

		return "You brace for impact!\n" + enemyTurn;
	}

	public String heal(ArrayList<String> args) {
		String itemName = args.get(0);


		InventoryController ic = new InventoryController();
		HealingItem item = (HealingItem) ic.getItemByNameCaseInsensitive(player.getInventory(), itemName);


		if (item == null) return "You don't have that item.\n";


		PlayerController pc = new PlayerController(player, new BattleEntityController());
		pc.heal(item);


		EnemyController ec = new EnemyController(new BattleEntityController());
		Enemy enemy = database.getEnemiesForRoom(player.getRoom()).get(0);
		int max = -1;
		for (Integer itemId : inventoryController.getWeapons(enemy.getInventory()).keySet()) {
			if (itemId >= 0 && itemId <= 40) {
				if (itemId > max) {
					max = itemId;
				}
			}
		}
		WeaponAbility enemyAttack = new WeaponAbility(9, 15, "wacks with his cool stick");
		if (max == 30) {
			enemyAttack.setDamage(5);
			enemyAttack.setAttackDescription("pokes with their crude spear");
		} else if (max == 31) {
			enemyAttack.setDamage(7);
			enemyAttack.setAttackDescription("slashes with their sharp claws");
		} else {
			enemyAttack.setDamage(15);
			enemyAttack.setAttackDescription("wacks with his cool stick");
		}
		//WeaponAbility enemyAttack = database.getAbilitiesForWeapon(inventoryController.getWeapons(enemy.getInventory()).get(max)).get(max);
		String enemyTurn = ec.takeTurn(enemy, player, enemyAttack);

		if (enemy == null) {
			return "No enemy is attacking you.\n";
		}

		database.removeItemFromPlayer(item);

		System.out.println("Enemy taking turn...");


		return "You healed yourself.\n" + enemyTurn;
	}

	private void endBattle(boolean playerWon) {
//		Enemy enemy = player.getCurrentEnemy();
//
//		if (playerWon) {
//			player.getRoom().removeEnemy(enemy);
//		}

		database.setPlayerState(PlayerState.EXPLORING);
	}

	public String restart(ArrayList<String> arguments) {
		if (!player.getConfirming()) {
			database.setConfirming(true);
			return "Are you sure (yes or no)?";
		}

		Boolean success = database.reset();

		if (!success) return "Reset failed, try again.";

		return "Restarted game.";
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

		return lastCommand.getName() + " command canceled.";
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
		String error = validateConfirming(command);
		if (error != null) return error;

		error = validatePlayerState(command);
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

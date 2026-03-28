package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.Player;
import edu.ycp.cs320.TBAG.model.PlayerState;
import edu.ycp.cs320.TBAG.model.Room;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class GameEngineTest {
	private Player player;
	private HashMap<String, Room> rooms;
	private GameEngine gameEngine;
	private ArrayList<String> arguments;

	@BeforeEach
	public void setUp() {
		player = new Player(100, 100);
		rooms = new HashMap<String, Room>();
		Room roomA = new Room("0", "a", "description a");
		Room roomB = new Room("1", "b", "description b");

		roomA.setConnection(roomB, "north");
		roomB.setConnection(roomA, "south");

		rooms.put(roomA.getID(), roomA);
		rooms.put(roomB.getID(), roomB);
		gameEngine = new GameEngine(player, rooms);

		this.arguments = new ArrayList<>();
	}

	@Test
	public void testCreateRooms() {
		rooms.clear();
		gameEngine = new GameEngine(player, rooms);

		Assertions.assertFalse(rooms.isEmpty());
	}

	@Test
	public void testDefaultRoom() {
		Room playerRoom = player.getRoom();

		Assertions.assertEquals("0", playerRoom.getID());
		Assertions.assertEquals("a", playerRoom.getName());
		Assertions.assertEquals("description a", playerRoom.getDescription());
	}

	@Test
	public void testMove() {
		Room playerRoom = player.getRoom();

		// Valid move
		arguments.add("north");
		Assertions.assertEquals("description b\n\n", gameEngine.inputCommand("move", arguments));
		Assertions.assertEquals("1", playerRoom.getID());
		Assertions.assertEquals("b", playerRoom.getName());
		Assertions.assertEquals("description b", playerRoom.getDescription());

		arguments.clear();
		arguments.add("south");
		Assertions.assertEquals(
			"description a\n\n",
			gameEngine.inputCommand("move", arguments)
		);
		Assertions.assertEquals("0", player.getRoom().getID());
		Assertions.assertEquals("a", player.getRoom().getName());
		Assertions.assertEquals("description a", player.getRoom().getDescription());

		// Invalid direction
		arguments.clear();
		arguments.add("left");
		Assertions.assertEquals(
			"Invalid direction for this room\n\n",
			gameEngine.inputCommand("move", arguments)
		);
		Assertions.assertEquals("0", player.getRoom().getID());
		Assertions.assertEquals("a", player.getRoom().getName());
		Assertions.assertEquals("description a", player.getRoom().getDescription());
	}

	@Test
	public void testLook() {
		Assertions.assertEquals(
			"description a\n\n",
			gameEngine.inputCommand("look", arguments)
		);

		arguments.add("north");
		gameEngine.inputCommand("move", arguments);

		arguments.clear();
		Assertions.assertEquals(
			"description b\n\n",
			gameEngine.inputCommand("look", arguments)
		);
	}

	@Test
	public void testInventory() {
		// Empty inventory
		Assertions.assertEquals(
			"Your Inventory:\n"
				+ "Empty\n\n",
			gameEngine.inputCommand("inventory", arguments)
		);

		// Add item to inventory
		Item item = new Item("0", "sword", "A sharp sword", 1, 1);
		player.getInventory().getItems().put(item.getId(), item);

		Assertions.assertEquals(
			"Your Inventory:\n"
				+ "- 1 x sword\n\n",
			gameEngine.inputCommand("inventory", arguments)
		);

		// Add multiple items
		Item item2 = new Item("1", "potion", "A healing potion", 1, 3);
		player.getInventory().getItems().put(item2.getId(), item2);

		Assertions.assertEquals(
			"Your Inventory:\n"
				+ "- 1 x sword\n"
				+ "- 3 x potions\n\n",
			gameEngine.inputCommand("inventory", arguments)
		);
	}

	@Test
	public void testInspectItem() {
		// Item not in inventory
		arguments.add("sword");
		Assertions.assertEquals(
			"You do not have a sword in your inventory.\n\n",
			gameEngine.inputCommand("inspect", arguments)
		);

		// Add item to inventory
		Item item = new Item("0", "sword", "A sharp sword", 1);
		player.getInventory().getItems().put(item.getId(), item);

		// Valid inspect
		Assertions.assertEquals(
			item.getDescription() + "\n\n",
			gameEngine.inputCommand("inspect", arguments)
		);
	}

	@Test
	public void testSearch() {
		// Empty room
		Assertions.assertEquals(
			"You found:\n"
				+ "Nothing!\n\n",
			gameEngine.inputCommand("search", arguments)
		);

		// One item
		Item item1 = new Item("0", "sword", "A sharp sword", 1);
		player.getRoom().getInventory().getItems().put(item1.getId(), item1);

		Assertions.assertEquals(
			"You found:\n"
				+ "- 1 x sword\n\n",
			gameEngine.inputCommand("search", arguments)
		);

		// Multiple items
		Item item2 = new Item("1", "potion", "A healing potion", 1, 3);
		player.getRoom().getInventory().getItems().put(item2.getId(), item2);

		Assertions.assertEquals(
			"You found:\n"
				+ "- 1 x sword\n"
				+ "- 3 x potions\n\n",
			gameEngine.inputCommand("search", arguments)
		);
	}

	@Test
	public void testPickup() {
		Room playerRoom = player.getRoom();

		// Item not in room
		arguments.add("sword");
		Assertions.assertEquals(
			"This room does not have a sword.\n\n",
			gameEngine.inputCommand("pickup", arguments)
		);

		// Add item to room
		Item item1 = new Item("0", "sword", "A sharp sword", 1);

		// Test pickup item not in room
		Assertions.assertEquals(
			"This room does not have a sword.\n\n",
			gameEngine.inputCommand("pickup", arguments)
		);

		// Add item to room
		playerRoom.getInventory().getItems().put(item1.getId(), item1);

		// Valid pickup
		Assertions.assertEquals("You picked up 1 sword.\n\n", gameEngine.inputCommand("pickup", arguments));
		Assertions.assertTrue(player.getInventory().getItems().containsKey(item1.getId()));
		Assertions.assertFalse(playerRoom.getInventory().getItems().containsKey(item1.getId()));

		// Pickup item you already have
		playerRoom.getInventory().getItems().put(item1.getId(), new Item("0", "sword", "A sharp sword", 1));

		Assertions.assertEquals("You picked up 1 sword.\n\n", gameEngine.inputCommand("pickup", arguments));
		Assertions.assertTrue(player.getInventory().getItems().containsKey(item1.getId()));
		Assertions.assertEquals(2, player.getInventory().getItems().get(item1.getId()).getAmount());
		Assertions.assertFalse(playerRoom.getInventory().getItems().containsKey(item1.getId()));

		// Pickup with multiple items
		Item item2 = new Item("1", "potion", "A healing potion", 1, 3);
		playerRoom.getInventory().getItems().put(item2.getId(), item2);

		arguments.clear();
		arguments.add("potion");
		Assertions.assertEquals(
			"You picked up 3 potions.\n\n",
			gameEngine.inputCommand("pickup", arguments)
		);
		Assertions.assertTrue(player.getInventory().getItems().containsKey(item1.getId()));
		Assertions.assertEquals(2, player.getInventory().getItems().get(item1.getId()).getAmount());
		Assertions.assertTrue(player.getInventory().getItems().containsKey(item2.getId()));
		Assertions.assertEquals(3, player.getInventory().getItems().get(item2.getId()).getAmount());
		Assertions.assertFalse(playerRoom.getInventory().getItems().containsKey(item1.getId()));
		Assertions.assertFalse(playerRoom.getInventory().getItems().containsKey(item2.getId()));
	}

	@Test
	public void testPickupAll() {
		Room playerRoom = player.getRoom();

		Item item1 = new Item("0", "sword", "A sharp sword", 1);
		Item item2 = new Item("1", "potion", "A healing potion", 1, 3);

		playerRoom.getInventory().addItem(item1);
		playerRoom.getInventory().addItem(item2);

		Assertions.assertEquals(
			"You picked up:\n1 x sword\n3 x potion\n\n",
			gameEngine.inputCommand("pickup-all", arguments)
		);

		Assertions.assertTrue(player.getInventory().getItems().containsKey(item1.getId()));
		Assertions.assertEquals(1, player.getInventory().getItems().get(item1.getId()).getAmount());
		Assertions.assertFalse(playerRoom.getInventory().getItems().containsKey(item1.getId()));

		Assertions.assertTrue(player.getInventory().getItems().containsKey(item2.getId()));
		Assertions.assertEquals(3, player.getInventory().getItems().get(item2.getId()).getAmount());
		Assertions.assertFalse(playerRoom.getInventory().getItems().containsKey(item2.getId()));

		// Test empty room
		Assertions.assertEquals(
			"You did not pick anything up from this room.\n\n",
			gameEngine.inputCommand("pickup-all", arguments)
		);
	}

	@Test
	public void testDrop() {
		Room playerRoom = player.getRoom();

		// Item not in inventory
		arguments.add("sword");
		Assertions.assertEquals(
			"This room does not have a sword.\n\n",
			gameEngine.inputCommand("drop", arguments))
		;

		// Add item to inventory
		Item item = new Item("0", "sword", "A sharp sword", 1);
		player.getInventory().getItems().put(item.getId(), item);

		// Valid drop
		Assertions.assertEquals(
			"You dropped 1 sword.\n\n",
			gameEngine.inputCommand("drop", arguments)
		);
		Assertions.assertFalse(player.getInventory().getItems().containsKey(item.getId()));
		Assertions.assertTrue(playerRoom.getInventory().getItems().containsKey(item.getId()));

		// Drop with multiple items
		Item item2 = new Item("1", "potion", "A healing potion", 1, 3);
		player.getInventory().getItems().put(item2.getId(), item2);

		arguments.clear();
		arguments.add("potion");
		Assertions.assertEquals(
			"You dropped 3 potions.\n\n",
			gameEngine.inputCommand("drop", arguments)
		);
		Assertions.assertFalse(player.getInventory().getItems().containsKey(item2.getId()));
		Assertions.assertTrue(playerRoom.getInventory().getItems().containsKey(item2.getId()));
		Assertions.assertEquals(
			3,
			playerRoom.getInventory().getItems().get(item2.getId()).getAmount()
		);
	}

	@Test
	public void testDropAll() {
		Room playerRoom = player.getRoom();

		Item item1 = new Item("0", "sword", "A sharp sword", 1);
		Item item2 = new Item("1", "potion", "A healing potion", 1, 3);

		player.getInventory().addItem(item1);
		player.getInventory().addItem(item2);

		Assertions.assertEquals(
			"You dropped:\n1 x sword\n3 x potion\n\n",
			gameEngine.inputCommand("drop-all", arguments)
		);

		Assertions.assertTrue(playerRoom.getInventory().getItems().containsKey(item1.getId()));
		Assertions.assertEquals(1, playerRoom.getInventory().getItems().get(item1.getId()).getAmount());
		Assertions.assertFalse(player.getInventory().getItems().containsKey(item1.getId()));

		Assertions.assertTrue(playerRoom.getInventory().getItems().containsKey(item2.getId()));
		Assertions.assertEquals(3, playerRoom.getInventory().getItems().get(item2.getId()).getAmount());
		Assertions.assertFalse(player.getInventory().getItems().containsKey(item2.getId()));

		Assertions.assertEquals(
			"You do not have anything to drop.\n\n",
			gameEngine.inputCommand("drop-all", arguments)
		);
	}

	// Unsure how to make restart able to work with any starting data.
	@Test
	public void testRestart() {
		Room playerRoom = player.getRoom();

		// Add items to inventory and room to test restart properly
		Item item = new Item("0", "sword", "A sharp sword", 1);
		player.getInventory().getItems().put(item.getId(), item);
		playerRoom.getInventory().getItems().put(item.getId(), item);

		// Move to different room
		arguments.add("north");
		gameEngine.inputCommand("move", arguments);

		// Test restart
		arguments.clear();
		Assertions.assertEquals(
			"Restarted game.\n\n",
			gameEngine.inputCommand("restart", arguments)
		);

		// Verify restart worked
		Assertions.assertEquals("0", playerRoom.getID());
		Assertions.assertEquals("a", playerRoom.getName());
		Assertions.assertEquals("description a", playerRoom.getDescription());
		Assertions.assertTrue(player.getInventory().getItems().isEmpty());
		Assertions.assertTrue(player.getArmor().isEmpty());
		Assertions.assertEquals(PlayerState.EXPLORING, player.getState());
	}

	@Test
	public void testHelp() {
		String output = gameEngine.inputCommand("help", arguments);

		Assertions.assertTrue(
			output.contains(Command.MOVE.getFormat())
		);

		StringBuilder examples = new StringBuilder("Examples:");
		for (String example : Command.MOVE.getExamples()) {
			examples
				.append("\n  - \"")
				.append(example)
				.append("\"");
		}
		Assertions.assertTrue(
			output.contains(examples.toString())
		);

		Assertions.assertTrue(
			output.contains(Command.DROP_ALL.getFormat())
		);
		Assertions.assertTrue(
			output.contains(Command.HELP.getFormat())
		);
	}

	@Test
	public void testInvalidCommandFormat() {
		// Test all commands with wrong number of arguments

		// Move command with too many arguments
		arguments.add("north");
		arguments.add("south");
		Assertions.assertTrue(
			gameEngine.inputCommand("move", arguments).startsWith(
				"Invalid move command. Must be in the format:\n\"move <direction>\"\n"
			)
		);

		// Move command with no arguments
		arguments.clear();
		Assertions.assertTrue(
			gameEngine.inputCommand("move", arguments).startsWith(
				"Invalid move command. Must be in the format:\n\"move <direction>\"\n"
			)
		);
	}

	@Test
	public void testInvalidCommand() {
		// Test unrecognized command
		Assertions.assertEquals(
			"Sorry, command not recognized.\n\n",
			gameEngine.inputCommand("invalid", arguments)
		);
		Assertions.assertEquals(
			"Sorry, command not recognized.\n\n",
			gameEngine.inputCommand("xyz", arguments)
		);
	}
}

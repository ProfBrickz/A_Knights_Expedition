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
	public void testDefaultRoom() {
		Assertions.assertEquals("0", player.getRoom().getID());
		Assertions.assertEquals("a", player.getRoom().getName());
		Assertions.assertEquals("description a", player.getRoom().getDescription());
	}

	@Test
	public void testMove() {
		// Valid move
		arguments.add("north");
		Assertions.assertEquals("description b\n\n", gameEngine.inputCommand("move", arguments));
		Assertions.assertEquals("1", player.getRoom().getID());
		Assertions.assertEquals("b", player.getRoom().getName());
		Assertions.assertEquals("description b", player.getRoom().getDescription());

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
			gameEngine.inputCommand("inspect-item", arguments)
		);

		// Add item to inventory
		Item item = new Item("0", "sword", "A sharp sword", 1);
		player.getInventory().getItems().put(item.getId(), item);

		// Valid inspect
		Assertions.assertEquals(
			item.getDescription() + "\n\n",
			gameEngine.inputCommand("inspect-item", arguments)
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
		// Item not in room
		arguments.add("sword");
		Assertions.assertEquals(
			"This room does not have a sword.\n\n",
			gameEngine.inputCommand("pickup", arguments)
		);

		// Add item to room
		Item item = new Item("0", "sword", "A sharp sword", 1);

		// Test pickup item not in room
		Assertions.assertEquals(
			"This room does not have a sword.\n\n",
			gameEngine.inputCommand("pickup", arguments)
		);

		// Add item to room
		player.getRoom().getInventory().getItems().put(item.getId(), item);

		// Valid pickup
		Assertions.assertEquals("You picked up a sword.\n\n", gameEngine.inputCommand("pickup", arguments));
		Assertions.assertTrue(player.getInventory().getItems().containsKey(item.getId()));
		Assertions.assertFalse(player.getRoom().getInventory().getItems().containsKey(item.getId()));

		// Pickup with multiple items
		Item item2 = new Item("1", "potion", "A healing potion", 1, 3);
		player.getRoom().getInventory().getItems().put(item2.getId(), item2);

		arguments.clear();
		arguments.add("potion");
		Assertions.assertEquals(
			"You picked up 3 potions.\n\n",
			gameEngine.inputCommand("pickup", arguments)
		);
		Assertions.assertTrue(player.getInventory().getItems().containsKey(item2.getId()));
		Assertions.assertEquals(3, player.getInventory().getItems().get(item2.getId()).getAmount());
		Assertions.assertFalse(player.getRoom().getInventory().getItems().containsKey(item2.getId()));
	}

	@Test
	public void testDrop() {
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
			"You dropped a sword.\n\n",
			gameEngine.inputCommand("drop", arguments)
		);
		Assertions.assertFalse(player.getInventory().getItems().containsKey(item.getId()));
		Assertions.assertTrue(player.getRoom().getInventory().getItems().containsKey(item.getId()));

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
		Assertions.assertTrue(player.getRoom().getInventory().getItems().containsKey(item2.getId()));
		Assertions.assertEquals(
			3,
			player.getRoom().getInventory().getItems().get(item2.getId()).getAmount()
		);
	}

	// Unsure how to make restart able to work with any starting data.
	@Test
	public void testRestart() {
		// Add items to inventory and room to test restart properly
		Item item = new Item("0", "sword", "A sharp sword", 1);
		player.getInventory().getItems().put(item.getId(), item);
		player.getRoom().getInventory().getItems().put(item.getId(), item);

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
		Assertions.assertEquals("0", player.getRoom().getID());
		Assertions.assertEquals("a", player.getRoom().getName());
		Assertions.assertEquals("description a", player.getRoom().getDescription());
		Assertions.assertTrue(player.getInventory().getItems().isEmpty());
		Assertions.assertTrue(player.getArmor().isEmpty());
		Assertions.assertEquals(PlayerState.EXPLORING, player.getState());
	}

	@Test
	public void testInvalidCommandFormat() {
		// Test all commands with wrong number of arguments

		// Move command with too many arguments
		arguments.add("north");
		arguments.add("south");
		Assertions.assertEquals(
			"Invalid move command. Must be in the format:\n\"move <direction>\"\n\n",
			gameEngine.inputCommand("move", arguments)
		);

		// Move command with no arguments
		arguments.clear();
		Assertions.assertEquals(
			"Invalid move command. Must be in the format:\n\"move <direction>\"\n\n",
			gameEngine.inputCommand("move", arguments)
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

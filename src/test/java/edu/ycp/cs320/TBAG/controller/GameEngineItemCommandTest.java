package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.Player;
import edu.ycp.cs320.TBAG.model.Room;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class GameEngineItemCommandTest {
	private Player player;
	private Room room;
	private GameEngine gameEngine;

	@BeforeEach
	public void setUp() {
		player = new Player(100, 100);
		room = new Room(0, "start", "Starting room");

		HashMap<Integer, Room> rooms = new HashMap<>();
		rooms.put(room.getID(), room);

		gameEngine = new GameEngine(player, rooms);
	}

	@Test
	public void pickupMovesItemFromRoomToPlayer() {
		Item sword = new Item(1, "sword", "A sharp sword", 10);
		room.getInventory().addItem(sword);

		ArrayList<String> arguments = new ArrayList<>();
		arguments.add("sword");

		String output = gameEngine.inputCommand("pickup", arguments);

		Assertions.assertEquals("You picked up 1 sword.\n", output);
		Assertions.assertFalse(room.getInventory().getItems().containsKey(sword.getId()));
		Assertions.assertTrue(player.getInventory().getItems().containsKey(sword.getId()));
		Assertions.assertEquals(1, player.getInventory().getItems().get(sword.getId()).getAmount());
	}

	@Test
	public void pickupAllMovesEveryItemFromRoomToPlayer() {
		Item sword = new Item(1, "sword", "A sharp sword", 10);
		Item potion = new Item(2, "potion", "A healing potion", 5, 3);
		room.getInventory().addItem(sword);
		room.getInventory().addItem(potion);

		String output = gameEngine.inputCommand("pickup-all", new ArrayList<>());

		Assertions.assertEquals("You picked up:\n1 x sword\n3 x potion\n", output);
		Assertions.assertTrue(room.getInventory().getItems().isEmpty());
		Assertions.assertEquals(1, player.getInventory().getItems().get(sword.getId()).getAmount());
		Assertions.assertEquals(3, player.getInventory().getItems().get(potion.getId()).getAmount());
	}

	@Test
	public void dropMovesItemFromPlayerToRoom() {
		Item sword = new Item(1, "sword", "A sharp sword", 10);
		player.getInventory().addItem(sword);

		ArrayList<String> arguments = new ArrayList<>();
		arguments.add("sword");

		String output = gameEngine.inputCommand("drop", arguments);

		Assertions.assertEquals("You dropped 1 sword.\n", output);
		Assertions.assertFalse(player.getInventory().getItems().containsKey(sword.getId()));
		Assertions.assertTrue(room.getInventory().getItems().containsKey(sword.getId()));
		Assertions.assertEquals(1, room.getInventory().getItems().get(sword.getId()).getAmount());
	}

	@Test
	public void dropAllMovesEveryItemFromPlayerToRoom() {
		Item sword = new Item(1, "sword", "A sharp sword", 10);
		Item potion = new Item(2, "potion", "A healing potion", 5, 3);
		player.getInventory().addItem(sword);
		player.getInventory().addItem(potion);

		String output = gameEngine.inputCommand("drop-all", new ArrayList<>());

		Assertions.assertEquals("You dropped:\n1 x sword\n3 x potion\n", output);
		Assertions.assertTrue(player.getInventory().getItems().isEmpty());
		Assertions.assertEquals(1, room.getInventory().getItems().get(sword.getId()).getAmount());
		Assertions.assertEquals(3, room.getInventory().getItems().get(potion.getId()).getAmount());
	}
}

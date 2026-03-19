package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.Player;
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
		roomB.setConnection(roomA, "SOUTH");

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
		arguments.add("north");
		Assertions.assertEquals("description b\n", gameEngine.inputCommand("move", arguments));
		Assertions.assertEquals("1", player.getRoom().getID());
		Assertions.assertEquals("b", player.getRoom().getName());
		Assertions.assertEquals("description b", player.getRoom().getDescription());

		arguments.clear();
		arguments.add("south");
		Assertions.assertEquals("description a\n", gameEngine.inputCommand("move", arguments));
		Assertions.assertEquals("0", player.getRoom().getID());
		Assertions.assertEquals("a", player.getRoom().getName());
		Assertions.assertEquals("description a", player.getRoom().getDescription());

		// Invalid direction
		arguments.clear();
		arguments.add("left");
		Assertions.assertEquals("Invalid direction for this room\n", gameEngine.inputCommand("move", arguments));
		Assertions.assertEquals("0", player.getRoom().getID());
		Assertions.assertEquals("a", player.getRoom().getName());
		Assertions.assertEquals("description a", player.getRoom().getDescription());
	}

	@Test
	public void testInvalidFormat() {
		final String moveFormat = "Invalid move command. Must be in the format:\n\"move <direction>\"\n";

		// Too many arguments
		arguments.add("left");
		arguments.add("up");
		Assertions.assertEquals(moveFormat, gameEngine.inputCommand("move", arguments));
		Assertions.assertEquals("0", player.getRoom().getID());
		Assertions.assertEquals("a", player.getRoom().getName());
		Assertions.assertEquals("description a", player.getRoom().getDescription());

		// Too few arguments
		arguments.clear();
		Assertions.assertEquals(moveFormat, gameEngine.inputCommand("move", arguments));
	}

	@Test
	public void testLook() {
		Assertions.assertEquals("description a\n", gameEngine.inputCommand("look", arguments));

		arguments.add("north");
		gameEngine.inputCommand("move", arguments);

		arguments.clear();
		Assertions.assertEquals("description b\n", gameEngine.inputCommand("look", arguments));
	}
}

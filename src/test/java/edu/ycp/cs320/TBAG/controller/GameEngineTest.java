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

	@BeforeEach
	public void setUp() {
		player = new Player();
		rooms = new HashMap<String, Room>();
		Room start = new Room("0", "Shore", "You find yourself washed ashore after a shipwreck");
		Room center = new Room("1", "Center", "You walk inshore and find a crossroads");

		start.setConnection(center, "NORTH");

		center.setConnection(start, "SOUTH");

		rooms.put(start.getID(), start);
		rooms.put(center.getID(), center);
		gameEngine = new GameEngine(player, rooms);
	}

	@Test
	public void testDirection() {
		ArrayList<String> argue = new ArrayList<String>();
		argue.add("north");
		Assertions.assertEquals("north", gameEngine.inputCommand("north", argue));
	}

	@Test
	public void testDirectionNull() {
		ArrayList<String> argue = new ArrayList<String>();
		argue.add("north");
		Assertions.assertEquals(null, gameEngine.inputCommand("south", argue));
	}

//	@Test
//	public void testPlayerInMatchingRoom() {
//		Assertions.assertLinesMatch();
//	}
//
//	@Test
//	public void testLook() {
//		Assertions.assertLinesMatch(gameEngine.);
//	}
//
//	@Test
//	public void testLookNull() {
//		Assertions.assertLinesMatch(gameEngine.);
//	}
//
//	@Test
//	public void testNullCommand() {
//		Assertions.assertLinesMatch(gameEngine.);
//	}
}

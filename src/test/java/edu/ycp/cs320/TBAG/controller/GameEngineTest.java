package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.CommandType;
import edu.ycp.cs320.TBAG.model.Player;
import edu.ycp.cs320.TBAG.model.Room;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class GameEngineTest {
	private Player player;
	private ArrayList<Room> rooms;
	private GameEngine gameEngine;

	@BeforeEach
	public void setUp() {
		player = new Player();
		rooms = new ArrayList<Room>();
		gameEngine = new GameEngine(player, rooms);
	}

	@Test
	public void testParseCommand() {
		Assertions.assertEquals(CommandType.MOVE, gameEngine.parseCommand("move"));
		Assertions.assertNull(gameEngine.parseCommand("move "));
		Assertions.assertNull(gameEngine.parseCommand(""));
		Assertions.assertNull(gameEngine.parseCommand("abc"));
	}

	@Test
	public void testMovePlayer() {
		throw new UnsupportedOperationException("TODO - implement");
	}
}

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
		gameEngine = new GameEngine(player, rooms);
	}
}

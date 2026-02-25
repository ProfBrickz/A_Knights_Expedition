package edu.ycp.cs320.TBAG.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import edu.ycp.cs320.TBAG.controller.GameEngine;
import edu.ycp.cs320.TBAG.model.Player;

public class GameEngineTest {
	private Player player;
	private GameEngine engine;
	
	@BeforeEach
	public void setUp() {
		player = new Player();
		engine = new GameEngine();
		
		player.setRoomID(1);
		
		engine.setPlayer(player);
	}
	
	
}

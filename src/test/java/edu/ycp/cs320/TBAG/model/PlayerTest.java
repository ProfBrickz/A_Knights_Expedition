package edu.ycp.cs320.TBAG.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import edu.ycp.cs320.TBAG.model.Player;

public class PlayerTest {
	private Player player;
	
	@BeforeEach
	public void setUp() {
		player = new Player();
	}
	
	@Test
	public void testDefaultGetRoomID() {
		Assertions.assertEquals(1, player.getRoomID());
	}

}

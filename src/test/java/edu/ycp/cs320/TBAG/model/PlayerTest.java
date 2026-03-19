package edu.ycp.cs320.TBAG.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlayerTest {
	private Player player;

	@BeforeEach
	public void setUp() {
		player = new Player(100, 100);
	}

	@Test
	public void testDefaultGetRoomID() {
		Assertions.assertNull(player.getRoom());
	}

//	@Test
//	public void testDefaultGetCoin() {
//		Assertions.assertEquals(0, player.getCoins());
//	}
//
//	@Test
//	public void testDefaultGetState() {
//		Assertions.assertEquals("NORMAL", player.getState());
//	}

//	@Test
//	public void testGetRoomID() {
//		player.setRoom(5);
//		Assertions.assertEquals(5, player.getRoomID());
//	}

//	@Test
//	public void testGetCoin() {
//		player.setCoins(1000);
//		Assertions.assertEquals(1000, player.getCoins());
//	}
//
//	@Test
//	public void testGetState() {
//		player.setState("BATTLE");
//		Assertions.assertEquals("BATTLE", player.getState());
//	}

}

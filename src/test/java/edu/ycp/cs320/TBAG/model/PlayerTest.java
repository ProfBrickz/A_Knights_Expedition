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

	@Test
	public void testDefaultState() {
		Assertions.assertEquals(PlayerState.EXPLORING, player.getState());
	}

	@Test
	public void testSetGetRoom() {
		Room room = new Room("1", "Hall", "A long hall");
		player.setRoom(room);
		Assertions.assertSame(room, player.getRoom());
	}

	@Test
	public void testSetGetState() {
		player.setState(PlayerState.BATTLE);
		Assertions.assertEquals(PlayerState.BATTLE, player.getState());
	}

	@Test
	public void testArmorListDefaultsToEmpty() {
		Assertions.assertNotNull(player.getArmor());
		Assertions.assertTrue(player.getArmor().isEmpty());
	}

	@Test
	public void testInventoryDefaultsToEmptyAndStable() {
		Assertions.assertNotNull(player.getInventory());
		Assertions.assertTrue(player.getInventory().getItems().isEmpty());
		Assertions.assertSame(player.getInventory(), player.getInventory());
	}

	@Test
	public void testConstructorWithStateAndRoom() {
		Room room = new Room("2", "Armory", "Lots of gear");
		Player battlePlayer = new Player(50, 25, PlayerState.BATTLE, room);
		Assertions.assertEquals(50, battlePlayer.getMaxHealth());
		Assertions.assertEquals(25, battlePlayer.getHealth());
		Assertions.assertSame(room, battlePlayer.getRoom());
		Assertions.assertEquals(PlayerState.BATTLE, battlePlayer.getState());
	}

}

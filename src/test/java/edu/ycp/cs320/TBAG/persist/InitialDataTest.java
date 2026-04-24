package edu.ycp.cs320.TBAG.persist;

import edu.ycp.cs320.TBAG.model.Player;
import edu.ycp.cs320.TBAG.model.PlayerState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

public class InitialDataTest {

	@BeforeEach
	public void setUp() {
		InitialData.setCsvFolder("src/test/fixtures/database");
	}

	@AfterEach
	public void tearDown() {
		InitialData.setCsvFolder("src/resources");
	}


	// Dialog
	@Test
	public void testGetDialog() throws IOException {
		ArrayList<String> dialog = InitialData.getDialog();
		Assertions.assertEquals(1, dialog.size());
		Assertions.assertEquals("Welcome to the test!", dialog.get(0));
	}


	// Items


	// NPCs


	// Enemies


	// Rooms

	
	// Player
	@Test
	public void testGetPlayer() throws IOException {
		InitialData.getRooms();
		Player player = InitialData.getPlayer();
		Assertions.assertNotNull(player);
		Assertions.assertNotNull(player.getRoom());
		Assertions.assertEquals(0, player.getRoom().getId());
		Assertions.assertEquals(PlayerState.EXPLORING, player.getState());
		Assertions.assertEquals(10, player.getCoins());
		Assertions.assertEquals(50, player.getHealth());
		Assertions.assertEquals(100, player.getMaxHealth());
	}
}

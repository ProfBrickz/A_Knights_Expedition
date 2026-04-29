package edu.ycp.cs320.TBAG.persist;

import edu.ycp.cs320.TBAG.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class InitialDataTests {

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
	@Test
	public void testGetItems() throws IOException {
		HashMap<Integer, Item> items = InitialData.getItems();

		Assertions.assertEquals(3, items.size());
		Assertions.assertInstanceOf(Weapon.class, items.get(0));
		Assertions.assertEquals("Test Sword", items.get(0).getName());
		Assertions.assertEquals("sword.png", items.get(0).getAssetName());

		Assertions.assertInstanceOf(HealingItem.class, items.get(1));
		Assertions.assertEquals(10, ((HealingItem) items.get(1)).getHealAmount());

		Assertions.assertInstanceOf(Armor.class, items.get(2));
		Assertions.assertEquals(5, ((Armor) items.get(2)).getDefense());
		Assertions.assertFalse(((Armor) items.get(2)).getActive());
	}

	@Test
	public void testGetWeaponAbilities() throws IOException {
		HashMap<Integer, WeaponAbility> weaponAbilities = InitialData.getWeaponAbilities();
		Assertions.assertTrue(weaponAbilities.isEmpty());
	}

	@Test
	public void testGetWeaponAbilitiesJunction() throws IOException {
		ArrayList<Pair<Integer, Integer>> junction = InitialData.getWeaponAbilitiesJunction();
		Assertions.assertTrue(junction.isEmpty());
	}


	// NPCs
	@Test
	public void testGetNPCs() throws IOException {
		HashMap<Integer, NPC> npcs = InitialData.getNPCs();

		Assertions.assertEquals(1, npcs.size());
		Assertions.assertEquals("Test NPC", npcs.get(0).getName());
		Assertions.assertEquals(50, npcs.get(0).getMaxHealth());
		Assertions.assertEquals("Hello traveler!", npcs.get(0).getGreeting());
		Assertions.assertEquals("Goodbye!", npcs.get(0).getGoodbye());
	}

	@Test
	public void testGetNPCItems() throws IOException {
		InitialData.getItems();
		InitialData.getNPCs();

		HashMap<Integer, ArrayList<Item>> npcItems = InitialData.getNPCItems();
		Assertions.assertTrue(npcItems.isEmpty());
	}


	// Enemies
	@Test
	public void testGetEnemies() throws IOException {
		HashMap<Integer, Enemy> enemies = InitialData.getEnemies();

		Assertions.assertEquals(1, enemies.size());
		Assertions.assertEquals("Goblin", enemies.get(0).getName());
		Assertions.assertEquals(30, enemies.get(0).getMaxHealth());
	}

	@Test
	public void testGetEnemyItems() throws IOException {
		InitialData.getItems();
		InitialData.getEnemies();

		HashMap<Integer, ArrayList<Item>> enemyItems = InitialData.getEnemyItems();
		Assertions.assertEquals(1, enemyItems.size());
		Assertions.assertEquals(1, enemyItems.get(0).size());
		Assertions.assertEquals("Test Sword", enemyItems.get(0).get(0).getName());
		Assertions.assertEquals(5, enemyItems.get(0).get(0).getAmount());
	}


	// Rooms
	@Test
	public void testGetRooms() throws IOException {
		HashMap<Integer, Room> rooms = InitialData.getRooms();

		Assertions.assertEquals(3, rooms.size());
		Assertions.assertEquals("Test Room", rooms.get(0).getName());
		Assertions.assertEquals("A room with an npc.", rooms.get(1).getDescription());
		Assertions.assertEquals("Enemy Room", rooms.get(2).getName());
	}

	@Test
	public void testGetRoomConnections() throws IOException {
		InitialData.getRooms();

		HashMap<Integer, HashMap<String, RoomConnection>> roomConnections = InitialData.getRoomConnections();

		Assertions.assertEquals(3, roomConnections.size());
		Assertions.assertEquals(1, roomConnections.get(0).get("North").getRoom().getId());
		Assertions.assertEquals(0, roomConnections.get(1).get("South").getRoom().getId());
		Assertions.assertEquals(2, roomConnections.get(1).get("East").getRoom().getId());
		Assertions.assertEquals(1, roomConnections.get(2).get("West").getRoom().getId());
	}

	@Test
	public void testGetRoomNPCs() throws IOException {
		InitialData.getRooms();
		InitialData.getNPCs();

		HashMap<Integer, ArrayList<NPC>> roomNPCs = InitialData.getRoomNPCs();

		Assertions.assertEquals(1, roomNPCs.size());
		Assertions.assertEquals(1, roomNPCs.get(2).size());
		Assertions.assertEquals("Test NPC", roomNPCs.get(2).get(0).getName());
	}

	@Test
	public void testGetRoomEnemiesThrowsForInvalidEnemyReference() throws IOException {
		InitialData.getRooms();
		InitialData.getEnemies();

		Assertions.assertThrows(
			IllegalStateException.class,
			InitialData::getRoomEnemies
		);
	}

	@Test
	public void testGetRoomItems() throws IOException {
		InitialData.getRooms();

		HashMap<Integer, ArrayList<Item>> roomItems = InitialData.getRoomItems();

		Assertions.assertEquals(1, roomItems.size());
		Assertions.assertEquals(1, roomItems.get(0).size());
		Assertions.assertEquals("Test Potion", roomItems.get(0).get(0).getName());
		Assertions.assertEquals(1, roomItems.get(0).get(0).getAmount());
	}


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

	@Test
	public void testGetPlayerItems() throws IOException {
		HashMap<Integer, Item> playerItems = InitialData.getPlayerItems();

		Assertions.assertEquals(1, playerItems.size());
		Assertions.assertEquals("Test Sword", playerItems.get(0).getName());
		Assertions.assertEquals(1, playerItems.get(0).getAmount());
	}
}

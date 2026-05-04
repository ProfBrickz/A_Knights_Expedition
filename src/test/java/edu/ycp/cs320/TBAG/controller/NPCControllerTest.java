package edu.ycp.cs320.TBAG.controller;
import edu.ycp.cs320.TBAG.model.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NPCControllerTest {

	private NPCController npcController;
	private InventoryController inventoryController;

	private NPC npc;
	private Player player;
	private Item item;

	@BeforeEach
	public void setUp() {
		inventoryController = new InventoryController();
		npcController = new NPCController(inventoryController);

		npc = new NPC(1, "Shopkeeper");
		player = new Player(100, 100);
        player.setCoins(10);
		item = new Item(1, "Potion", "Heals", 5);
	

		// Give NPC the item
		npc.getInventory().getItems().put(1, item.copy());
	}

	// -----------------------------
	// BUY TESTS
	// -----------------------------

	@Test
	public void testBuySuccess() {
		Item result = npcController.buy(npc, player, item, 2);

		Assertions.assertNotNull(result);

		// Player coins decrease (5 * 2 = 10)
		Assertions.assertEquals(90, player.getCoins());

		// Player now has the item
		Assertions.assertTrue(player.getInventory().getItems().containsKey(1));
		Assertions.assertEquals(2, player.getInventory().getItems().get(1).getAmount());
	}

	@Test
	public void testBuyItemNotFound() {
		Item fakeItem = new Item(999, "Fake", "Fake", 10);

		Item result = npcController.buy(npc, player, fakeItem, 1);

		Assertions.assertNull(result);
	}

	@Test
	public void testBuyNotEnoughCoins() {
		player.setCoins(5); // not enough for 2 items (cost = 10)

		Item result = npcController.buy(npc, player, item, 2);

		Assertions.assertNull(result);

		// Coins unchanged
		Assertions.assertEquals(5, player.getCoins());
	}


	// SELL TESTS


	@Test
	public void testSellSuccess() {
		// Give player item first
		player.getInventory().getItems().put(1, item.copy());
		player.getInventory().getItems().get(1).setAmount(3);

		Integer result = npcController.sell(player, item, 2);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(10, result); // value per item

		// Player coins increase (10 * 2 = 20)
		Assertions.assertEquals(120, player.getCoins());

		// Item amount reduced
		Assertions.assertEquals(1, player.getInventory().getItems().get(1).getAmount());
	}

	@Test
	public void testSellItemNotFound() {
		Integer result = npcController.sell(player, item, 1);

		Assertions.assertNull(result);
	}

	@Test
	public void testSellNotEnoughAmount() {
		player.getInventory().getItems().put(1, item.copy());
		player.getInventory().getItems().get(1).setAmount(1);

		Integer result = npcController.sell(player, item, 2);

		Assertions.assertNull(result);
	}
}
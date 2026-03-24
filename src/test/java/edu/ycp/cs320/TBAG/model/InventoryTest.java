package edu.ycp.cs320.TBAG.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InventoryTest {
	private Inventory inventory;

	@BeforeEach
	public void setUp() {
		inventory = new Inventory();
	}

	@Test
	public void testDefaultInventoryEmpty() {
		Assertions.assertNotNull(inventory.getItems());
		Assertions.assertTrue(inventory.getItems().isEmpty());
	}

	@Test
	public void testAddItem() {
		Item potion = new Item("p1", "Potion", "Heals 10 HP", 5, 1);
		inventory.addItem(potion);

		Assertions.assertEquals(1, inventory.getItems().size());
		Assertions.assertSame(potion, inventory.getItems().get("p1"));
	}

	@Test
	public void testAddItemIgnoresNull() {
		inventory.addItem(null);
		Assertions.assertTrue(inventory.getItems().isEmpty());
	}

	@Test
	public void testRemoveItem() {
		Item potion = new Item("p1", "Potion", "Heals 10 HP", 5, 1);
		inventory.addItem(potion);

		inventory.removeItem("p1");
		Assertions.assertTrue(inventory.getItems().isEmpty());
	}

	@Test
	public void testRemoveItemIgnoresNullAndMissing() {
		Item potion = new Item("p1", "Potion", "Heals 10 HP", 5, 1);
		inventory.addItem(potion);

		inventory.removeItem(null);
		Assertions.assertEquals(1, inventory.getItems().size());

		inventory.removeItem("missing");
		Assertions.assertEquals(1, inventory.getItems().size());
		Assertions.assertSame(potion, inventory.getItems().get("p1"));
	}
}

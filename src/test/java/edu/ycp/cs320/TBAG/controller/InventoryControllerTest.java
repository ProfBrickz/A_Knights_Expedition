package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InventoryControllerTest {
	private Inventory inventory;
	private InventoryController controller;

	@BeforeEach
	public void setUp() {
		inventory = new Inventory();
		controller = new InventoryController();
	}

	@Test
	public void testGetItemsNullInventory() {
		Assertions.assertTrue(controller.getItems(null).isEmpty());
	}

	@Test
	public void testGetItemByName() {
		Item potion = new Item("p1", "Potion", "Heals 10 HP", 5, 2);
		inventory.getItems().put(potion.getId(), potion);

		Assertions.assertSame(potion, controller.getItemByName(inventory, "Potion"));
		Assertions.assertNull(controller.getItemByName(inventory, "Missing"));
	}

	@Test
	public void testAddItemAddsAndCapsAmount() {
		Item potion = new Item("p1", "Potion", "Heals 10 HP", 5, 3);
		controller.addItem(inventory, potion, 5);

		Assertions.assertEquals(1, inventory.getItems().size());
		Assertions.assertEquals(3, inventory.getItems().get("p1").getAmount());
	}

	@Test
	public void testAddItemIncrementsExisting() {
		Item potion = new Item("p1", "Potion", "Heals 10 HP", 5, 3);
		controller.addItem(inventory, potion, 2);
		controller.addItem(inventory, potion, 1);

		Assertions.assertEquals(1, inventory.getItems().size());
		Assertions.assertEquals(4, inventory.getItems().get("p1").getAmount());
	}

	@Test
	public void testRemoveItemDecrementsAndRemoves() {
		Item potion = new Item("p1", "Potion", "Heals 10 HP", 5, 3);
		controller.addItem(inventory, potion, 3);

		controller.removeItem(inventory, "p1", 1);
		Assertions.assertEquals(2, inventory.getItems().get("p1").getAmount());

		controller.removeItem(inventory, "p1", 2);
		Assertions.assertFalse(inventory.getItems().containsKey("p1"));
	}

	@Test
	public void testRemoveItemIgnoresInvalidArguments() {
		Item potion = new Item("p1", "Potion", "Heals 10 HP", 5, 1);
		controller.addItem(inventory, potion, 1);

		controller.removeItem(inventory, null, 1);
		controller.removeItem(inventory, "p1", 0);
		controller.removeItem(inventory, "missing", 1);

		Assertions.assertEquals(1, inventory.getItems().size());
	}

	@Test
	public void testTypedFilters() {
		HealingItem heal = new HealingItem("h1", "Heal", "Heals", 5, 2, 1);
		Weapon weapon = new Weapon("w1", "Sword", "Sharp", 10, 1);
		Armor armor = new Armor("a1", "Leather", "Basic", 1, true, 5, 1);
		Item misc = new Item("m1", "Rock", "Just a rock", 0, 1);

		inventory.getItems().put(heal.getId(), heal);
		inventory.getItems().put(weapon.getId(), weapon);
		inventory.getItems().put(armor.getId(), armor);
		inventory.getItems().put(misc.getId(), misc);

		Assertions.assertEquals(1, controller.getHealingItems(inventory).size());
		Assertions.assertEquals(1, controller.getWeapons(inventory).size());
		Assertions.assertEquals(1, controller.getArmor(inventory).size());
	}
}

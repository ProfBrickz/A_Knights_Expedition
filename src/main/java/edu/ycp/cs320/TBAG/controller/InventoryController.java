package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.*;

import java.util.HashMap;

public class InventoryController {
	public InventoryController() {

	}

	public HashMap<String, Item> getItems(Inventory inventory) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public Item getItemByName(Inventory inventory) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	// adds item to inventory if not in it, increments amount if in it
	public void addItem(Inventory inventory, Item item) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	// removes item in inventory if amount == 1, decrements amount if amount > 1
	public void removeItem(Inventory inventory, Integer id, Integer amount) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public void removeItem(Inventory inventory, Integer id) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public HashMap<String, HealingItem> getHealingItems(Inventory inventory) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public HashMap<String, Weapon> getWeapons(Inventory inventory) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public HashMap<String, Armor> getArmor(Inventory inventory) {
		throw new UnsupportedOperationException("TODO - implement");
	}
}

package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.*;

import java.util.HashMap;

public class InventoryController {
	public InventoryController() {

	}

	public HashMap<String, Item> getItems(Inventory inventory) {
		if (inventory == null) {
			return new HashMap<>();
		}

		return inventory.getItems();
	}

	public Item getItemByName(Inventory inventory, String itemName) {
		for (Item item : inventory.getItems().values()) {
			if (item.getName().equals(itemName)) return item;
		}

		return null;
	}

	/**
	 * adds item to inventory if not in it, increments amount if in it
	 */
	public void addItem(Inventory inventory, Item item) {
		if (inventory == null || item == null) {
			return;
		}

		HashMap<String, Item> items = inventory.getItems();
		String key = String.valueOf(item.getId());

		if (items.containsKey(key)) {
			Item existing = items.get(key);
			Integer amount = existing.getAmount();
			if (amount == null) {
				amount = 0;
			}
			existing.setAmount(amount + 1);
		} else {
			items.put(key, item);
		}
	}

	/**
	 * removes item in inventory if amount == 1, decrements amount if amount > 1
	 */
	public void removeItem(Inventory inventory, String id, Integer amount) {
		if (inventory == null || id == null) {
			return;
		}

		if (amount == null || amount <= 0) {
			return;
		}

		HashMap<String, Item> items = inventory.getItems();
		Item existing = items.get(id);

		if (existing == null) {
			return;
		}

		Integer currentAmount = existing.getAmount();
		if (currentAmount == null || currentAmount <= amount) {
			items.remove(id);
		} else {
			existing.setAmount(currentAmount - amount);
		}
	}

	/**
	 * removes one of an item from the inventory
	 */
	public void removeItem(Inventory inventory, String id) {
		removeItem(inventory, id, 1);
	}

	public HashMap<String, HealingItem> getHealingItems(Inventory inventory) {
		HashMap<String, HealingItem> healingItems = new HashMap<>();
		if (inventory == null) {
			return healingItems;
		}

		for (String key : inventory.getItems().keySet()) {
			Item item = inventory.getItems().get(key);
			if (item instanceof HealingItem) {
				healingItems.put(key, (HealingItem) item);
			}
		}

		return healingItems;
	}

	public HashMap<String, Weapon> getWeapons(Inventory inventory) {
		HashMap<String, Weapon> weapons = new HashMap<>();
		if (inventory == null) {
			return weapons;
		}

		for (String key : inventory.getItems().keySet()) {
			Item item = inventory.getItems().get(key);
			if (item instanceof Weapon) {
				weapons.put(key, (Weapon) item);
			}
		}

		return weapons;
	}

	public HashMap<String, Armor> getArmor(Inventory inventory) {
		HashMap<String, Armor> armorItems = new HashMap<>();
		if (inventory == null) {
			return armorItems;
		}

		for (String key : inventory.getItems().keySet()) {
			Item item = inventory.getItems().get(key);
			if (item instanceof Armor) {
				armorItems.put(key, (Armor) item);
			}
		}

		return armorItems;
	}
}

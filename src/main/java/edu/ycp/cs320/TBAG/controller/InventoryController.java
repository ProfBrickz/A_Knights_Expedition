package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.*;

import java.util.HashMap;

public class InventoryController {
	public InventoryController() {

	}

	public HashMap<Integer, Item> getItems(Inventory inventory) {
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
	public void addItem(Inventory inventory, Item item, Integer amount) {
		if (inventory == null || item == null) {
			return;
		}

		if (amount > item.getAmount()) amount = item.getAmount();

		HashMap<Integer, Item> items = inventory.getItems();
		Integer key = item.getId();

		if (items.containsKey(key)) {
			Item existing = items.get(key);
			Integer existingAmount = existing.getAmount();
			if (existingAmount == null) {
				existingAmount = 0;
			}
			existing.setAmount(existingAmount + amount);
		} else {
			items.put(key, new Item(
				item.getId(),
				item.getName(),
				item.getDescription(),
				item.getValue(),
				item.getAmount()
			));
		}
	}

	/**
	 * adds one of an item to the inventory
	 */
	public void addItem(Inventory inventory, Item item) {
		addItem(inventory, item, 1);
	}

	/**
	 * removes item in inventory if amount == 1, decrements amount if amount > 1
	 */
	public void removeItem(Inventory inventory, Integer id, Integer amount) {
		if (inventory == null || id == null) {
			return;
		}

		if (amount == null || amount <= 0) {
			return;
		}

		HashMap<Integer, Item> items = inventory.getItems();
		Item existing = items.get(id);

		if (existing == null) {
			return;
		}

		if (amount > existing.getAmount()) amount = existing.getAmount();

		Integer currentAmount = existing.getAmount();
		if (currentAmount == null || currentAmount.equals(amount)) {
			items.remove(id);
		} else {
			existing.setAmount(currentAmount - amount);
		}
	}

	/**
	 * removes one of an item from the inventory
	 */
	public void removeItem(Inventory inventory, Integer id) {
		removeItem(inventory, id, 1);
	}

	public HashMap<Integer, HealingItem> getHealingItems(Inventory inventory) {
		HashMap<Integer, HealingItem> healingItems = new HashMap<>();
		if (inventory == null) {
			return healingItems;
		}

		for (Integer key : inventory.getItems().keySet()) {
			Item item = inventory.getItems().get(key);
			if (item instanceof HealingItem) {
				healingItems.put(key, (HealingItem) item);
			}
		}

		return healingItems;
	}

	public HashMap<Integer, Weapon> getWeapons(Inventory inventory) {
		HashMap<Integer, Weapon> weapons = new HashMap<>();
		if (inventory == null) {
			return weapons;
		}

		for (Integer key : inventory.getItems().keySet()) {
			Item item = inventory.getItems().get(key);
			if (item instanceof Weapon) {
				weapons.put(key, (Weapon) item);
			}
		}

		return weapons;
	}

	public HashMap<Integer, Armor> getArmor(Inventory inventory) {
		HashMap<Integer, Armor> armorItems = new HashMap<>();
		if (inventory == null) {
			return armorItems;
		}

		for (Integer key : inventory.getItems().keySet()) {
			Item item = inventory.getItems().get(key);
			if (item instanceof Armor) {
				armorItems.put(key, (Armor) item);
			}
		}

		return armorItems;
	}
}

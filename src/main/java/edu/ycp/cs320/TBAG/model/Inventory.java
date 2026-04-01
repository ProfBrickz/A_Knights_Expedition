package edu.ycp.cs320.TBAG.model;

import java.util.HashMap;

public class Inventory {
	private final HashMap<Integer, Item> items = new HashMap<>();

	public Inventory() {

	}

	public HashMap<Integer, Item> getItems() {
		return items;
	}

	public void addItem(Item item) {
		if (item == null) {
			return;
		}

		Integer key = item.getId();

		items.put(key, item);
	}

	public void removeItem(Integer id) {
		if (id == null) {
			return;
		}

		Item existing = items.get(id);

		if (existing == null) {
			return;
		}

		items.remove(id);
	}
}

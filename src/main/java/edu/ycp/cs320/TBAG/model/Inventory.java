package edu.ycp.cs320.TBAG.model;

import java.util.HashMap;

public class Inventory {
	private final HashMap<String, Item> items = new HashMap<String, Item>();

	public Inventory() {

	}

	public HashMap<String, Item> getItems() {
		return items;
	}

	public void addItem(Item item) {
		if (item == null) {
			return;
		}

		String key = String.valueOf(item.getId());

		items.put(key, item);
	}

	public void removeItem(String id) {
		if (id == null) {
			return;
		}

		String key = id;
		Item existing = items.get(key);

		if (existing == null) {
			return;
		}

		items.remove(key);
	}
}

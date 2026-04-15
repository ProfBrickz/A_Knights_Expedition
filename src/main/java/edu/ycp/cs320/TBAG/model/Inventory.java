package edu.ycp.cs320.TBAG.model;

import java.util.ArrayList;
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

	public void addItems(ArrayList<Item> items) {
		for (Item item : items) {
			this.addItem(item);
		}
	}

	public void removeItem(Item item) {
		if (item == null) {
			return;
		}

		Integer id = item.getId();
		Item existing = items.get(id);

		if (existing == null) {
			return;
		}

		items.remove(id);
	}
}

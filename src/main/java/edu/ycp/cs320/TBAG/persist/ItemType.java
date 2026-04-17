package edu.ycp.cs320.TBAG.persist;

import edu.ycp.cs320.TBAG.model.Armor;
import edu.ycp.cs320.TBAG.model.HealingItem;
import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.Weapon;

public enum ItemType {
	//	item or healing or weapon or armor or unlock
	ITEM("item"),
	HEALING("healing"),
	WEAPON("weapon"),
	ARMOR("armor"),
	UNLOCK("unlock");

	private final String name;

	ItemType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	static ItemType getByName(String name) {
		for (ItemType itemType : ItemType.values()) {
			if (itemType.getName().equals(name)) return itemType;
		}

		return null;
	}

	static ItemType getByItem(Item item) {
		if (item instanceof HealingItem) return HEALING;
		if (item instanceof Weapon) return WEAPON;
		if (item instanceof Armor) return ARMOR;

		return ITEM;
	}
}

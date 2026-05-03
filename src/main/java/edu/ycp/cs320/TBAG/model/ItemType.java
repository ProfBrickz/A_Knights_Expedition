package edu.ycp.cs320.TBAG.model;

public enum ItemType {
	//	item or healing or weapon or armor or unlock
	ITEM("Item"),
	HEALING("Healing"),
	WEAPON("Weapon"),
	ARMOR("Armor"),
	UNLOCK("Unlock");

	private final String name;

	ItemType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static ItemType getByName(String name) {
		name = name.toLowerCase();

		for (ItemType itemType : ItemType.values()) {
			if (itemType.getName().toLowerCase().equals(name)) return itemType;
		}

		return null;
	}

	public static ItemType getByItem(Item item) {
		if (item instanceof HealingItem) return HEALING;
		if (item instanceof Weapon) return WEAPON;
		if (item instanceof Armor) return ARMOR;

		return ITEM;
	}
}

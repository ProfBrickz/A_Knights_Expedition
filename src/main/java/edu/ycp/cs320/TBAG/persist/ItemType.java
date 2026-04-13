package edu.ycp.cs320.TBAG.persist;

public enum ItemType {
	//	item or healing or weapon or armor or unlock
	ITEM("item"),
	HEALING("healing"),
	WEAPON("weapon"),
	ARMOR("armor"),
	UNLOCK("unlock");

	private String name;

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
}

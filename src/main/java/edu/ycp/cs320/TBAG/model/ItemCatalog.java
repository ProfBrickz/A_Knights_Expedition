package edu.ycp.cs320.TBAG.model;

public final class ItemCatalog {
	private ItemCatalog() {
	}

	public static void addBaseItemsToInventory(Inventory inventory) {
		if (inventory == null) {
			return;
		}

		Weapon woodenCudgel = new Weapon(
			0,
			"Wooden Cudgel",
			"A wooden pole mainly used for practice. Good for beating people to death",
			1
		);
		woodenCudgel.setAssetName("Wooden_Cudgel.png");
		inventory.addItem(woodenCudgel);

		Armor favoriteShirt = new Armor(
			5,
			"Favorite Shirt",
			"Provides a placebo effect in DEF",
			1,
			false,
			1
		);
		favoriteShirt.setAssetName("Favorite_Shirt.png");
		inventory.addItem(favoriteShirt);
	}

	public static void addAllItemsToInventory(Inventory inventory) {
		if (inventory == null) {
			return;
		}

		// Weapons
		Weapon woodenCudgel = new Weapon(
			0,
			"Wooden Cudgel",
			"A wooden pole mainly used for practice. Good for beating people to death",
			1
		);
		woodenCudgel.setAssetName("Wooden_Cudgel.png");
		inventory.addItem(woodenCudgel);
		inventory.addItem(new Weapon(
			1,
			"Iron Sword",
			"A good iron sword. On the side, it reads \"Made in China\"",
			1
		));
		inventory.addItem(new Weapon(
			2,
			"Skyforge Steel Blade",
			"Forged far in the north. A unique blade from distant lands.",
			1
		));
		inventory.addItem(new Weapon(
			3,
			"Badassium Greatsword",
			"A glowing sword made from some fantasy metal. You get a feeling you should dodge roll with this.",
			1
		));
		inventory.addItem(new Weapon(
			4,
			"Damascus Katana",
			"It is beautiful, it is perfection. I would trade my house for this.",
			1
		));

		// Armor
		Armor favoriteShirt = new Armor(
			5,
			"Favorite Shirt",
			"Provides a placebo effect in DEF",
			1,
			false,
			1
		);
		favoriteShirt.setAssetName("Favorite_Shirt.png");
		inventory.addItem(favoriteShirt);
		inventory.addItem(new Armor(
			6,
			"Wish Body Armor",
			"Terrible, but better than nothing",
			2,
			false,
			1
		));
		inventory.addItem(new Armor(
			7,
			"Pola Tab Chainmail",
			"Made from hundreds of Pola Tabs. The maker died from Pola overdose",
			3,
			false,
			1
		));
		inventory.addItem(new Armor(
			8,
			"Caped Armor",
			"Armor of steel and leather with a fashionable cape. You feel like a hero because all heroes wear capes.",
			5,
			false,
			1
		));
		inventory.addItem(new Armor(
			9,
			"Cosplay Armor",
			"Surprisingly sturdy. Someone must have spent a fortune on this.",
			4,
			false,
			1
		));

		// Consumables
		HealingItem spam = new HealingItem(
			10,
			"Spam",
			"Heals 15% HP",
			15,
			1
		);
		spam.setAssetName("SPAM.png");
		inventory.addItem(spam);
		inventory.addItem(new HealingItem(
			11,
			"Pola Soda",
			"Heals 25% HP",
			25,
			1
		));
		inventory.addItem(new HealingItem(
			12,
			"Vegetable Soup",
			"Heals 50% HP",
			50,
			1
		));
		inventory.addItem(new HealingItem(
			13,
			"Truffle Pie",
			"Heals 100% HP",
			100,
			1
		));
		inventory.addItem(new HealingItem(
			14,
			"Green Omelets",
			"Damages 20% HP, ATK increased by 3",
			-20,
			1
		));
		inventory.addItem(new HealingItem(
			15,
			"Baked Truffle Pie",
			"Heals 100% HP, ATK increased by 5",
			100,
			1
		));

		// Quest items
		inventory.addItem(new Item(
			16,
			"Rick's Key",
			"Key to the Great Canyon",
			0
		));
		inventory.addItem(new Item(
			17,
			"Convenient Ladder",
			"Very convenient. Needed to gain access to the kitchen",
			0
		));
		inventory.addItem(new Item(
			18,
			"Headlamp",
			"Needed to access Certain-Death cave",
			0
		));
		inventory.addItem(new Item(
			19,
			"McGuffin Muffin",
			"Tastiest muffin to further the plot. Needed to access the Dragoon's man-cave",
			0
		));

		// Misc items
		inventory.addItem(new Item(
			20,
			"Truffles",
			"Quest item",
			0
		));
	}
}

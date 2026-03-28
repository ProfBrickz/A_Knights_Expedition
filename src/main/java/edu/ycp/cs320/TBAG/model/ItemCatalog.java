package edu.ycp.cs320.TBAG.model;

public final class ItemCatalog {
	private ItemCatalog() {
	}

	public static void addBaseItemsToInventory(Inventory inventory) {
		if (inventory == null) {
			return;
		}

		inventory.addItem(new Weapon(
			"w_wooden_cudgel",
			"Wooden-Cudgel",
			"A wooden pole mainly used for practice. Good for beating people to death",
			1
		));

		inventory.addItem(new Armor(
			"a_favorite_shirt",
			"Favorite-Shirt",
			"Provides a placebo effect in DEF",
			1,
			false,
			1
		));
	}

	public static void addAllItemsToInventory(Inventory inventory) {
		if (inventory == null) {
			return;
		}

		// Weapons
		inventory.addItem(new Weapon(
			"w_wooden_cudgel",
			"Wooden Cudgel",
			"A wooden pole mainly used for practice. Good for beating people to death",
			1
		));
		inventory.addItem(new Weapon(
			"w_iron_sword",
			"Iron Sword",
			"A good iron sword. On the side, it reads \"Made in China\"",
			1
		));
		inventory.addItem(new Weapon(
			"w_skyforge_steel_blade",
			"Skyforge Steel Blade",
			"Forged far in the north. A unique blade from distant lands.",
			1
		));
		inventory.addItem(new Weapon(
			"w_badassium_greatsword",
			"Badassium Greatsword",
			"A glowing sword made from some fantasy metal. You get a feeling you should dodge roll with this.",
			1
		));
		inventory.addItem(new Weapon(
			"w_damascus_katana",
			"Damascus Katana",
			"It is beautiful, it is perfection. I would trade my house for this.",
			1
		));

		// Armor
		inventory.addItem(new Armor(
			"a_favorite_shirt",
			"Favorite Shirt",
			"Provides a placebo effect in DEF",
			1,
			false,
			1
		));
		inventory.addItem(new Armor(
			"a_wish_body_armor",
			"Wish Body Armor",
			"Terrible, but better than nothing",
			2,
			false,
			1
		));
		inventory.addItem(new Armor(
			"a_pola_tab_chainmail",
			"Pola Tab Chainmail",
			"Made from hundreds of Pola Tabs. The maker died from Pola overdose",
			3,
			false,
			1
		));
		inventory.addItem(new Armor(
			"a_caped_armor",
			"Caped Armor",
			"Armor of steel and leather with a fashionable cape. You feel like a hero because all heroes wear capes.",
			5,
			false,
			1
		));
		inventory.addItem(new Armor(
			"a_cosplay_armor",
			"Cosplay Armor",
			"Surprisingly sturdy. Someone must have spent a fortune on this.",
			4,
			false,
			1
		));

		// Consumables
		inventory.addItem(new HealingItem(
			"h_spam",
			"Spam",
			"Heals 15% HP",
			15,
			1
		));
		inventory.addItem(new HealingItem(
			"h_pola_soda",
			"Pola Soda",
			"Heals 25% HP",
			25,
			1
		));
		inventory.addItem(new HealingItem(
			"h_vegetable_soup",
			"Vegetable Soup",
			"Heals 50% HP",
			50,
			1
		));
		inventory.addItem(new HealingItem(
			"h_truffle_pie",
			"Truffle Pie",
			"Heals 100% HP",
			100,
			1
		));
		inventory.addItem(new HealingItem(
			"h_green_omelets",
			"Green Omelets",
			"Damages 20% HP, ATK increased by 3",
			-20,
			1
		));
		inventory.addItem(new HealingItem(
			"h_baked_truffle_pie",
			"Baked Truffle Pie",
			"Heals 100% HP, ATK increased by 5",
			100,
			1
		));

		// Quest items
		inventory.addItem(new Item(
			"q_ricks_key",
			"Rick's Key",
			"Key to the Great Canyon",
			0
		));
		inventory.addItem(new Item(
			"q_convenient_ladder",
			"Convenient Ladder",
			"Very convenient. Needed to gain access to the kitchen",
			0
		));
		inventory.addItem(new Item(
			"q_headlamp",
			"Headlamp",
			"Needed to access Certain-Death cave",
			0
		));
		inventory.addItem(new Item(
			"q_mcguffin_muffin",
			"McGuffin Muffin",
			"Tastiest muffin to further the plot. Needed to access the Dragoon's man-cave",
			0
		));

		// Misc items
		inventory.addItem(new Item(
			"m_truffles",
			"Truffles",
			"Quest item",
			0
		));
	}
}

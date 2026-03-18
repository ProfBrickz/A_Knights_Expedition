package edu.ycp.cs320.TBAG.model;

import java.util.HashMap;

public class Weapon extends Item {
	private final HashMap<String, WeaponAbility> abilities = new HashMap<>();

	public Weapon(
		String id,
		String name,
		String description,
		Integer sellValue,
		Integer amount
	) {
		super(id, name, description, sellValue, amount);

		throw new UnsupportedOperationException("TODO - implement");
	}

	public Weapon(
		String id,
		String name,
		String description,
		Integer sellValue
	) {
		super(id, name, description, sellValue);

		throw new UnsupportedOperationException("TODO - implement");
	}

	public HashMap<String, WeaponAbility> getAbilities() {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public WeaponAbility addAbility(WeaponAbility ability) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public void removeAbility(String ability) {
		throw new UnsupportedOperationException("TODO - implement");
	}
}

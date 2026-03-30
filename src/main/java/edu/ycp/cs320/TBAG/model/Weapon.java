package edu.ycp.cs320.TBAG.model;

import java.util.HashMap;

public class Weapon extends Item {
	private final HashMap<String, WeaponAbility> abilities = new HashMap<>();

	public Weapon(
		String id,
		String name,
		String description,
		Integer value,
		Integer amount
	) {
		super(id, name, description, value, amount);
	}

	public Weapon(
		String id,
		String name,
		String description,
		Integer value
	) {
		super(id, name, description, value);
	}

	public HashMap<String, WeaponAbility> getAbilities() {
		return abilities;
	}

	public WeaponAbility addAbility(WeaponAbility ability) {
		return abilities.put(ability.getId(), ability);
	}

	public WeaponAbility removeAbility(WeaponAbility ability) {
		return abilities.remove(ability.getId());
	}
}

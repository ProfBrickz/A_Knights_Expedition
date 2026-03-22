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
	}

	public Weapon(
		String id,
		String name,
		String description,
		Integer sellValue
	) {
		super(id, name, description, sellValue);
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

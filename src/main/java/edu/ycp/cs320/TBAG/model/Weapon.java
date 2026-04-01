package edu.ycp.cs320.TBAG.model;

import java.util.HashMap;

public class Weapon extends Item {
	private final HashMap<Integer, WeaponAbility> abilities = new HashMap<>();

	public Weapon(
		Integer id,
		String name,
		String description,
		Integer value,
		Integer amount
	) {
		super(id, name, description, value, amount);
	}

	public Weapon(
		Integer id,
		String name,
		String description,
		Integer value
	) {
		super(id, name, description, value);
	}

	public HashMap<Integer, WeaponAbility> getAbilities() {
		return abilities;
	}

	public WeaponAbility addAbility(WeaponAbility ability) {
		return abilities.put(ability.getId(), ability);
	}

	public WeaponAbility removeAbility(WeaponAbility ability) {
		return abilities.remove(ability.getId());
	}
}

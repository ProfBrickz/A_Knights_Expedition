package edu.ycp.cs320.TBAG.model;

public class Weapon extends Item {
	public Weapon(
		Integer id,
		String name,
		String description,
		Integer sellValue,
		Integer amount
	) {
		super(id, name, description, sellValue, amount);

		throw new UnsupportedOperationException("TODO - implement");
	}

	public Weapon(
		Integer id,
		String name,
		String description,
		Integer sellValue
	) {
		super(id, name, description, sellValue);

		throw new UnsupportedOperationException("TODO - implement");
	}
}

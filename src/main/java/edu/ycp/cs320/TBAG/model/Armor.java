package edu.ycp.cs320.TBAG.model;

public class Armor extends Item {
	Integer defense;
	Boolean active;

	public Armor(
		Integer id,
		String name,
		String description,
		Integer defense,
		Integer active,
		Integer sellValue,
		Integer amount
	) {
		super(id, name, description, sellValue, amount);

		throw new UnsupportedOperationException("TODO - implement");
	}

	public Armor(
		Integer id,
		String name,
		String description,
		Integer defense,
		Integer active,
		Integer sellValue
	) {
		super(id, name, description, sellValue);

		throw new UnsupportedOperationException("TODO - implement");
	}
}

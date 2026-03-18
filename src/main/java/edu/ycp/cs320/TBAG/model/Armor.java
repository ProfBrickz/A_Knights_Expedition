package edu.ycp.cs320.TBAG.model;

public class Armor extends Item {
	private Integer defense;
	private Boolean active;

	public Armor(
		String id,
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
		String id,
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

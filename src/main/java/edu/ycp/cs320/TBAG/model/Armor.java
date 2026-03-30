package edu.ycp.cs320.TBAG.model;

public class Armor extends Item {
	private Integer defense;
	private Boolean active;

	public Armor(
		String id,
		String name,
		String description,
		Integer defense,
		Boolean active,
		Integer sellValue,
		Integer amount
	) {
		super(id, name, description, sellValue, amount);

		this.defense = defense;
		this.active = active;
	}

	public Armor(
		String id,
		String name,
		String description,
		Integer defense,
		Boolean active,
		Integer value
	) {
		super(id, name, description, value);

		this.defense = defense;
		this.active = active;
	}

	public Integer getDefense() {
		return defense;
	}

	public void setDefense(Integer defense) {
		this.defense = defense;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
}

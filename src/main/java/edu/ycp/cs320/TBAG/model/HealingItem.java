package edu.ycp.cs320.TBAG.model;

public class HealingItem extends Item {
	private Integer healAmount;

	public HealingItem(
		String id,
		String name,
		String description,
		Integer healAmount,
		Integer sellValue,
		Integer amount
	) {
		super(id, name, description, sellValue, amount);

		this.healAmount = healAmount;
	}

	public HealingItem(
		String id,
		String name,
		String description,
		Integer healAmount,
		Integer sellValue
	) {
		super(id, name, description, sellValue);

		this.healAmount = healAmount;
	}

	public Integer getHealAmount() {
		return healAmount;
	}

	public void setHealAmount(Integer healAmount) {
		this.healAmount = healAmount;
	}
}

package edu.ycp.cs320.TBAG.model;

public class HealingItem extends Item {
	private Integer healAmount;

	public HealingItem(
		Integer id,
		String name,
		String description,
		Integer healAmount,
		Integer value,
		Integer amount,
		String assetName
	) {
		super(id, name, description, value, amount, assetName);

		this.healAmount = healAmount;
	}

	public HealingItem(
		Integer id,
		String name,
		String description,
		Integer healAmount,
		Integer value,
		String assetName
	) {
		super(id, name, description, value, assetName);

		this.healAmount = healAmount;
	}

	public HealingItem(
		Integer id,
		String name,
		String description,
		Integer healAmount,
		Integer value
	) {
		super(id, name, description, value);

		this.healAmount = healAmount;
	}

	public HealingItem(
		Integer id,
		String name,
		String description,
		Integer healAmount,
		Integer value,
		Integer amount
	) {
		super(id, name, description, value, amount);

		this.healAmount = healAmount;
	}


	public Integer getHealAmount() {
		return healAmount;
	}

	public void setHealAmount(Integer healAmount) {
		this.healAmount = healAmount;
	}
}

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

		throw new UnsupportedOperationException("TODO - implement");
	}

	public HealingItem(
		String id,
		String name,
		String description,
		Integer healAmount,
		Integer sellValue
	) {
		super(id, name, description, sellValue);

		throw new UnsupportedOperationException("TODO - implement");
	}

	public Integer getHealAmount() {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public void setHealAmount(Integer healAmount) {
		throw new UnsupportedOperationException("TODO - implement");
	}
}

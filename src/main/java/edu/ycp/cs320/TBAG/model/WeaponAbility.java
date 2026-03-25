package edu.ycp.cs320.TBAG.model;

public class WeaponAbility {
	private final String id;
	private Integer damage;
	private String attackDescription;

	public WeaponAbility(String id, Integer damage, String attackDescription) {
		this.id = id;
		this.damage = damage;
		this.attackDescription = attackDescription;
	}

	public String getId() {
		return id;
	}

	public Integer getDamage() {
		return damage;
	}

	public void setDamage(Integer damage) {
		this.damage = damage;
	}

	public String getAttackDescription() {
		return attackDescription;
	}

	public void setAttackDescription(String attackDescription) {
		this.attackDescription = attackDescription;
	}
}

package edu.ycp.cs320.TBAG.model;

public class Enemy extends BattleEntity {
	private final Integer id;
	private String name;

	private int attackPower;
	private int specialPower;
	private double specialChance; // 0.0 - 1.0

	public Enemy(Integer id, String name, Integer maxHealth, Integer health) {
		super(maxHealth, health);

		this.id = id;
		this.name = name;

		// default values (can later load from CSV)
		this.attackPower = 5;
		this.specialPower = 10;
		this.specialChance = 0.2; // 20% chance
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAttackPower() {
		return attackPower;
	}

	public int getSpecialPower() {
		return specialPower;
	}

	public double getSpecialChance() {
		return specialChance;
	}
}

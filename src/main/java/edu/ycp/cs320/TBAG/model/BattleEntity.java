package edu.ycp.cs320.TBAG.model;

public class BattleEntity extends Entity {
	private int defense = 0;
	private int temporaryDefense = 0;

	public BattleEntity(Integer maxHealth, Integer health) {
		super(maxHealth, health);
	}

	public int getDefense() {
		return defense;
	}

	public void setDefense(int defense) {
		this.defense = defense;
	}

	public int getTemporaryDefense() {
		return temporaryDefense;
	}

	public void setTemporaryDefense(int temporaryDefense) {
		this.temporaryDefense = temporaryDefense;
	}

	public void resetTemporaryDefense() {
		this.temporaryDefense = 0;
	}
}

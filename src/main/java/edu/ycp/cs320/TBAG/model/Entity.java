package edu.ycp.cs320.TBAG.model;

public class Entity {
	private Integer health, maxHealth;

	public Entity(Integer maxHealth, Integer health) {
		this.maxHealth = maxHealth;
		this.health = health;
	}

	public Integer getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(Integer maxHealth) {
		this.maxHealth = maxHealth;
	}

	public Integer getHealth() {
		return health;
	}

	public void setHealth(Integer health) {
		this.health = health;
	}
}

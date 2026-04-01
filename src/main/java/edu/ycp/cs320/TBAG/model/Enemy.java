package edu.ycp.cs320.TBAG.model;

public class Enemy extends BattleEntity {
	private final Integer id;
	private String name;

	public Enemy(Integer id, String name, Integer maxHealth, Integer health) {
		super(maxHealth, health);

		this.id = id;
		this.name = name;
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
}

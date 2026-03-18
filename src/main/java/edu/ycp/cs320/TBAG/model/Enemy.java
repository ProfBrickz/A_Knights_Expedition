package edu.ycp.cs320.TBAG.model;

public class Enemy extends BattleEntity {
	private final Integer id;
	private String name;

	public Enemy(Integer id, String name, Integer maxHealth, Integer health) {
		super(maxHealth, health);

		throw new UnsupportedOperationException("TODO - implement");
	}

	public Integer getId() {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public String getName() {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public void setName(String name) {
		throw new UnsupportedOperationException("TODO - implement");
	}
}

package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.Enemy;
import edu.ycp.cs320.TBAG.model.Player;

public class EnemyController {
	private final BattleEntityController battleEntityController;

	public EnemyController(BattleEntityController battleEntityController) {
		this.battleEntityController = battleEntityController;
	}

	// Randomly chooses WeaponAbility
	public void attack(Enemy enemy, Player player) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	// Randomly chooses Armor
	// Throws IllegalArgumentException if armor is active
	public void defend(Enemy enemy) throws IllegalArgumentException {
		throw new UnsupportedOperationException("TODO - implement");
	}

	// Randomly chooses HealingItem
	public void heal(Enemy enemy) {
		throw new UnsupportedOperationException("TODO - implement");
	}
}

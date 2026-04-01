package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.*;

import java.util.Random;

public class EnemyController {
	private final BattleEntityController battleEntityController;
	private final Random random = new Random();

	public EnemyController(BattleEntityController battleEntityController) {
		this.battleEntityController = battleEntityController;
	}

	public void attack(Enemy enemy, Player player) {
		WeaponAbility ability = new WeaponAbility(0, 5, "Enemy attacks!");
		battleEntityController.attack(enemy, player, ability);
	}

	public void defend(Enemy enemy) {
		Armor armor = new Armor(0, "Hide", "Basic armor", 3, false, 0);

		if (armor.getActive()) {
			throw new IllegalArgumentException("Armor already active");
		}

		battleEntityController.defend(enemy, armor);
	}

	public void heal(Enemy enemy) {
		HealingItem heal = new HealingItem(1, "Potion", "Heals", 10, 0);
		battleEntityController.heal(enemy, heal);
	}
}

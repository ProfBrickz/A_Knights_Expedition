package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.*;
import java.util.Random;

public class EnemyController {
	private final BattleEntityController battleEntityController;
	private final Random random = new Random();

	public EnemyController(BattleEntityController battleEntityController) {
		this.battleEntityController = battleEntityController;
	}

	public void takeTurn(Enemy enemy, Player player) {
		double roll = random.nextDouble();

		// PRIORITY: low health → heal
		if (enemy.getHealth() < enemy.getMaxHealth() * 0.3 && roll < 0.3) {
			heal(enemy);
			System.out.println(enemy.getName() + " uses a potion!");
			return;
		}

		// SPECIAL MOVE
		if (roll < enemy.getSpecialChance()) {
			specialMove(enemy, player);
			System.out.println(enemy.getName() + " uses a SPECIAL attack!");
			return;
		}

		// DEFEND
		if (roll < 0.6) {
			defend(enemy);
			System.out.println(enemy.getName() + " defends!");
			return;
		}

		// NORMAL ATTACK
		attack(enemy, player);
		System.out.println(enemy.getName() + " attacks!");
	}

	public void attack(Enemy enemy, Player player) {
		WeaponAbility ability = new WeaponAbility(0, enemy.getAttackPower(), "Enemy attacks!");
		battleEntityController.attack(enemy, player, ability);
	}

	public void specialMove(Enemy enemy, Player player) {
		WeaponAbility ability = new WeaponAbility(1, enemy.getSpecialPower(), "Enemy special attack!");
		battleEntityController.attack(enemy, player, ability);
	}

	public void defend(Enemy enemy) {
		Armor armor = new Armor(0, "Hide", "Basic armor", 3, false, 0);
		battleEntityController.defend(enemy, armor);
	}

	public void heal(Enemy enemy) {
		HealingItem heal = new HealingItem(1, "Potion", "Heals", 10, 0);
		battleEntityController.heal(enemy, heal);
	}
}

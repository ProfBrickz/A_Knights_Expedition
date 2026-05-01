package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.*;
import java.util.Random;

public class EnemyController {
	private final BattleEntityController battleEntityController;
	private final Random random = new Random();

	public EnemyController(BattleEntityController battleEntityController) {
		this.battleEntityController = battleEntityController;
	}

	public String takeTurn(Enemy enemy, Player player, WeaponAbility ability) {
		double roll = random.nextDouble();

		if (enemy == null || player == null) {
			return "";
		}

		// PRIORITY: low health → heal
		if (enemy.getHealth() < enemy.getMaxHealth() * 0.3 && roll < 0.3) {
			heal(enemy);
			System.out.println(enemy.getName() + " uses a potion!");
			return enemy.getName() + " uses a potion!";
		}

		// SPECIAL MOVE
		if (roll < enemy.getSpecialChance()) {
			specialMove(enemy, player);
			System.out.println(enemy.getName() + " uses a SPECIAL attack!");
			return enemy.getName() + " uses a SPECIAL attack!";
		}

		// DEFEND
		if (roll < 0.6) {
			defend(enemy);
			System.out.println(enemy.getName() + " defends!");
			return enemy.getName() + " defends!";
		}

		// NORMAL ATTACK
		attack(enemy, player, ability);
		System.out.println(enemy.getName() + " attacks!");
		return enemy.getName() + " " + ability.getAttackDescription() +"!";
	}

	public void attack(Enemy enemy, Player player, WeaponAbility ability) {
		//WeaponAbility ability = new WeaponAbility(0, enemy.getAttackPower(), "Enemy attacks!");
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

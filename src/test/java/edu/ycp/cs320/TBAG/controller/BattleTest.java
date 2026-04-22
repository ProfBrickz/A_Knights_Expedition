package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
//edit to look more like previous

public class BattleTest(Player player, Enemy enemy) {
	BattleEntityController battleController = new BattleEntityController();
	EnemyController enemyController = new EnemyController(battleController);

	Scanner scanner = new Scanner(System.in);

	while (player.getHealth() > 0 && enemy.getHealth() > 0) {
		System.out.println("\n--- BATTLE ---");
		System.out.println("Player HP: " + player.getHealth());
		System.out.println(enemy.getName() + " HP: " + enemy.getHealth());

		System.out.println("Choose action: 1) Attack 2) Defend 3) Use Item");
		int choice = scanner.nextInt();

		switch (choice) {
			case 1:
				battleController.attack(player, enemy, new WeaponAbility(0, 8, "Slash"));
				break;
			case 2:
				battleController.defend(player, new Armor(0, "Shield", "Basic", 4, false, 0));
				break;
			case 3:
				battleController.heal(player, new HealingItem(0, "Potion", "Heals", 10, 0));
				break;
			default:
				System.out.println("Invalid action.");
				continue;
		}

		// enemy turn
		if (enemy.getHealth() > 0) {
			enemyController.takeTurn(enemy, player);
		}
	}

	if (player.getHealth() <= 0) {
		System.out.println("You were defeated.");
	} else {
		System.out.println("Enemy defeated!");
	}
}

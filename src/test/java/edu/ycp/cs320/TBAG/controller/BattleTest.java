package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class BattleTest {
	private Player player;
	private Enemy enemy;
	private BattleEntityController battleController;
	private EnemyController enemyController;
	private PlayerController playerController;

	@BeforeEach
	public void setUp() {
		player = new Player(100, 100);
		enemy = new Enemy(0, "Goblin", 30, 30);
		battleController = new BattleEntityController();
		enemyController = new EnemyController(battleController);
		playerController = new PlayerController(player, battleController);

		player.setState(PlayerState.BATTLE);
	}

	@Nested
	class AttackTests {
		@Test
		public void playerAttackReducesEnemyHealth() {
			WeaponAbility slash = new WeaponAbility(0, 10, "Slash");

			battleController.attack(player, enemy, slash);

			Assertions.assertEquals(20, enemy.getHealth());
		}

		@Test
		public void attackCannotReduceBelowZero() {
			WeaponAbility smash = new WeaponAbility(0, 999, "Smash");

			battleController.attack(player, enemy, smash);

			Assertions.assertEquals(0, enemy.getHealth());
		}
	}

	@Nested
	class DefendTests {
		@Test
		public void defendAddsTemporaryDefense() {
			Armor shield = new Armor(0, "Shield", "Basic shield", 4, true, 0);

			battleController.defend(player, shield);

			Assertions.assertEquals(4, player.getTemporaryDefense());
		}

		@Test
		public void attackConsumesTemporaryDefense() {
			Armor shield = new Armor(0, "Shield", "Basic shield", 4, true, 0);
			battleController.defend(player, shield);

			WeaponAbility slash = new WeaponAbility(0, 6, "Slash");
			battleController.attack(enemy, player, slash);

			Assertions.assertEquals(98, player.getHealth());
			Assertions.assertEquals(0, player.getTemporaryDefense());
		}
	}

	@Nested
	class HealTests {
		@Test
		public void healCapsAtMaxHealth() {
			player.setHealth(95);

			HealingItem potion = new HealingItem(0, "Potion", "Heals", 10, 0);
			battleController.heal(player, potion);

			Assertions.assertEquals(100, player.getHealth());
		}

		@Test
		public void healRaisesHealth() {
			player.setHealth(50);

			HealingItem potion = new HealingItem(0, "Potion", "Heals", 10, 0);
			battleController.heal(player, potion);

			Assertions.assertEquals(60, player.getHealth());
		}
	}

	@Nested
	class StateTests {
		@Test
		public void fleeReturnsPlayerToExploring() {
			Assertions.assertTrue(playerController.flee());
			Assertions.assertEquals(PlayerState.EXPLORING, player.getState());
		}

		@Test
		public void fleeFailsOutsideBattle() {
			player.setState(PlayerState.EXPLORING);
			Assertions.assertFalse(playerController.flee());
		}
	}
}

package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class BattleTest {

	private BattleEntityController controller;
	private Player player;
	private Enemy enemy;

	@BeforeEach
	public void setup() {
		controller = new BattleEntityController();
		player = new Player(100, 100);
		enemy = new Enemy(0, "Goblin", 50, 50);
	}

	@Test
	public void testAttack() {
		WeaponAbility ability = new WeaponAbility(0, 10, "Slash attack");

		controller.attack(player, enemy, ability);

		assertEquals(40, (int) enemy.getHealth());
	}

	@Test
	public void testDefendReducesDamage() {
		Armor armor = new Armor(0, "Shield", "Basic shield", 5, false, 0);

		controller.defend(enemy, armor);

		WeaponAbility ability = new WeaponAbility(0, 10, "Hit");

		controller.attack(player, enemy, ability);

		// 10 damage - 5 defense = 5 damage
		assertEquals(45, (int) enemy.getHealth());
	}

	@Test
	public void testHeal() {
		enemy.setHealth(20);

		HealingItem potion = new HealingItem(0, "Potion", "Heal", 15, 0);

		controller.heal(enemy, potion);

		assertEquals(35, (int) enemy.getHealth());
	}

	@Test
	public void testHealDoesNotExceedMax() {
		HealingItem potion = new HealingItem(0, "Potion", "Heal", 50, 0);

		controller.heal(player, potion);

		assertEquals(100, (int) player.getHealth());
	}

	@Test
	public void testAttackCannotGoBelowZero() {
		WeaponAbility ability = new WeaponAbility(0, 1000, "Big attack");

		controller.attack(player, enemy, ability);

		assertEquals(0, (int) enemy.getHealth());
	}
}

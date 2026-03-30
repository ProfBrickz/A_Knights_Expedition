package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.*;

public class BattleEntityController {

	public BattleEntityController() {}

	// ATTACK
	public void attack(BattleEntity attacker, BattleEntity target, WeaponAbility ability) {
		if (attacker == null || target == null || ability == null) {
			return;
		}

		int damage = ability.getDamage();

		int defense = 0;
		if (target instanceof BattleEntity) {
			defense = ((BattleEntity) target).getTemporaryDefense()
				+ ((BattleEntity) target).getDefense();
		}

		int finalDamage = damage - defense;
		if (finalDamage < 0) {
			finalDamage = 0;
		}

		int newHealth = target.getHealth() - finalDamage;
		if (newHealth < 0) {
			newHealth = 0;
		}

		target.setHealth(newHealth);

		// reset defend after hit
		target.resetTemporaryDefense();
	}

	// DEFEND
	public void defend(BattleEntity entity, Armor armor) {
		if (entity == null || armor == null) {
			return;
		}

		entity.setTemporaryDefense(entity.getTemporaryDefense() + armor.getDefense());
		armor.setActive(true);
	}

	// HEAL
	public void heal(BattleEntity entity, HealingItem item) {
		if (entity == null || item == null) {
			return;
		}

		int newHealth = entity.getHealth() + item.getHealAmount();

		if (newHealth > entity.getMaxHealth()) {
			newHealth = entity.getMaxHealth();
		}

		if (newHealth < 0) {
			newHealth = 0;
		}

		entity.setHealth(newHealth);
	}
}

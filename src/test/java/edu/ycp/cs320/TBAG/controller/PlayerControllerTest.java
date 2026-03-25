package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlayerControllerTest {
	private Player player;
	private Room roomA;
	private Room roomB;
	private PlayerController controller;
	private TestBattleEntityController battleEntityController;

	private static class TestBattleEntityController extends BattleEntityController {
		boolean attackCalled = false;
		boolean defendCalled = false;
		boolean healCalled = false;

		@Override
		public void attack(BattleEntity attacker, BattleEntity target, WeaponAbility weaponAbility) {
			attackCalled = true;
		}

		@Override
		public void defend(BattleEntity battleEntity, Armor armor) {
			defendCalled = true;
		}

		@Override
		public void heal(BattleEntity battleEntity, HealingItem healingItem) {
			healCalled = true;
		}
	}

	@BeforeEach
	public void setUp() {
		player = new Player(100, 100);
		roomA = new Room("A", "Room A", "First room");
		roomB = new Room("B", "Room B", "Second room");
		roomA.setConnection(roomB, "north");
		roomB.setConnection(roomA, "south");
		player.setRoom(roomA);
		battleEntityController = new TestBattleEntityController();
		controller = new PlayerController(player, battleEntityController);
	}

	@Test
	public void testMoveSuccessAndFailure() {
		Assertions.assertTrue(controller.move("north"));
		Assertions.assertSame(roomB, player.getRoom());

		Assertions.assertFalse(controller.move("east"));
		Assertions.assertSame(roomB, player.getRoom());
	}

	@Test
	public void testMoveFailsWithoutRoom() {
		player.setRoom(null);
		Assertions.assertFalse(controller.move("north"));
	}

	@Test
	public void testEquipAndUnequipArmor() {
		Armor armor = new Armor("a1", "Leather", "Basic armor", 1, true, 5, 1);

		controller.equipArmor(armor);
		Assertions.assertTrue(player.getArmor().contains(armor));

		Assertions.assertThrows(IllegalArgumentException.class, () -> controller.equipArmor(armor));

		controller.unequipArmor(armor);
		Assertions.assertFalse(player.getArmor().contains(armor));

		Assertions.assertThrows(IllegalArgumentException.class, () -> controller.unequipArmor(armor));
	}

	@Test
	public void testDieResetsStatsAndState() {
		player.setState(PlayerState.BATTLE);
		player.setHealth(1);
		player.setMaxHealth(10);
		Room startingRoom = new Room("S", "Start", "Start room");

		controller.die(50, 50, startingRoom);

		Assertions.assertEquals(50, player.getMaxHealth());
		Assertions.assertEquals(50, player.getHealth());
		Assertions.assertSame(startingRoom, player.getRoom());
		Assertions.assertEquals(PlayerState.EXPLORING, player.getState());
	}

	@Test
	public void testAttackDefendHealDelegate() {
		Enemy enemy = new Enemy("e1", "Goblin", 10, 10);
		WeaponAbility ability = new WeaponAbility("w1", 3, "Slash");
		HealingItem potion = new HealingItem("h1", "Potion", "Heals", 5, 2, 1);
		Armor armor = new Armor("a1", "Leather", "Basic armor", 1, true, 5, 1);

		controller.attack(enemy, ability);
		controller.defend(armor);
		controller.heal(potion);

		Assertions.assertTrue(battleEntityController.attackCalled);
		Assertions.assertTrue(battleEntityController.defendCalled);
		Assertions.assertTrue(battleEntityController.healCalled);
	}

	@Test
	public void testFlee() {
		Assertions.assertFalse(controller.flee());
		Assertions.assertEquals(PlayerState.EXPLORING, player.getState());

		player.setState(PlayerState.BATTLE);
		Assertions.assertTrue(controller.flee());
		Assertions.assertEquals(PlayerState.EXPLORING, player.getState());
	}

	@Test
	public void testInspectItem() {
		Item item = new Item("i1", "Key", "A small key", 1, 1);
		Assertions.assertEquals("A small key", controller.inspectItem(item));
		Assertions.assertNull(controller.inspectItem(null));
	}
}

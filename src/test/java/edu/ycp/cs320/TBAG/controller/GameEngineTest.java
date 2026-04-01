package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class GameEngineTest {
	private Player player;
	private HashMap<Integer, Room> rooms;
	private GameEngine gameEngine;
	private ArrayList<String> arguments;

	@BeforeEach
	public void setUp() {
		player = new Player(100, 100);
		rooms = new HashMap<>();
		Room roomA = new Room(0, "a", "description a");
		Room roomB = new Room(1, "b", "description b");

		roomA.setConnection(roomB, "north");
		roomB.setConnection(roomA, "south");

		NPC npc = new NPC(0, "name");
		roomA.addNPC(npc);

		rooms.put(roomA.getID(), roomA);
		rooms.put(roomB.getID(), roomB);
		gameEngine = new GameEngine(player, rooms);

		this.arguments = new ArrayList<>();
	}

	@Test
	public void testCreateRooms() {
		rooms.clear();
		gameEngine = new GameEngine(player, rooms);

		Assertions.assertFalse(rooms.isEmpty());
	}

	@Nested
	class DefaultPlayerRooms {
		@Test
		public void valid() {
			Room playerRoom = player.getRoom();

			Assertions.assertEquals(0, playerRoom.getID());
			Assertions.assertEquals("a", playerRoom.getName());
			Assertions.assertEquals("description a", playerRoom.getDescription());
		}

		@Test
		public void noMatchingRoom() {
			player = new Player(100, 100);
			rooms = new HashMap<>();
			Room roomA = new Room(1, "a", "description a");
			rooms.put(roomA.getID(), roomA);
			Assertions.assertNull(player.getRoom());
		}
	}

	@Nested
	class MoveTests {
		@Test
		public void valid() {
			arguments.add("north");
			Assertions.assertEquals("description b\n\n", gameEngine.inputCommand("move", arguments));
			Room playerRoom = player.getRoom();
			Assertions.assertEquals(1, playerRoom.getID());
			Assertions.assertEquals("b", playerRoom.getName());
			Assertions.assertEquals("description b", playerRoom.getDescription());

			arguments.clear();
			arguments.add("south");
			Assertions.assertEquals("description a\n\n", gameEngine.inputCommand("move", arguments));
			playerRoom = player.getRoom();
			Assertions.assertEquals(0, playerRoom.getID());
			Assertions.assertEquals("a", playerRoom.getName());
			Assertions.assertEquals("description a", playerRoom.getDescription());
		}

		@Test
		public void invalidDirection() {
			arguments.add("left");
			Assertions.assertEquals("Invalid direction for this room\n\n", gameEngine.inputCommand("move", arguments));
			Room playerRoom = player.getRoom();
			Assertions.assertEquals(0, playerRoom.getID());
			Assertions.assertEquals("a", playerRoom.getName());
			Assertions.assertEquals("description a", playerRoom.getDescription());
		}

		@Test
		public void emptyString() {
			arguments.add("");
			Assertions.assertEquals("Invalid direction for this room\n\n", gameEngine.inputCommand("move", arguments));
			Room playerRoom = player.getRoom();
			Assertions.assertEquals(0, playerRoom.getID());
			Assertions.assertEquals("a", playerRoom.getName());
			Assertions.assertEquals("description a", playerRoom.getDescription());
		}

		@Test
		public void whitespace() {
			arguments.add(" ");
			Assertions.assertEquals("Invalid direction for this room\n\n", gameEngine.inputCommand("move", arguments));
			Room playerRoom = player.getRoom();
			Assertions.assertEquals(0, playerRoom.getID());
			Assertions.assertEquals("a", playerRoom.getName());
			Assertions.assertEquals("description a", playerRoom.getDescription());
		}

		@Test
		public void nullRoomConnection() {
			rooms.clear();
			Room roomA = new Room(0, "a", "description a");
			Room roomB = null;
			roomA.getRoomConnections().put("north", new RoomConnection(roomB));
			rooms.put(0, roomA);
			rooms.put(1, roomB);
			player.setRoom(roomA);

			arguments.add("north");
			Assertions.assertEquals("Move failed, either player, or the room does not exist\n\n", gameEngine.inputCommand("move", arguments));
			Room playerRoom = player.getRoom();
			Assertions.assertEquals(0, playerRoom.getID());
			Assertions.assertEquals("a", playerRoom.getName());
			Assertions.assertEquals("description a", playerRoom.getDescription());
		}
	}

	@Nested
	class LookTests {
		@Test
		public void valid() {
			Assertions.assertEquals(
				"description a\n\n",
				gameEngine.inputCommand("look", arguments)
			);

			arguments.add("north");
			gameEngine.inputCommand("move", arguments);

			arguments.clear();
			Assertions.assertEquals(
				"description b\n\n",
				gameEngine.inputCommand("look", arguments)
			);
		}

		@Test
		public void emptyDescription() {
			player.getRoom().setDescription("");
			Assertions.assertEquals(
				"\n\n",
				gameEngine.inputCommand("look", arguments)
			);
		}

		@Test
		public void nullRoom() {
			player.setRoom(null);
			Assertions.assertThrows(
				NullPointerException.class,
				() -> gameEngine.inputCommand("look", arguments)
			);
		}
	}

	@Nested
	class InventoryTests {
		@Test
		public void empty() {
			Assertions.assertEquals(
				"""
					Your Inventory:
					Empty
					
					""",
				gameEngine.inputCommand("inventory", arguments)
			);
		}

		@Test
		public void singleItem() {
			Item item = new Item(0, "sword", "A sharp sword", 1, 1);
			player.getInventory().addItem(item);

			Assertions.assertEquals(
				"""
					Your Inventory:
					- 1 x sword
					
					""",
				gameEngine.inputCommand("inventory", arguments)
			);
		}

		@Test
		public void multipleItems() {
			Item item1 = new Item(0, "sword", "A sharp sword", 1, 1);
			player.getInventory().addItem(item1);

			Item item2 = new Item(1, "potion", "A healing potion", 1, 3);
			player.getInventory().addItem(item2);

			Assertions.assertEquals(
				"""
					Your Inventory:
					- 1 x sword
					- 3 x potions
					
					""",
				gameEngine.inputCommand("inventory", arguments)
			);
		}
	}

	@Nested
	class InspectItemTests {
		@Test
		public void valid() {
			Item item = new Item(0, "sword", "A sharp sword", 1);
			player.getInventory().addItem(item);

			arguments.add("sword");
			Assertions.assertEquals(
				item.getDescription() + "\n\n",
				gameEngine.inputCommand("inspect", arguments)
			);
		}

		@Test
		public void doNotHaveItem() {
			arguments.add("sword");
			Assertions.assertEquals(
				"You do not have a sword in your inventory.\n\n",
				gameEngine.inputCommand("inspect", arguments)
			);
		}

		@Test
		public void emptyName() {
			Item item = new Item(0, "", "A sharp sword", 1);
			player.getInventory().addItem(item);

			arguments.add("");
			Assertions.assertEquals(
				item.getDescription() + "\n\n",
				gameEngine.inputCommand("inspect", arguments)
			);
		}

		@Test
		public void whiteSpaceInName() {
			Item item = new Item(0, "sharp sword", "A sharp sword", 1);
			player.getInventory().addItem(item);

			arguments.add("sharp sword");
			Assertions.assertEquals(
				item.getDescription() + "\n\n",
				gameEngine.inputCommand("inspect", arguments)
			);
		}
	}

	@Nested
	class SearchTests {
		@Test
		public void emptyRoom() {
			Assertions.assertEquals(
				"""
					You found:
					Nothing!
					
					""",
				gameEngine.inputCommand("search", arguments)
			);
		}

		@Test
		public void oneItem() {
			Item item = new Item(0, "sword", "A sharp sword", 1);
			player.getRoom().getInventory().addItem(item);

			Assertions.assertEquals(
				"""
					You found:
					- 1 x sword
					
					""",
				gameEngine.inputCommand("search", arguments)
			);
		}

		@Test
		public void multipleItems() {
			Item item1 = new Item(0, "sword", "A sharp sword", 1);
			player.getRoom().getInventory().addItem(item1);

			Item item2 = new Item(1, "potion", "A healing potion", 1, 3);
			player.getRoom().getInventory().addItem(item2);

			Assertions.assertEquals(
				"""
					You found:
					- 1 x sword
					- 3 x potions
					
					""",
				gameEngine.inputCommand("search", arguments)
			);
		}
	}

	@Nested
	class PickupTests {
		@Test
		public void itemNotInRoom() {
			arguments.add("sword");
			Assertions.assertEquals(
				"This room does not have a sword.\n\n",
				gameEngine.inputCommand("pickup", arguments)
			);
		}

		@Test
		public void valid() {
			Room playerRoom = player.getRoom();

			Item item1 = new Item(0, "sword", "A sharp sword", 1);
			playerRoom.getInventory().addItem(item1);

			arguments.add("sword");
			Assertions.assertEquals(
				"You picked up 1 sword.\n\n",
				gameEngine.inputCommand("pickup", arguments)
			);
			Assertions.assertTrue(player.getInventory().getItems().containsKey(item1.getId()));
			Assertions.assertFalse(playerRoom.getInventory().getItems().containsKey(item1.getId()));
		}

		@Test
		public void multipleItems() {
			Room playerRoom = player.getRoom();

			Item item1 = new Item(0, "sword", "A sharp sword", 1);
			playerRoom.getInventory().addItem(item1);

			Item item2 = new Item(1, "potion", "A healing potion", 1, 3);
			playerRoom.getInventory().addItem(item2);

			arguments.add("potion");

			Assertions.assertEquals(
				"You picked up 3 potions.\n\n",
				gameEngine.inputCommand("pickup", arguments)
			);

			// Check player inventory
			Assertions.assertFalse(player.getInventory().getItems().containsKey(item1.getId()));
			Assertions.assertTrue(player.getInventory().getItems().containsKey(item2.getId()));
			Assertions.assertEquals(3, player.getInventory().getItems().get(item2.getId()).getAmount());

			// Check room inventory
			Assertions.assertTrue(playerRoom.getInventory().getItems().containsKey(item1.getId()));
			Assertions.assertEquals(1, playerRoom.getInventory().getItems().get(item1.getId()).getAmount());
			Assertions.assertFalse(playerRoom.getInventory().getItems().containsKey(item2.getId()));
		}

		@Test
		public void alreadyHasItem() {
			Room playerRoom = player.getRoom();

			Item item = new Item(0, "sword", "A sharp sword", 1);
			playerRoom.getInventory().addItem(new Item(0, "sword", "A sharp sword", 1));
			player.getInventory().addItem(item);

			arguments.add("sword");
			Assertions.assertEquals(
				"You picked up 1 sword.\n\n",
				gameEngine.inputCommand("pickup", arguments)
			);
			Assertions.assertTrue(player.getInventory().getItems().containsKey(item.getId()));
			Assertions.assertEquals(
				2,
				player.getInventory().getItems().get(item.getId()).getAmount()
			);
			Assertions.assertFalse(playerRoom.getInventory().getItems().containsKey(item.getId()));
		}

		@Test
		public void alreadyHasDifferentDescription() {
			Room playerRoom = player.getRoom();

			Item item = new Item(0, "sword", "A sword", 1);
			playerRoom.getInventory().addItem(new Item(0, "sword", "A sword sword", 1));
			player.getInventory().addItem(item);

			arguments.add("sword");
			Assertions.assertEquals(
				"You picked up 1 sword.\n\n",
				gameEngine.inputCommand("pickup", arguments)
			);
			Assertions.assertEquals(
				"A sword",
				player.getInventory().getItems().get(0).getDescription()
			);
		}

		@Test
		public void alreadyHasIntegerLimit() {
			Room playerRoom = player.getRoom();

			Item item = new Item(0, "sword", "A sharp sword", 1, Integer.MAX_VALUE);
			playerRoom.getInventory().addItem(new Item(0, "sword", "A sharp sword", 1));
			player.getInventory().addItem(item);

			arguments.add("sword");
			Assertions.assertEquals(
				"You picked up 1 sword.\n\n",
				gameEngine.inputCommand("pickup", arguments)
			);
			Assertions.assertTrue(player.getInventory().getItems().containsKey(item.getId()));
			Assertions.assertEquals(
				Integer.MIN_VALUE,
				player.getInventory().getItems().get(item.getId()).getAmount()
			);
			Assertions.assertFalse(playerRoom.getInventory().getItems().containsKey(item.getId()));
		}
	}

	@Nested
	class PickupAllTests {
		@Test
		public void valid() {
			Room playerRoom = player.getRoom();

			Item item1 = new Item(0, "sword", "A sharp sword", 1);
			Item item2 = new Item(1, "potion", "A healing potion", 1, 3);

			playerRoom.getInventory().addItem(item1);
			playerRoom.getInventory().addItem(item2);

			Assertions.assertEquals(
				"You picked up:\n1 x sword\n3 x potion\n\n",
				gameEngine.inputCommand("pickup-all", arguments)
			);

			Assertions.assertTrue(player.getInventory().getItems().containsKey(item1.getId()));
			Assertions.assertEquals(1, player.getInventory().getItems().get(item1.getId()).getAmount());
			Assertions.assertFalse(playerRoom.getInventory().getItems().containsKey(item1.getId()));

			Assertions.assertTrue(player.getInventory().getItems().containsKey(item2.getId()));
			Assertions.assertEquals(3, player.getInventory().getItems().get(item2.getId()).getAmount());
			Assertions.assertFalse(playerRoom.getInventory().getItems().containsKey(item2.getId()));
		}

		@Test
		public void haveItem() {
			Room playerRoom = player.getRoom();

			Item item1 = new Item(0, "sword", "A sharp sword", 1);
			Item item2 = new Item(1, "potion", "A healing potion", 1, 3);

			playerRoom.getInventory().addItem(item1);
			playerRoom.getInventory().addItem(item2);
			player.getInventory().addItem(new Item(0, "sword", "A sharp sword", 1));

			Assertions.assertEquals(
				"You picked up:\n1 x sword\n3 x potion\n\n",
				gameEngine.inputCommand("pickup-all", arguments)
			);

			Assertions.assertTrue(player.getInventory().getItems().containsKey(item1.getId()));
			Assertions.assertEquals(2, player.getInventory().getItems().get(item1.getId()).getAmount());
			Assertions.assertFalse(playerRoom.getInventory().getItems().containsKey(item1.getId()));

			Assertions.assertTrue(player.getInventory().getItems().containsKey(item2.getId()));
			Assertions.assertEquals(3, player.getInventory().getItems().get(item2.getId()).getAmount());
			Assertions.assertFalse(playerRoom.getInventory().getItems().containsKey(item2.getId()));
		}

		@Test
		public void empty() {
			Assertions.assertEquals(
				"You did not pick anything up from this room.\n\n",
				gameEngine.inputCommand("pickup-all", arguments)
			);
		}
	}

	@Nested
	class DropTests {
		@Test
		public void valid() {
			Room playerRoom = player.getRoom();

			Item item = new Item(0, "sword", "A sharp sword", 1);
			player.getInventory().addItem(item);

			arguments.add("sword");
			Assertions.assertEquals(
				"You dropped 1 sword.\n\n",
				gameEngine.inputCommand("drop", arguments)
			);
			Assertions.assertFalse(player.getInventory().getItems().containsKey(item.getId()));
			Assertions.assertTrue(playerRoom.getInventory().getItems().containsKey(item.getId()));
		}

		@Test
		public void notInInventory() {
			arguments.add("sword");
			Assertions.assertEquals(
				"You do not have a sword.\n\n",
				gameEngine.inputCommand("drop", arguments)
			);
		}

		@Test
		public void alreadyHasItem() {
			Room playerRoom = player.getRoom();

			Item item = new Item(0, "sword", "A sharp sword", 1);
			playerRoom.getInventory().addItem(new Item(0, "sword", "A sharp sword", 1));
			player.getInventory().addItem(item);

			arguments.add("sword");
			Assertions.assertEquals(
				"You dropped 1 sword.\n\n",
				gameEngine.inputCommand("drop", arguments)
			);
			Assertions.assertTrue(playerRoom.getInventory().getItems().containsKey(item.getId()));
			Assertions.assertEquals(
				2,
				playerRoom.getInventory().getItems().get(item.getId()).getAmount()
			);
			Assertions.assertFalse(player.getInventory().getItems().containsKey(item.getId()));
		}

		@Test
		public void alreadyHasItemIntegerLimit() {
			Room playerRoom = player.getRoom();

			Item item = new Item(0, "sword", "A sharp sword", 1);
			playerRoom.getInventory().addItem(new Item(0, "sword", "A sharp sword", 1, Integer.MAX_VALUE));
			player.getInventory().addItem(item);

			arguments.add("sword");
			Assertions.assertEquals(
				"You dropped 1 sword.\n\n",
				gameEngine.inputCommand("drop", arguments)
			);
			Assertions.assertTrue(playerRoom.getInventory().getItems().containsKey(item.getId()));
			Assertions.assertEquals(
				Integer.MIN_VALUE,
				playerRoom.getInventory().getItems().get(item.getId()).getAmount()
			);
			Assertions.assertFalse(player.getInventory().getItems().containsKey(item.getId()));
		}

		@Test
		public void multipleItems() {
			Room playerRoom = player.getRoom();

			Item item1 = new Item(0, "sword", "A sharp sword", 1);
			player.getInventory().addItem(item1);

			Item item2 = new Item(1, "potion", "A healing potion", 1, 3);
			player.getInventory().addItem(item2);

			arguments.clear();
			arguments.add("potion");

			Assertions.assertEquals(
				"You dropped 3 potions.\n\n",
				gameEngine.inputCommand("drop", arguments)
			);

			Assertions.assertFalse(player.getInventory().getItems().containsKey(item2.getId()));
			Assertions.assertTrue(playerRoom.getInventory().getItems().containsKey(item2.getId()));
			Assertions.assertEquals(
				3,
				playerRoom.getInventory().getItems().get(item2.getId()).getAmount()
			);

			Assertions.assertTrue(player.getInventory().getItems().containsKey(item1.getId()));
			Assertions.assertFalse(playerRoom.getInventory().getItems().containsKey(item1.getId()));
		}
	}

	@Nested
	class DropAllTests {
		@Test
		public void valid() {
			Room playerRoom = player.getRoom();

			Item item1 = new Item(0, "sword", "A sharp sword", 1);
			Item item2 = new Item(1, "potion", "A healing potion", 1, 3);

			player.getInventory().addItem(item1);
			player.getInventory().addItem(item2);

			Assertions.assertEquals(
				"You dropped:\n1 x sword\n3 x potion\n\n",
				gameEngine.inputCommand("drop-all", arguments)
			);

			Assertions.assertTrue(playerRoom.getInventory().getItems().containsKey(item1.getId()));
			Assertions.assertEquals(1, playerRoom.getInventory().getItems().get(item1.getId()).getAmount());
			Assertions.assertFalse(player.getInventory().getItems().containsKey(item1.getId()));

			Assertions.assertTrue(playerRoom.getInventory().getItems().containsKey(item2.getId()));
			Assertions.assertEquals(3, playerRoom.getInventory().getItems().get(item2.getId()).getAmount());
			Assertions.assertFalse(player.getInventory().getItems().containsKey(item2.getId()));
		}

		@Test
		public void haveOneItem() {
			Room playerRoom = player.getRoom();

			Item item1 = new Item(0, "sword", "A sharp sword", 1);
			Item item2 = new Item(1, "potion", "A healing potion", 1, 3);

			player.getInventory().addItem(item1);
			player.getInventory().addItem(item2);
			playerRoom.getInventory().addItem(new Item(0, "sword", "A sharp sword", 1));

			Assertions.assertEquals(
				"You dropped:\n1 x sword\n3 x potion\n\n",
				gameEngine.inputCommand("drop-all", arguments)
			);

			Assertions.assertTrue(playerRoom.getInventory().getItems().containsKey(item1.getId()));
			Assertions.assertEquals(2, playerRoom.getInventory().getItems().get(item1.getId()).getAmount());
			Assertions.assertFalse(player.getInventory().getItems().containsKey(item1.getId()));

			Assertions.assertTrue(playerRoom.getInventory().getItems().containsKey(item2.getId()));
			Assertions.assertEquals(3, playerRoom.getInventory().getItems().get(item2.getId()).getAmount());
			Assertions.assertFalse(player.getInventory().getItems().containsKey(item2.getId()));
		}

		@Test
		public void empty() {
			Assertions.assertEquals(
				"You do not have anything to drop.\n\n",
				gameEngine.inputCommand("drop-all", arguments)
			);
		}
	}

	@Test
	public void testWallet() {
		Assertions.assertEquals(
			"You have 0 coins.\n\n",
			gameEngine.inputCommand("wallet", arguments)
		);

		player.setCoins(1);
		Assertions.assertEquals(
			"You have 1 coin.\n\n",
			gameEngine.inputCommand("wallet", arguments)
		);

		player.setCoins(10);
		Assertions.assertEquals(
			"You have 10 coins.\n\n",
			gameEngine.inputCommand("wallet", arguments)
		);
	}

	@Nested
	class TalkToTests {
		private NPC npc;

		@BeforeEach
		public void setup() {
			npc = player.getRoom().getNpcs().get(0);
		}

		@Test
		public void defaultGreeting() {
			arguments.add("name");
			Assertions.assertEquals(
				"Hello adventurer, I am name.\n\n",
				gameEngine.inputCommand("talk-to", arguments)
			);

			Assertions.assertEquals(
				npc,
				player.getCurrentNPC()
			);
		}

		@Test
		public void customGreeting() {
			npc.setGreeting("Hi");

			arguments.add("name");
			Assertions.assertEquals(
				"Hi\n\n",
				gameEngine.inputCommand("talk-to", arguments)
			);

			Assertions.assertEquals(
				npc,
				player.getCurrentNPC()
			);
		}

		@Test
		public void noNPC() {
			arguments.add("abc");
			Assertions.assertEquals(
				"abc is not in this room.\n\n",
				gameEngine.inputCommand("talk-to", arguments)
			);

			Assertions.assertNull(player.getCurrentNPC());
		}
	}

	@Nested
	class LeaveTests {
		private NPC npc;

		@BeforeEach
		public void setup() {
			npc = player.getRoom().getNpcs().get(0);

			arguments.add("name");
			gameEngine.inputCommand("talk-to", arguments);

			arguments.clear();
		}

		@Test
		public void defaultBye() {
			Assertions.assertEquals(
				"Goodbye adventurer.\n\n",
				gameEngine.inputCommand("leave", arguments)
			);

			Assertions.assertNull(player.getCurrentNPC());
		}

		@Test
		public void customGreeting() {
			npc.setGoodbye("Bye");

			Assertions.assertEquals(
				"Bye\n\n",
				gameEngine.inputCommand("leave", arguments)
			);

			Assertions.assertNull(player.getCurrentNPC());
		}
	}

	@Nested
	class SearchShopTests {
		private NPC npc;

		@BeforeEach
		public void setup() {
			npc = player.getRoom().getNpcs().get(0);

			arguments.add("name");
			gameEngine.inputCommand("talk-to", arguments);

			arguments.clear();
		}

		@Test
		public void oneItem() {
			npc.getInventory().addItem(new Item(0, "a", "", 1));

			Assertions.assertEquals(
				"""
					I am selling:
					- 1 x a for 4 coins
					
					""",
				gameEngine.inputCommand("search-shop", arguments)
			);
		}

		@Test
		public void multipleItems() {
			npc.getInventory().addItem(new Item(0, "a", "", 1, 2));
			npc.getInventory().addItem(new Item(1, "b", "", 7));

			Assertions.assertEquals(
				"""
					I am selling:
					- 2 x a for 8 coins
					- 1 x b for 28 coins
					
					""",
				gameEngine.inputCommand("search-shop", arguments)
			);
		}

		@Test
		public void empty() {
			Assertions.assertEquals(
				"I am not selling anything.\n\n",
				gameEngine.inputCommand("search-shop", arguments)
			);
		}
	}

	@Nested
	class buyItemTests {
		private NPC npc;

		@BeforeEach
		public void setup() {
			npc = player.getRoom().getNpcs().get(0);
			npc.getInventory().addItem(new Item(0, "a", "", 3));
			npc.getInventory().addItem(new Item(1, "b", "", 2, 2));
			player.setCoins(100);

			arguments.add("name");
			gameEngine.inputCommand("talk-to", arguments);

			arguments.clear();
		}

		@Test
		public void oneItem() {
			arguments.add("a");
			arguments.add("1");

			Assertions.assertEquals(
				"You bought 1 x a, -12 coins.\n\n",
				gameEngine.inputCommand("buy", arguments)
			);

			Assertions.assertEquals(
				100 - 12,
				player.getCoins()
			);
			Assertions.assertTrue(player.getInventory().getItems().containsKey(0));
			Assertions.assertEquals(
				1,
				player.getInventory().getItems().get(0).getAmount()
			);
			Assertions.assertTrue(npc.getInventory().getItems().containsKey(0));
			Assertions.assertEquals(
				1,
				npc.getInventory().getItems().get(0).getAmount()
			);
		}

		@Test
		public void oneItemMultipleTimes() {
			arguments.add("a");
			arguments.add("1");

			Assertions.assertEquals(
				"You bought 1 x a, -12 coins.\n\n",
				gameEngine.inputCommand("buy", arguments)
			);

			Assertions.assertEquals(
				100 - 12,
				player.getCoins()
			);
			Assertions.assertTrue(player.getInventory().getItems().containsKey(0));
			Assertions.assertEquals(
				1,
				player.getInventory().getItems().get(0).getAmount()
			);
			Assertions.assertTrue(npc.getInventory().getItems().containsKey(0));
			Assertions.assertEquals(
				1,
				npc.getInventory().getItems().get(0).getAmount()
			);

			Assertions.assertEquals(
				"You bought 1 x a, -12 coins.\n\n",
				gameEngine.inputCommand("buy", arguments)
			);

			Assertions.assertEquals(
				100 - 24,
				player.getCoins()
			);
			Assertions.assertTrue(player.getInventory().getItems().containsKey(0));
			Assertions.assertEquals(
				2,
				player.getInventory().getItems().get(0).getAmount()
			);
			Assertions.assertTrue(npc.getInventory().getItems().containsKey(0));
			Assertions.assertEquals(
				1,
				npc.getInventory().getItems().get(0).getAmount()
			);
		}

		@Test
		public void multipleItems() {
			arguments.add("b");
			arguments.add("2");

			Assertions.assertEquals(
				"You bought 2 x b, -16 coins.\n\n",
				gameEngine.inputCommand("buy", arguments)
			);

			Assertions.assertEquals(
				100 - 16,
				player.getCoins()
			);
		}

		@Test
		public void notEnoughCoins() {
			player.setCoins(100);

			arguments.add("a");
			arguments.add("10");

			Assertions.assertEquals(
				"You are too poor to buy 10 x a.\n\n",
				gameEngine.inputCommand("buy", arguments)
			);

			Assertions.assertEquals(
				100,
				player.getCoins()
			);
		}

		@Test
		public void notSelling() {
			arguments.add("abc");
			arguments.add("1");

			Assertions.assertEquals(
				"I am not selling any abcs.\n\n",
				gameEngine.inputCommand("buy", arguments)
			);
		}
	}

	@Nested
	class SellItemTests {

		@BeforeEach
		public void setup() {
			player.setCoins(100);
			player.getInventory().addItem(new Item(0, "a", "", 3));
			player.getInventory().addItem(new Item(1, "b", "", 2, 2));

			arguments.add("name");
			gameEngine.inputCommand("talk-to", arguments);

			arguments.clear();
		}

		@Test
		public void oneItem() {
			arguments.add("a");
			arguments.add("1");

			Assertions.assertEquals(
				"You sold 1 x a, +3 coins.\n\n",
				gameEngine.inputCommand("sell", arguments)
			);

			Assertions.assertEquals(
				100 + 3,
				player.getCoins()
			);
			Assertions.assertFalse(player.getInventory().getItems().containsKey(0));
		}

		@Test
		public void oneItemMultipleTimes() {
			player.getInventory().getItems().get(0).setAmount(2);

			arguments.add("a");
			arguments.add("1");

			// Sell 1
			Assertions.assertEquals(
				"You sold 1 x a, +3 coins.\n\n",
				gameEngine.inputCommand("sell", arguments)
			);
			Assertions.assertTrue(player.getInventory().getItems().containsKey(0));
			Assertions.assertEquals(
				1,
				player.getInventory().getItems().get(0).getAmount()
			);

			// Sell 2
			Assertions.assertEquals(
				"You sold 1 x a, +3 coins.\n\n",
				gameEngine.inputCommand("sell", arguments)
			);
			Assertions.assertEquals(
				100 + 6,
				player.getCoins()
			);
			Assertions.assertFalse(player.getInventory().getItems().containsKey(0));
		}

		@Test
		public void multipleItems() {
			arguments.add("b");
			arguments.add("2");

			Assertions.assertEquals(
				"You sold 2 x b, +4 coins.\n\n",
				gameEngine.inputCommand("sell", arguments)
			);

			Assertions.assertEquals(
				100 + 4,
				player.getCoins()
			);
			Assertions.assertFalse(player.getInventory().getItems().containsKey(1));
		}

		@Test
		public void notEnoughItems() {
			arguments.add("a");
			arguments.add("5");

			Assertions.assertEquals(
				"You do not have 5 of a.\n\n",
				gameEngine.inputCommand("sell", arguments)
			);

			Assertions.assertEquals(
				100,
				player.getCoins()
			);
			Assertions.assertTrue(player.getInventory().getItems().containsKey(0));
			Assertions.assertEquals(
				1,
				player.getInventory().getItems().get(0).getAmount()
			);
		}

		@Test
		public void notHaveItem() {
			arguments.add("abc");
			arguments.add("1");

			Assertions.assertEquals(
				"You do not have any abc to sell.\n\n",
				gameEngine.inputCommand("sell", arguments)
			);
		}
	}

	@Nested
	class SellAllItemTests {

		@BeforeEach
		public void setup() {
			player.setCoins(100);
			player.getInventory().addItem(new Item(0, "a", "", 3));
			player.getInventory().addItem(new Item(1, "b", "", 2, 2));

			arguments.add("name");
			gameEngine.inputCommand("talk-to", arguments);

			arguments.clear();
		}

		@Test
		public void valid() {
			arguments.add("a");
			Assertions.assertEquals(
				"You sold 1 x a, +3 coins.\n\n",
				gameEngine.inputCommand("sell-all", arguments)
			);

			Assertions.assertEquals(
				100 + 3,
				player.getCoins()
			);
			Assertions.assertFalse(player.getInventory().getItems().containsKey(0));


			arguments.clear();
			arguments.add("b");
			Assertions.assertEquals(
				"You sold 2 x b, +4 coins.\n\n",
				gameEngine.inputCommand("sell-all", arguments)
			);

			Assertions.assertEquals(
				100 + 3 + 4,
				player.getCoins()
			);
			Assertions.assertFalse(player.getInventory().getItems().containsKey(1));
		}

		@Test
		public void notHaveItem() {
			arguments.add("abc");

			Assertions.assertEquals(
				"You do not have any abc to sell.\n\n",
				gameEngine.inputCommand("sell-all", arguments)
			);
		}
	}

	// Unsure how to make restart able to work with any starting data.
	@Test
	public void testRestart() {
		Room playerRoom = player.getRoom();

		// Add items to inventory and room to test restart properly
		Item item = new Item(0, "sword", "A sharp sword", 1);
		player.getInventory().addItem(item);
		playerRoom.getInventory().addItem(item);

		// Move to different room
		arguments.add("north");
		gameEngine.inputCommand("move", arguments);

		// Test restart
		arguments.clear();
		Assertions.assertEquals(
			"Restarted game.\n\n",
			gameEngine.inputCommand("restart", arguments)
		);

		// Verify restart worked
		Assertions.assertEquals(0, playerRoom.getID());
		Assertions.assertEquals("a", playerRoom.getName());
		Assertions.assertEquals("description a", playerRoom.getDescription());
		Assertions.assertTrue(player.getInventory().getItems().isEmpty());
		Assertions.assertEquals(0, player.getCoins());
		Assertions.assertTrue(player.getArmor().isEmpty());
		Assertions.assertEquals(PlayerState.EXPLORING, player.getState());
	}

	@Test
	public void testHelp() {
		String output = gameEngine.inputCommand("help", arguments);

		Assertions.assertTrue(
			output.contains(Command.MOVE.getFormat())
		);

		StringBuilder examples = new StringBuilder("Examples:");
		for (String example : Command.MOVE.getExamples()) {
			examples
				.append("\n  - \"")
				.append(example)
				.append("\"");
		}
		Assertions.assertTrue(
			output.contains(examples.toString())
		);

		Assertions.assertTrue(
			output.contains(Command.DROP_ALL.getFormat())
		);
		Assertions.assertTrue(
			output.contains(Command.HELP.getFormat())
		);
	}

	@Test
	public void testInvalidCommandFormat() {
		// Test all commands with wrong number of arguments

		// Move command with too many arguments
		arguments.add("north");
		arguments.add("south");
		Assertions.assertTrue(
			gameEngine.inputCommand("move", arguments).startsWith(
				"Invalid move command. Must be in the format:\n\"move <Direction>\"\n"
			)
		);

		// Move command with no arguments
		arguments.clear();
		Assertions.assertTrue(
			gameEngine.inputCommand("move", arguments).startsWith(
				"Invalid move command. Must be in the format:\n\"move <Direction>\"\n"
			)
		);
	}

	@Test
	public void testDisallowedPlayerState() {
		Assertions.assertEquals(
			"You are not allowed to use sell while " + player.getState().getName() + ".\n\n",
			gameEngine.inputCommand("sell", arguments)
		);

		player.setState(PlayerState.TALKING_TO_NPC);
		Assertions.assertEquals(
			"You are not allowed to use move while " + player.getState().getName() + ".\n\n",
			gameEngine.inputCommand("move", arguments)
		);
	}

	@Test
	public void testInvalidCommand() {
		// Test unrecognized command
		Assertions.assertEquals(
			"Sorry, command not recognized.\n\n",
			gameEngine.inputCommand("invalid", arguments)
		);
		Assertions.assertEquals(
			"Sorry, command not recognized.\n\n",
			gameEngine.inputCommand("xyz", arguments)
		);
	}
}

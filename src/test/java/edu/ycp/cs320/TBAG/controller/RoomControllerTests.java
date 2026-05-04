package edu.ycp.cs320.TBAG.controller;
import edu.ycp.cs320.TBAG.model.NPC;
import edu.ycp.cs320.TBAG.model.Room;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class RoomControllerTests {

	private RoomController controller;
	private Room room1;
	private Room room2;

	@BeforeEach
	public void setUp() {
		HashMap<Integer, Room> rooms = new HashMap<>();

		room1 = new Room(1, "Room 1", "First room");
		room2 = new Room(2, "Room 2", "Second room");

		rooms.put(1, room1);
		rooms.put(2, room2);

		controller = new RoomController();
	}

	// addRoomConnection + isValidDirection
	@Test
	public void testAddRoomConnection() {
		controller.addRoomConnection(room1, room2, "north");

		Assertions.assertTrue(room1.getRoomConnections().containsKey("north"));
		Assertions.assertEquals(room2, room1.getRoomConnections().get("north"));
	}

	@Test
	public void testIsValidDirectionTrue() {
		controller.addRoomConnection(room1, room2, "north");

		Assertions.assertTrue(controller.isValidDirection(room1, "north"));
	}

	@Test
	public void testIsValidDirectionFalse() {
		Assertions.assertFalse(controller.isValidDirection(room1, "south"));
	}

	// getNPCByName (case-sensitive)
	@Test
	public void testGetNPCByNameFound() {
		NPC npc = new NPC(1, "Bob");
		room1.getNpcs().put(1, npc);

		NPC result = controller.getNPCByName(room1, "Bob");

		Assertions.assertNotNull(result);
		Assertions.assertEquals("Bob", result.getName());
	}

	@Test
	public void testGetNPCByNameNotFound() {
		NPC npc = new NPC(1, "Bob");
		room1.getNpcs().put(1, npc);

		NPC result = controller.getNPCByName(room1, "Alice");

		Assertions.assertNull(result);
	}

	// getNPCByNameCaseInsensitive
	@Test
	public void testGetNPCByNameCaseInsensitiveFound() {
		NPC npc = new NPC(1, "Bob");
		room1.getNpcs().put(1, npc);

		NPC result = controller.getNPCByNameCaseInsensitive(room1, "bob");

		Assertions.assertNotNull(result);
		Assertions.assertEquals("Bob", result.getName());
	}

	@Test
	public void testGetNPCByNameCaseInsensitiveDifferentCase() {
		NPC npc = new NPC(1, "Bob");
		room1.getNpcs().put(1, npc);

		NPC result = controller.getNPCByNameCaseInsensitive(room1, "BoB");

		Assertions.assertNotNull(result);
	}

	@Test
	public void testGetNPCByNameCaseInsensitiveNotFound() {
		NPC npc = new NPC(1, "Bob");
		room1.getNpcs().put(1, npc);

		NPC result = controller.getNPCByNameCaseInsensitive(room1, "Alice");

		Assertions.assertNull(result);
	}
}

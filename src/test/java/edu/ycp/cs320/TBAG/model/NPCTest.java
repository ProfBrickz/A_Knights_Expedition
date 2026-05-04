package edu.ycp.cs320.TBAG.model;

import edu.ycp.cs320.TBAG.model.NPC;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NPCTest {

	@Test
	public void testFullConstructor() {
		NPC npc = new NPC(1, "Bob", 150, 120, "Hi!", "Bye!");

		Assertions.assertEquals(1, npc.getId());
		Assertions.assertEquals("Bob", npc.getName());
		Assertions.assertEquals(150, npc.getMaxHealth());
		Assertions.assertEquals(120, npc.getHealth());
		Assertions.assertEquals("Hi!", npc.getGreeting());
		Assertions.assertEquals("Bye!", npc.getGoodbye());
	}

	@Test
	public void testDefaultConstructor() {
		NPC npc = new NPC(2, "Alice");

		Assertions.assertEquals(2, npc.getId());
		Assertions.assertEquals("Alice", npc.getName());

		// Default health values
		Assertions.assertEquals(100, npc.getMaxHealth());
		Assertions.assertEquals(100, npc.getHealth());

		// Default dialogue
		Assertions.assertEquals("Hello adventurer, I am Alice.", npc.getGreeting());
		Assertions.assertEquals("Goodbye adventurer.", npc.getGoodbye());
	}

	@Test
	public void testSetters() {
		NPC npc = new NPC(3, "Test");

		npc.setName("NewName");
		npc.setGreeting("Welcome!");
		npc.setGoodbye("See ya!");

		Assertions.assertEquals("NewName", npc.getName());
		Assertions.assertEquals("Welcome!", npc.getGreeting());
		Assertions.assertEquals("See ya!", npc.getGoodbye());
	}

	@Test
	public void testCopy() {
		NPC original = new NPC(4, "CopyNPC", 200, 150, "Hey!", "Later!");

		NPC copy = original.copy();

		// Ensure values are the same
		Assertions.assertEquals(original.getId(), copy.getId());
		Assertions.assertEquals(original.getName(), copy.getName());
		Assertions.assertEquals(original.getMaxHealth(), copy.getMaxHealth());
		Assertions.assertEquals(original.getHealth(), copy.getHealth());
		Assertions.assertEquals(original.getGreeting(), copy.getGreeting());
		Assertions.assertEquals(original.getGoodbye(), copy.getGoodbye());

		// Ensure it's a different object (important!)
		Assertions.assertNotSame(original, copy);
	}

	@Test
	public void testCopyIndependence() {
		NPC original = new NPC(5, "Original");
		NPC copy = original.copy();

		// Modify copy
		copy.setName("Changed");
		copy.setGreeting("Different");

		// Original should NOT change
		Assertions.assertNotEquals(original.getName(), copy.getName());
		Assertions.assertNotEquals(original.getGreeting(), copy.getGreeting());
	}
}
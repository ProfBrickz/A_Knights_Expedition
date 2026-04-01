package edu.ycp.cs320.TBAG.persist;

import edu.ycp.cs320.TBAG.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class InitialData {
	// Might not need maps for all of these, or might need maps for other things but this is what they would look like
	private HashMap<String, Integer> roomIds = new HashMap<String, Integer>();
	private HashMap<String, Integer> itemIds = new HashMap<String, Integer>();
	private HashMap<String, Integer> npcIds = new HashMap<String, Integer>();
	private HashMap<String, Integer> enemyIds = new HashMap<String, Integer>();

	/**
	 * will throw IllegalStateException when there are multiple players
	 */
	public static ArrayList<Player> getPlayers() throws IOException {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public static ArrayList<Room> getRooms() throws IOException {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public static ArrayList<Item> getItems() throws IOException {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public static ArrayList<NPC> getNPCs() throws IOException {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public static ArrayList<WeaponAbility> getWeaponAbilities() throws IOException {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public static ArrayList<Enemy> getEnemies() throws IOException {
		throw new UnsupportedOperationException("TODO - implement");
	}
}

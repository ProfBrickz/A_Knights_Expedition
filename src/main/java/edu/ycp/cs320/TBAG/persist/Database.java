package edu.ycp.cs320.TBAG.persist;

import edu.ycp.cs320.TBAG.controller.Command;
import edu.ycp.cs320.TBAG.model.*;

import java.util.ArrayList;
import java.util.HashMap;

public interface Database {
	public static final Integer MAX_HISTORY_SIZE = 10;

	// General purpose methods
	void loadInitialData();

	Boolean reset();

	// Dialog methods
	ArrayList<String> getDialog();

	void addDialog(String text);

	void clearDialog();

	/// Up to 15 commands long
	ArrayList<String> getCommandHistory();

	// Will also remove the oldest command
	void addCommandToHistory(String command);

	// Player-related methods

	/**
	 * Returns the player, without items or a room
	 */
	Player getPlayer();

	void setPlayerRoom(Integer roomId);

	void setPlayerCoins(Integer coins);

	void setPlayerState(PlayerState playerState);

	void setConfirming(Boolean confirming);

	void setPlayerNPC(NPC npc);

	void setLastCommand(Command command);

	void addItemToPlayer(Item item);

	void removeItemFromPlayer(Item item);


	// Room-related methods
	Room getRoomById(Integer id);

	/**
	 * Returns a map between a room's directions and connections
	 */
	HashMap<String, RoomConnection> getConnectionsForRoom(Room room);

	void addItemToRoom(Room room, Item item);

	void removeItemFromRoom(Room room, Item item);


	// Item-related methods
	HashMap<Integer, Item> getItemsForPlayer();

	HashMap<Integer, Item> getItemsForRoom(Room room);

	HashMap<Integer, Item> getItemsForNPC(NPC npc);

	HashMap<Integer, Item> getItemsForEnemy(Enemy enemy);


	// NPC-related methods
	HashMap<Integer, NPC> getNPCsForRoom(Room room);


	// Enemy-related methods
	HashMap<Integer, Enemy> getEnemiesForRoom(Room room);

	void addItemToEnemy(Enemy enemy, Item item);

	void removeItemFromEnemy(Enemy enemy, Item item);


	// WeaponAbility-related methods
	HashMap<Integer, WeaponAbility> getAbilitiesForWeapon(Weapon weapon);
}

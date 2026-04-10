package edu.ycp.cs320.TBAG.persist;

import edu.ycp.cs320.TBAG.model.*;

import java.sql.ResultSet;
import java.util.HashMap;

public interface Database {
	// General purpose methods
	public void loadInitialData();


	// Player-related methods

	/**
	 * Returns the player, without items or a room
	 */
	public Player getPlayer();

	public void setPlayerRoom(Integer roomId);

	public void setPlayerCoins(Integer coins);

	public void setPlayerState(PlayerState playerState);

	public void setPlayerNPC(NPC npc);

	public void addItemToPlayer(Item item);

	public void removeItemFromPlayer(Item item);


	// Room-related methods
	public Room getRoomById(Integer id);

	/**
	 * Returns a map between a room's directions and connections
	 */
	public HashMap<String, RoomConnection> getConnectionsForRoom(Room room);

	public void addItemToRoom(Room room, Item item);

	public void removeItemFromRoom(Room room, Item item);


	// Item-related methods, all will use getItemsFromResultSet after the query
	public HashMap<Integer, Item> getItemsFromResultSet(ResultSet resultSet);

	public HashMap<Integer, Item> getItemsForPlayer(Player player);

	public HashMap<Integer, Item> getItemsForRoom(Room room);

	public HashMap<Integer, Item> getItemsForNPC(NPC npc);

	public HashMap<Integer, Item> getItemsForEnemy(Enemy enemy);


	// NPC-related methods
	public HashMap<Integer, NPC> getNPCsForRoom(Room room);


	// Enemy-related methods
	public HashMap<Integer, Enemy> getEnemiesForRoom(Room room);

	public void addItemToEnemy(Enemy enemy, Item item);

	public void removeItemFromEnemy(Enemy enemy, Item item);


	// WeaponAbility-related methods
	public HashMap<Integer, WeaponAbility> getAbilitiesForWeapon(Weapon weapon);
}

package edu.ycp.cs320.TBAG.persist;

import edu.ycp.cs320.TBAG.model.*;

import java.sql.ResultSet;
import java.util.HashMap;

public interface Database {
	public void loadInitialData();

	// Player-related methods
	public Player getPlayer();

	public void setPlayerRoom(Integer roomId);

	public void setPlayerCoins(Integer coins);

	public void setPlayerState(PlayerState playerState);

	public void addItemToPlayer(Item item);

	public void removeItemFromPlayer(Item item);

	public void setPlayerNPC(NPC npc);


	// Room-related methods
	public Room getRoomById(Integer id);

	public HashMap<String, RoomConnection> getConnectionsForRoom(Room room);

	public void addItemToRoom(Room room, Item item);

	public void removeItemFromRoom(Room room, Item item);


	// Item-related methods, all will use getItemsFromResultSet after the query
	public HashMap<Integer, Item> getItemsFromResultSet(ResultSet resultSet);

	public HashMap<Integer, Item> getItemsForPlayer();

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

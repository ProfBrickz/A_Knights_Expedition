package edu.ycp.cs320.TBAG.persist;

import edu.ycp.cs320.TBAG.controller.Command;
import edu.ycp.cs320.TBAG.model.*;

import java.util.ArrayList;
import java.util.HashMap;

public interface Database {
	Integer MAX_HISTORY_SIZE = 10;

	// General purpose methods
	void loadInitialData();

	Boolean reset();


	// Dialog
	ArrayList<String> getDialog();

	void addDialog(String text);

	void clearDialog();


	// Command history
	ArrayList<String> getCommandHistory();

	void addCommandToHistory(String command);


	// Items
	HashMap<Integer, WeaponAbility> getAbilitiesForWeapon(Weapon weapon);
	

	// NPCs
	HashMap<Integer, Item> getItemsForNPC(NPC npc);


	// Enemies
	HashMap<Integer, Item> getItemsForEnemy(Enemy enemy);

	void addItemToEnemy(Enemy enemy, Item item);

	void removeItemFromEnemy(Enemy enemy, Item item);


	// Rooms
	Room getRoomById(Integer id);

	HashMap<String, RoomConnection> getConnectionsForRoom(Room room);

	HashMap<Integer, NPC> getNPCsForRoom(Room room);

	HashMap<Integer, Enemy> getEnemiesForRoom(Room room);

	HashMap<Integer, Item> getItemsForRoom(Room room);

	void addItemToRoom(Room room, Item item);

	void removeItemFromRoom(Room room, Item item);


	// Player
	Player getPlayer();

	NPC getNpcForPlayer();

	HashMap<Integer, Item> getItemsForPlayer();

	void addItemToPlayer(Item item);

	void removeItemFromPlayer(Item item);

	void setPlayerRoom(Integer roomId);

	void setPlayerCoins(Integer coins);

	void setPlayerState(PlayerState playerState);

	void setConfirming(Boolean confirming);

	void setPlayerNPC(NPC npc);

	void setLastCommand(Command command);
}

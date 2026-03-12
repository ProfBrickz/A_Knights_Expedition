package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.Player;
import edu.ycp.cs320.TBAG.model.Room;
import edu.ycp.cs320.TBAG.model.RoomConnection;

public class PlayerController {
	private Player player;

	public PlayerController(Player player) {
		this.player = player;
	}

	public Boolean move(String direction) {
		if (player == null || player.getRoom() == null) {
			return false;
		}

		Room currentRoom = player.getRoom();
		RoomConnection connection = currentRoom.getRoomConnections().get(direction);

		if (connection != null) {
			Room nextRoom = connection.getRoom();
			player.setRoom(nextRoom);
			return true;
		}

		return false;
	}
}

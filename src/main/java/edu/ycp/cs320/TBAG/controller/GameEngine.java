package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.CommandType;
import edu.ycp.cs320.TBAG.model.Player;
import edu.ycp.cs320.TBAG.model.Room;

import java.util.ArrayList;

/**
 * Controller for the TBAG game.
 */
public class GameEngine {
	private Player player;
	private ArrayList<Room> rooms;

	public GameEngine(Player player){
		this.player = player;
	}

	// Check if a command is valid, return which command it is, or null if it is not valid
	public CommandType parseCommand(String command) {
		return null;
	}

	// Attempt to move player
	public Boolean movePlayer(String direction)	{
		return false;
	}
}

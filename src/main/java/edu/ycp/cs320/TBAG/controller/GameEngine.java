package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.Player;
import edu.ycp.cs320.TBAG.model.Room;

import java.util.ArrayList;

/**
 * Controller for the TBAG game.
 */
public class GameEngine {
	private Player player;
	private ArrayList<Room> rooms;

	public GameEngine(Player player, ArrayList<Room> rooms) {
		this.player = player;
		this.rooms = rooms;
	}

	public Player getPlayer() {
		return player;
	}

	public ArrayList<Room> getRooms() {
		return rooms;
	}

	public String inputCommand(String command, ArrayList<String> arguments) {
		if (command.equals(Command.MOVE.getCommand())) {
			return this.move(arguments);
		} else {
			return "Sorry, command not recognized.\n";
		}
	}

	private String move(ArrayList<String> arguments) {
		if (arguments.size() != Command.MOVE.getArguments().size()) {
			return "Invalid move command\nMust be in the format:\n" + Command.MOVE.getFormat() + "\n";
		}

		final Boolean success = false;
		//final Boolean success = this.player.move(arguments[0]);
		if (!success) {
			return "Invalid direction for this room\n";
		}

		return "new room\n";
		//return this.player.getRoom().description;
	}
}

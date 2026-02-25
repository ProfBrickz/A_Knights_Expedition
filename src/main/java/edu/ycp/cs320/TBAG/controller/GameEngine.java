package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.Player;

/**
 * Controller for the TBAG game.
 */
public class GameEngine {

	private Player player;

	public GameEngine(){
	}

	public void setPlayer(Player player){
		this.player = player;
	}

	// Attempt to move player
	public Boolean movePlayer(String direction) {
		return false;
	}

}

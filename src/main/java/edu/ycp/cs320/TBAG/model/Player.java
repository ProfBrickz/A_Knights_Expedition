package edu.ycp.cs320.TBAG.model;

// model class for Player
// only the controller should be allowed to call the set methods
public class Player {
	private Integer roomID;
	
	public Player() {
		this.roomID = 1;
	}
	
	public Player(Integer roomID) {
		this.roomID = roomID;
	}
	
	public Integer getRoomID() {
		return roomID;
	}
	
	public void setRoomID(Integer roomID) {
		this.roomID = roomID;
	}
}

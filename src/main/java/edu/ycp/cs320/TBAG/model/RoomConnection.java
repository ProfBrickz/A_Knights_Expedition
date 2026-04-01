package edu.ycp.cs320.TBAG.model;


//replace id with room object

public class RoomConnection {

	private Room room;
	private String description;
	private boolean traveled;


	public RoomConnection(Room room, String description) {
		this.room = room;
		this.description = description;
		traveled = false;
	}


	public RoomConnection(Room room) {
		this.room = room;
		this.description = "NULL";
		traveled = false;
	}

	public Room getRoom() {
		return room;
	}


	public String getDescription() {
		return description;
	}


	public boolean getTraveled() {
		return traveled;
	}

	public void setTraveled(boolean traveled) {
		this.traveled = traveled;
	}

	public void setRoom(Room room) {
		this.room = room;
	}


	public void setDescription(String description) {
		this.description = description;
	}
}






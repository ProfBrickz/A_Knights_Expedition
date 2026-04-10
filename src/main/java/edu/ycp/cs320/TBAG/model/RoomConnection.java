package edu.ycp.cs320.TBAG.model;


//replace id with room object

public class RoomConnection {
	private Room room;
	private String description;
	private Boolean traveled;

	public RoomConnection(Room room, String description, Boolean traveled) {
		this.room = room;
		this.description = description;
		this.traveled = traveled;
	}

	public RoomConnection(Room room, String description) {
		this(room, description, null);
	}

	public RoomConnection(Room room) {
		this(room, null);
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






package edu.ycp.cs320.TBAG.model;

public enum CommandType {
	MOVE("move");

	private final String command;

	private CommandType(String command) {
		this.command = command;
	}

	public String getCommand() {
		return command;
	}
}

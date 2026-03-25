package edu.ycp.cs320.TBAG.controller;

import java.util.List;

public enum Command {
	MOVE("move", List.of("direction")),
	LOOK("look", List.of()),
	INVENTORY("inventory", List.of()),
	INSPECT_ITEM("inspect-item", List.of("item")),
	SEARCH("search", List.of()),
	PICKUP("pickup", List.of("item name")),
	DROP("drop", List.of("item name")),
	RESTART("restart", List.of());

	private final String command;
	private final List<String> arguments;

	private Command(String command, List<String> arguments) {
		this.command = command;
		this.arguments = arguments;
	}

	public String getCommand() {
		return command;
	}

	public List<String> getArguments() {
		return arguments;
	}

	public String getFormat() {
		String format = "\"" + this.command;

		for (String argument : this.arguments) {
			format += " <" + argument + ">";
		}
		format += "\"";

		return format;
	}
}

package edu.ycp.cs320.TBAG.controller;

import java.util.List;

public enum Command {
	MOVE("move", List.of("direction")),
	LOOK("look", List.of()),
	INVENTORY("inventory", List.of()),
	INSPECT_ITEM("inspect", List.of("item name")),
	SEARCH("search", List.of()),
	PICKUP("pickup", List.of("item name")),
	PICKUP_ALL("pickup-all", List.of()),
	DROP("drop", List.of("item name")),
	DROP_ALL("drop-all", List.of()),
	RESTART("restart", List.of()),
	HELP("help", List.of());

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

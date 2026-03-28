package edu.ycp.cs320.TBAG.controller;

import java.util.List;

public enum Command {
	MOVE(
		"move",
		List.of("direction"),
		List.of("move north")
	),
	LOOK(
		"look",
		List.of(),
		List.of()
	),
	INVENTORY(
		"inventory",
		List.of(),
		List.of()
	),
	INSPECT_ITEM(
		"inspect",
		List.of("item name"),
		List.of()
	),
	SEARCH(
		"search",
		List.of(),
		List.of()
	),
	PICKUP(
		"pickup",
		List.of("item name"),
		List.of()
	),
	PICKUP_ALL(
		"pickup-all",
		List.of(),
		List.of()
	),
	DROP(

		"drop",
		List.of("item name"),
		List.of()
	),
	DROP_ALL(
		"drop-all",
		List.of(),
		List.of()
	),
	RESTART(
		"restart",
		List.of(),
		List.of()
	),
	HELP(
		"help",
		List.of(),
		List.of()
	);

	private final String command;
	private final List<String> arguments;
	private final List<String> examples;

	private Command(String command, List<String> arguments, List<String> examples) {
		this.command = command;
		this.arguments = arguments;
		this.examples = examples;
	}

	public String getCommand() {
		return command;
	}

	public List<String> getArguments() {
		return arguments;
	}

	public List<String> getExamples() {
		return examples;
	}

	public String getFormat() {
		StringBuilder format = new StringBuilder("\"" + this.command);

		for (String argument : this.arguments) {
			format.append(" <").append(argument).append(">");
		}
		format.append("\"");

		if (!this.examples.isEmpty()) {
			format.append("\n  Examples:");

			for (String example : examples) {
				format
					.append("\n  - \"")
					.append(example)
					.append("\"");
			}
		}

		return format.toString();
	}
}

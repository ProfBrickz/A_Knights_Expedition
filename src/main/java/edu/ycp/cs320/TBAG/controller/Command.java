package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.PlayerState;

import java.util.List;

public enum Command {
	MOVE(
		"move",
		"moves to room",
		List.of("Direction"),
		List.of(PlayerState.EXPLORING),
		List.of("move north")
	),
	LOOK(
		"look",
		"",
		List.of(),
		List.of(PlayerState.EXPLORING),
		List.of()
	),
	INVENTORY(
		"inventory",
		"",
		List.of(),
		List.of(PlayerState.EXPLORING),
		List.of()
	),
	INSPECT_ITEM(
		"inspect",
		"",
		List.of("Item name"),
		List.of(PlayerState.EXPLORING),
		List.of()
	),
	SEARCH(
		"search",
		"",
		List.of(),
		List.of(PlayerState.EXPLORING),
		List.of()
	),
	PICKUP(
		"pickup",
		"",
		List.of("item name"),
		List.of(PlayerState.EXPLORING),
		List.of()
	),
	PICKUP_ALL(
		"pickup-all",
		"",
		List.of(),
		List.of(PlayerState.EXPLORING),
		List.of()
	),
	DROP(
		"drop",
		"",
		List.of("Item name"),
		List.of(PlayerState.EXPLORING),
		List.of()
	),
	DROP_ALL(
		"drop-all",
		"",
		List.of(),
		List.of(PlayerState.EXPLORING),
		List.of()
	),
	WALLET(
		"wallet",
		"",
		List.of(),
		List.of(PlayerState.EXPLORING, PlayerState.TALKING_TO_NPC),
		List.of()
	),
	TALK_TO(
		"talk-to",
		"",
		List.of("NPC name"),
		List.of(PlayerState.EXPLORING),
		List.of()
	),
	LEAVE(
		"leave",
		"",
		List.of(),
		List.of(PlayerState.TALKING_TO_NPC),
		List.of()
	),
	SEARCH_SHOP(
		"search-shop",
		"",
		List.of(),
		List.of(PlayerState.TALKING_TO_NPC),
		List.of()
	),
	BUY(
		"buy",
		"",
		List.of("Item name", "Amount"),
		List.of(PlayerState.TALKING_TO_NPC),
		List.of()
	),
	SELL(
		"sell",
		"",
		List.of("Item name", "Amount"),
		List.of(PlayerState.TALKING_TO_NPC),
		List.of()
	),
	SELL_ALL(
		"sell-all",
		"",
		List.of("Item name"),
		List.of(PlayerState.TALKING_TO_NPC),
		List.of()
	),
	RESTART(
		"restart",
		"",
		List.of(),
		List.of(PlayerState.EXPLORING, PlayerState.BATTLE, PlayerState.TALKING_TO_NPC),
		List.of()
	),
	HELP(
		"help",
		"",
		List.of(),
		List.of(PlayerState.EXPLORING, PlayerState.BATTLE, PlayerState.TALKING_TO_NPC),
		List.of()
	);

	private final String name;
	private final String description;
	private final List<String> arguments;
	private final List<PlayerState> allowedPlayerStates;
	private final List<String> examples;

	private Command(String name, String description, List<String> arguments, List<PlayerState> allowedPlayerStates, List<String> examples) {
		this.name = name;
		this.description = description;
		this.arguments = arguments;
		this.allowedPlayerStates = allowedPlayerStates;
		this.examples = examples;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public List<String> getArguments() {
		return arguments;
	}

	public List<PlayerState> getAllowedPlayerStates() {
		return allowedPlayerStates;
	}

	public List<String> getExamples() {
		return examples;
	}

	public String getFormat() {
		StringBuilder format = new StringBuilder("\"" + name);

		for (String argument : arguments) {
			format.append(" <").append(argument).append(">");
		}
		format.append("\"");

		if (!description.isEmpty()) {
			format.append("\n  Description:").append(description);
		}

		if (!examples.isEmpty()) {
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

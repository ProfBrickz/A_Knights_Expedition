package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.PlayerState;

import java.util.List;

public enum Command {
	MOVE(
		"move",
		"Moves the player to a different room in the specified direction",
		List.of("Direction"),
		List.of(PlayerState.EXPLORING),
		List.of(
			"move north",
			"move south",
			"move east",
			"move west",
			"move up",
			"move left"
		)
	),
	LOOK(
		"look",
		"Displays the description of the current room and any items or NPCs present",
		List.of(),
		List.of(PlayerState.EXPLORING),
		List.of()
	),
	INVENTORY(
		"inventory",
		"Shows all items currently carried by the player",
		List.of(),
		List.of(PlayerState.EXPLORING),
		List.of()
	),
	INSPECT_ITEM(
		"inspect",
		"Provides a detailed description of a specific item from the player's inventory",
		List.of("Item name"),
		List.of(PlayerState.EXPLORING),
		List.of("inspect sword", "inspect potion")
	),
	SEARCH(
		"search",
		"Searches the current room for hidden items or clues",
		List.of(),
		List.of(PlayerState.EXPLORING),
		List.of()
	),
	PICKUP(
		"pickup",
		"Collects an item from the current room and adds it to the player's inventory",
		List.of("item name"),
		List.of(PlayerState.EXPLORING),
		List.of("pickup sword", "pickup stick", "pickup \"Old book\"")
	),
	PICKUP_ALL(
		"pickup-all",
		"Collects all items from the current room and adds them to the player's inventory",
		List.of(),
		List.of(PlayerState.EXPLORING),
		List.of()
	),
	DROP(
		"drop",
		"Removes an item from the player's inventory and places it in the current room",
		List.of("Item name"),
		List.of(PlayerState.EXPLORING),
		List.of("drop sword", "drop potion", "drop \"Old book\"")
	),
	DROP_ALL(
		"drop-all",
		"Removes all items from the player's inventory and places them in the current room",
		List.of(),
		List.of(PlayerState.EXPLORING),
		List.of()
	),
	WALLET(
		"wallet",
		"Displays the player's current gold amount and inventory value",
		List.of(),
		List.of(PlayerState.EXPLORING, PlayerState.TALKING_TO_NPC),
		List.of()
	),
	TALK_TO(
		"talk-to",
		"Initiates a conversation with a specified NPC in the current room",
		List.of("NPC name"),
		List.of(PlayerState.EXPLORING),
		List.of("talk-to blacksmith", "talk-to brewer", "talk-to \"Dr. Babock\"")
	),
	LEAVE(
		"leave",
		"Ends the current conversation with an NPC and returns to exploring state",
		List.of(),
		List.of(PlayerState.TALKING_TO_NPC),
		List.of()
	),
	SEARCH_SHOP(
		"search-shop",
		"Displays the items available for purchase from the current NPC",
		List.of(),
		List.of(PlayerState.TALKING_TO_NPC),
		List.of()
	),
	BUY(
		"buy",
		"Purchase an item from the current NPC's shop",
		List.of("Item name", "Amount"),
		List.of(PlayerState.TALKING_TO_NPC),
		List.of("buy sword 1", "buy potion 3", "buy \"Diamond pickaxe\" 5")
	),
	SELL(
		"sell",
		"Sells an item from the player's inventory to the current NPC",
		List.of("Item name", "Amount"),
		List.of(PlayerState.TALKING_TO_NPC),
		List.of("sell sword 1", "sell potion 2", "sell \"Diamond pickaxe\" 5")
	),
	SELL_ALL(
		"sell-all",
		"Sells all of a specific item from the player's inventory to the current NPC",
		List.of("Item name"),
		List.of(PlayerState.TALKING_TO_NPC),
		List.of("sell-all sword", "sell-all potion", "sell-all \"Diamond pickaxe\"")
	),
	RESTART(
		"restart",
		"Restarts the game from the beginning",
		List.of(),
		List.of(PlayerState.EXPLORING, PlayerState.BATTLE, PlayerState.TALKING_TO_NPC),
		List.of()
	),
	HELP(
		"help",
		"Displays a list of all available commands and their usage",
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
			format.append("\n  Description: ").append(description.replaceAll("\n", "\n  "));
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

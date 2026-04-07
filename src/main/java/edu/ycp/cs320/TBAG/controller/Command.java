package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.PlayerState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public enum Command {
	MOVE(
		"move",
		GameEngine::move,
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
		GameEngine::look,
		"Displays the description of the current room and any items or NPCs present",
		List.of(),
		List.of(PlayerState.EXPLORING),
		List.of()
	),
	INVENTORY(
		"inventory",
		GameEngine::inventory,
		"Shows all items currently carried by the player",
		List.of(),
		List.of(PlayerState.EXPLORING),
		List.of()
	),
	INSPECT_ITEM(
		"inspect",
		GameEngine::inspectItem,
		"Provides a detailed description of a specific item from the player's inventory",
		List.of("Item name"),
		List.of(PlayerState.EXPLORING),
		List.of("inspect sword", "inspect potion")
	),
	SEARCH(
		"search",
		GameEngine::search,
		"Searches the current room for hidden items or clues",
		List.of(),
		List.of(PlayerState.EXPLORING),
		List.of()
	),
	PICKUP(
		"pickup",
		GameEngine::pickupItem,
		"Collects an item from the current room and adds it to the player's inventory",
		List.of("item name"),
		List.of(PlayerState.EXPLORING),
		List.of("pickup sword", "pickup stick", "pickup \"Old book\"")
	),
	PICKUP_ALL(
		"pickup-all",
		GameEngine::pickupAllItems,
		"Collects all items from the current room and adds them to the player's inventory",
		List.of(),
		List.of(PlayerState.EXPLORING),
		List.of()
	),
	DROP(
		"drop",
		GameEngine::dropItem,
		"Removes an item from the player's inventory and places it in the current room",
		List.of("Item name"),
		List.of(PlayerState.EXPLORING),
		List.of("drop sword", "drop potion", "drop \"Old book\"")
	),
	DROP_ALL(
		"drop-all",
		GameEngine::dropAllItems,
		"Removes all items from the player's inventory and places them in the current room",
		List.of(),
		List.of(PlayerState.EXPLORING),
		List.of()
	),
	WALLET(
		"wallet",
		GameEngine::wallet,
		"Displays the player's current gold amount and inventory value",
		List.of(),
		List.of(PlayerState.EXPLORING, PlayerState.TALKING_TO_NPC),
		List.of()
	),
	TALK_TO(
		"talk-to",
		GameEngine::talkToNPC,
		"Initiates a conversation with a specified NPC in the current room",
		List.of("NPC name"),
		List.of(PlayerState.EXPLORING),
		List.of("talk-to blacksmith", "talk-to brewer", "talk-to \"Dr. Babock\"")
	),
	LEAVE(
		"leave",
		GameEngine::leaveNPC,
		"Ends the current conversation with an NPC and returns to exploring state",
		List.of(),
		List.of(PlayerState.TALKING_TO_NPC),
		List.of()
	),
	SEARCH_SHOP(
		"search-shop",
		GameEngine::searchShop,
		"Displays the items available for purchase from the current NPC",
		List.of(),
		List.of(PlayerState.TALKING_TO_NPC),
		List.of()
	),
	BUY(
		"buy",
		GameEngine::buyItem,
		"Purchase an item from the current NPC's shop",
		List.of("Amount", "Item name"),
		List.of(PlayerState.TALKING_TO_NPC),
		List.of("buy 1 sword", "buy 3 potion", "buy 5 \"Diamond pickaxe\"")
	),
	SELL(
		"sell",
		GameEngine::sellItem,
		"Sells an item from the player's inventory to the current NPC",
		List.of("Amount", "Item name"),
		List.of(PlayerState.TALKING_TO_NPC),
		List.of("sell 1 sword", "sell 2 potion", "sell 5 \"Diamond pickaxe\"")
	),
	SELL_ALL(
		"sell-all",
		GameEngine::sellAllItem,
		"Sells all of a specific item from the player's inventory to the current NPC",
		List.of("Item name"),
		List.of(PlayerState.TALKING_TO_NPC),
		List.of("sell-all sword", "sell-all potion", "sell-all \"Diamond pickaxe\"")
	),
	RESTART(
		"restart",
		GameEngine::restart,
		"Restarts the game from the beginning",
		List.of(),
		List.of(PlayerState.EXPLORING, PlayerState.BATTLE, PlayerState.TALKING_TO_NPC),
		List.of()
	),
	HELP(
		"help",
		GameEngine::help,
		"Displays a list of all available commands and their usage",
		List.of(),
		List.of(PlayerState.EXPLORING, PlayerState.BATTLE, PlayerState.TALKING_TO_NPC),
		List.of()
	);

	private final String name;
	private final BiFunction<GameEngine, ArrayList<String>, String> method;
	private final String description;
	private final List<String> arguments;
	private final List<PlayerState> allowedPlayerStates;
	private final List<String> examples;

	Command(String name, BiFunction<GameEngine, ArrayList<String>, String> method, String description, List<String> arguments, List<PlayerState> allowedPlayerStates, List<String> examples) {
		this.name = name;
		this.method = method;
		this.description = description;
		this.arguments = arguments;
		this.allowedPlayerStates = allowedPlayerStates;
		this.examples = examples;
	}

	public String getName() {
		return name;
	}

	public String run(GameEngine gameEngine, ArrayList<String> arguments) {
		String error = gameEngine.validateCommand(this, arguments);
		if (error != null) return error;

		return method.apply(gameEngine, arguments);
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

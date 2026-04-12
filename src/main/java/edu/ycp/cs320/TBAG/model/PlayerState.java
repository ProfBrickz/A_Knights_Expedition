package edu.ycp.cs320.TBAG.model;

public enum PlayerState {
	EXPLORING("exploring"),
	BATTLE("in battle"),
	TALKING_TO_NPC("talking to an NPC");

	private final String name;

	PlayerState(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static PlayerState getByName(String name) {
		for (PlayerState state : PlayerState.values()) {
			if (state.getName().equals(name)) return state;
		}

		return null;
	}
}

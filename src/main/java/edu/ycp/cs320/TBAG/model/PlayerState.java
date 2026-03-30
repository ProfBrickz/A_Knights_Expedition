package edu.ycp.cs320.TBAG.model;

public enum PlayerState {
	EXPLORING("exploring"),
	BATTLE("in battle"),
	TALKING_TO_NPC("talking to an NPC");

	private final String name;

	private PlayerState(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}

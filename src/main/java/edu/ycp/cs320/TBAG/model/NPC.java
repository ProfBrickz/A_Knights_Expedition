package edu.ycp.cs320.TBAG.model;

import java.util.HashMap;

public class NPC {
	private final String id;
	private String name;
	private final HashMap<String, NPCItem> items = new HashMap<>();

	public NPC(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<String, NPCItem> getItems() {
		return items;
	}
}

package edu.ycp.cs320.TBAG.model;

import java.util.HashMap;

public class NPC {
	private final Integer id;
	private String name;
	private final HashMap<String, NPCItem> items = new HashMap<>();

	public NPC(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
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

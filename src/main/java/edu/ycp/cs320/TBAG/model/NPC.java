package edu.ycp.cs320.TBAG.model;

import java.util.HashMap;

public class NPC {
	private final Integer id;
	private String name;
	private final HashMap<String, NPCItem> items = new HashMap<>();

	public NPC(Integer id, String name) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public Integer getId() {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public String getName() {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public void setName(String name) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public HashMap<String, NPCItem> getItems() {
		throw new UnsupportedOperationException("TODO - implement");
	}
}

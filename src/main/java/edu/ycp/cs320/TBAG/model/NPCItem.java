package edu.ycp.cs320.TBAG.model;

public class NPCItem {
	private final String id;
	private final Item item;
	private Integer price;

	public NPCItem(String id, Item item, Integer price) {
		this.id = id;
		this.item = item;
		this.price = price;
	}

	public String getId() {
		return id;
	}

	public Item getItem() {
		return item;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}
}

package edu.ycp.cs320.TBAG.model;

public class Item {
	private final String id;
	private String name, description;
	private Integer sellValue, amount;

	public Item(String id, String name, String description, Integer sellValue, Integer amount) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.sellValue = sellValue;
		this.amount = amount;
	}

	public Item(String id, String name, String description, Integer sellValue) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.sellValue = sellValue;
		this.amount = 1;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getSellValue() {
		return sellValue;
	}

	public void setSellValue(Integer sellValue) {
		this.sellValue = sellValue;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}
}

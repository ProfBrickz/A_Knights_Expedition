package edu.ycp.cs320.TBAG.model;

public class Item {
	private final String id;
	private String name, description;
	private Integer value, amount;
	private String assetName;

	public Item(String id, String name, String description, Integer value, Integer amount) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.value = value;
		this.amount = amount;
	}

	public Item(String id, String name, String description, Integer value) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.value = value;
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

	public Integer getValue() {
		return value;
	}

	public Integer getPrice() {
		return value * 4;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}
}

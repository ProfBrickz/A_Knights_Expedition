package edu.ycp.cs320.TBAG.model;

public class Item {
	private final Integer id;
	private String name, description;
	private Integer value, amount;
	private String assetName;

	public Item(Integer id, String name, String description, Integer value, Integer amount, String assetName) {
		this.id=id;
		this.name = name;
		this.description = description;
		this.value = value;
		this.amount = amount;

		this.assetName=assetName;
		if (this.assetName.isEmpty()) {
			this.assetName = "fixIt.png";
		}
	}

	public Item(Integer id, String name, String description, Integer value, Integer amount) {
		this(id, name,description,value,amount,null);
	}

	public Item(Integer id, String name, String description, Integer value) {
		this(id,name,description,value,1);
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
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

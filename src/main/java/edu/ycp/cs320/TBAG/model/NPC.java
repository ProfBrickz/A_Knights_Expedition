package edu.ycp.cs320.TBAG.model;

public class NPC extends Entity {
	private final Integer id;
	private String name;
	private String greeting;
	private String goodbye;

	public NPC(Integer id, String name, Integer maxHealth, Integer health, String greeting, String goodbye) {
		super(maxHealth, health);
		this.id = id;
		this.name = name;
		this.greeting = greeting;
		this.goodbye = goodbye;
	}

	public NPC(Integer id, String name) {
		this(
			id,
			name,
			100,
			100,
			"Hello adventurer, I am " + name + ".",
			"Goodbye adventurer."
		);
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

	public String getGreeting() {
		return greeting;
	}

	public void setGreeting(String greeting) {
		this.greeting = greeting;
	}

	public String getGoodbye() {
		return goodbye;
	}

	public void setGoodbye(String goodbye) {
		this.goodbye = goodbye;
	}
}

package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.NPC;
import edu.ycp.cs320.TBAG.model.Player;

public class NPCController {
	private InventoryController inventoryController;

	public NPCController(InventoryController inventoryController) {
		this.inventoryController = inventoryController;
	}

	public Item buy(NPC npc, Player player, String npcItemId, Integer amount) {
		Item npcItem = npc.getInventory().getItems().get(npcItemId);
		if (npcItem == null) {
			return null;
		}
		Integer totalCost = npcItem.getPrice() * amount;
		if (player.getCoins() < totalCost) {
			return null;
		}
		player.setCoins(player.getCoins() - totalCost);
		inventoryController.addItem(player.getInventory(), npcItem, amount);

		return npcItem;
	}

	public Integer sell(Player player, Item item, Integer amount) {
		Item playerItem = player.getInventory().getItems().get(item.getId());
		if (playerItem == null) {
			return null;
		}
		if (playerItem.getAmount() < amount) {
			return null;
		}

		player.getInventory().removeItem(item.getId());
		player.setCoins(player.getCoins() + item.getValue() * amount);

		return item.getValue();
	}
}

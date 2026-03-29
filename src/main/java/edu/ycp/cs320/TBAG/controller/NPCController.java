package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.NPC;
import edu.ycp.cs320.TBAG.model.NPCItem;
import edu.ycp.cs320.TBAG.model.Player;

public class NPCController {
	public NPCController() {

	}

	public Item buy(NPC npc, Player player, Integer npcItemId) {
		 NPCItem npcItem = npc.getItems().get(String.valueOf(npcItemId));
    	if (npcItem == null) {
        	return null;
		}
		int price = (int)(npcItem.getItem().getValue() * 1.25);
    	if (player.getCoins() < price) {
        	return null;
    	}
		player.setCoins(player.getCoins() - price);
    	player.getInventory().addItem(npcItem.getItem());

    	return npcItem.getItem();
	}
	

	public Integer sell(NPC npc, Player player, Item item) {
		Item playerItem = player.getInventory().getItems().get(item.getId());
    	if (playerItem == null) {
        	return 0;
		}
		player.getInventory().removeItem(item.getId());
    	player.setCoins(player.getCoins() + item.getValue());

    	return item.getValue();
	}
}

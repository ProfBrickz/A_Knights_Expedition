package edu.ycp.cs320.TBAG.controller;
import edu.ycp.cs320.TBAG.model.Player;


public class PlayerController{
    private Player player;

    public void setModel(Player player){
        this.player = player;
    }

    public boolean move(String direction){
        if (player == null){
            return false;
        }
        return player.move(direction);
    }
}

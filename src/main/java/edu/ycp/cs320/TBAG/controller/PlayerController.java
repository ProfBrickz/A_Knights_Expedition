package edu.ycp.cs320.TBAG.controller;
import edu.ycp.cs320.TBAG.model.Player;


public class PlayerController{
    private Player model;

    public void setModel(Player model){
        this.model = model;
    }

    public boolean move(String direction){
        if (model == null){
            return false;
        }
        return model.move(direction);
    }
}

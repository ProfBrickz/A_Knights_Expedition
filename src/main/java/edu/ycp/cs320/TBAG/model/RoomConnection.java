package edu.ycp.cs320.TBAG.model;




public class RoomConnection {


private String id, description;
private boolean traveled;


   public RoomConnection(String id, String description) {
       this.id = id;
       this.description = description;
       traveled = false;
   }


   public RoomConnection(String id){
       this.id = id;
       this.description = "NULL";
       traveled = false;
   }


   public RoomConnection() {
       this.id = "NULL";
       this.description = "NULL";
       traveled = false;
   }


   public String getID() {
       return id;
   }


   public String getDescription(){
       return description;
   }


   public boolean getTraveled(){
       return traveled;
   }


   public void setID(String id){
       this.id = id;
   }


   public void setDescription(String description){
       this.description = description;
   }
}






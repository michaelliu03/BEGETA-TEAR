package com.kxw.drools.sample;

public class Item {

    private final long item_id;
    private float item_price;
    private String item_category;

    public Item(long id){
        this.item_id=id;
    }

    public long getItemId(){
        return this.item_id;
    }

    public float getItemPrice(){
        return this.item_price;
    }

    public void setItemPrice(float price){
        this.item_price=price;
    }

    public String getItemCategory(){
        return this.item_category;
    }

    public void setItemCategoty(String cate){
        this.item_category=cate;
    }
}
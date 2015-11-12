package com.kxw.drools.sample;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Purchase {
    private User user;
    private Map<Long, Integer> items;
    private Map<Long, Item> IdToItem;
    private float total;
    private Map<String, Float> favourable;

    public Purchase() {
        items = new HashMap<Long, Integer>();
        IdToItem = new HashMap<Long, Item>();
        favourable = new HashMap<String, Float>();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User u) {
        this.user = u;
    }

    public Map<Long, Integer> getItems() {
        return this.items;
    }

    public void addItem(Item item, int num) {
        this.IdToItem.put(item.getItemId(), item);
        this.items.put(item.getItemId(), num);
    }

    public float getCategotyTotal(String cate) {
        float t = 0;
        Set<Long> Ids = this.IdToItem.keySet();
        for (Long id : Ids) {
            Item item = IdToItem.get(id);
            if (item.getItemCategory().equals(cate)) {
                int num = items.get(id);
                t += num * item.getItemPrice();
            }
        }
        return t;
    }

    public float getTotal() {
        Set<Long> Ids = this.IdToItem.keySet();
        for (Long id : Ids) {
            Item item = IdToItem.get(id);
            int num = items.get(id);
            total += num * item.getItemPrice();
        }
        return total;
    }

    public void addFavour(String message, float num) {
        this.favourable.put(message, num);
    }

    public Map<String, Float> getFavour() {
        return this.favourable;
    }
}
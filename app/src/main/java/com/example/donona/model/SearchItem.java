package com.example.donona.model;

public class SearchItem {
    private String itemName = "";
    private String itemAddress = "";
    private String key = "";

    public SearchItem() {}

    public SearchItem(String itemName, String itemAddress) {
        this.itemAddress = itemAddress;
        this.itemName = itemName;
    }

    public String getKey() {
        return itemName + " " + itemAddress;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemAddress() {
        return itemAddress;
    }
}

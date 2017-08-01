package io.indices.troubleinminecraft.shop;

import io.indices.troubleinminecraft.shop.items.ShopItem;

import java.util.List;

public abstract class Shop {
    protected List<ShopItem> items;

    public List<ShopItem> getItems() {
        return items;
    }
}

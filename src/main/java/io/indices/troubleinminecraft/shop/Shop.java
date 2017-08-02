package io.indices.troubleinminecraft.shop;

import io.indices.troubleinminecraft.shop.items.ShopItem;

import java.util.ArrayList;
import java.util.List;

public abstract class Shop {
    protected List<ShopItem> items = new ArrayList<>();

    public List<ShopItem> getItems() {
        return items;
    }
}

package io.indices.troubleinminecraft.shop;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

import io.indices.troubleinminecraft.shop.items.ShopItem;

public abstract class Shop {
    protected List<ShopItem> items = new ArrayList<>();

    @Nonnull
    public List<ShopItem> getItems() {
        return items;
    }
}

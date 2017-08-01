package io.indices.troubleinminecraft.shop;

import io.indices.troubleinminecraft.shop.items.BodyArmour;

import javax.inject.Singleton;

@Singleton
public class TraitorShop extends Shop {
    public TraitorShop() {
        items.add(new BodyArmour());
    }
}

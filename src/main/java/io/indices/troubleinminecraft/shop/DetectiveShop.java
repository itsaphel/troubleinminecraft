package io.indices.troubleinminecraft.shop;

import javax.inject.Singleton;

import io.indices.troubleinminecraft.shop.items.role.BodyArmour;

@Singleton
public class DetectiveShop extends Shop {
    public DetectiveShop() {
        items.add(new BodyArmour());
    }
}

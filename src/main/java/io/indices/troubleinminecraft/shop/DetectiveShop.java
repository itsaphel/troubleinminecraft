package io.indices.troubleinminecraft.shop;

import io.indices.troubleinminecraft.shop.items.*;

import javax.inject.Singleton;

@Singleton
public class DetectiveShop extends Shop {
    public DetectiveShop() {
        items.add(new BodyArmour());
    }
}

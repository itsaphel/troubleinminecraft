package io.indices.troubleinminecraft.shop;

import io.indices.troubleinminecraft.shop.items.*;

import javax.inject.Singleton;

@Singleton
public class TraitorShop extends Shop {
    public TraitorShop() {
        items.add(new BodyArmour());
        items.add(new CreeperEgg());
        items.add(new Disguiser());
        items.add(new Knife());
        items.add(new Radar());
    }
}

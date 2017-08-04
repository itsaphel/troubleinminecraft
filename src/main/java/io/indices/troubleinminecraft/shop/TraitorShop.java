package io.indices.troubleinminecraft.shop;

import javax.inject.Singleton;

import io.indices.troubleinminecraft.shop.items.BodyArmour;
import io.indices.troubleinminecraft.shop.items.CreeperEgg;
import io.indices.troubleinminecraft.shop.items.Disguiser;
import io.indices.troubleinminecraft.shop.items.Knife;
import io.indices.troubleinminecraft.shop.items.Radar;

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

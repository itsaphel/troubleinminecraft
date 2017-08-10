package io.indices.troubleinminecraft.shop;

import javax.inject.Singleton;

import io.indices.troubleinminecraft.shop.items.*;

@Singleton
public class TraitorShop extends Shop {
    public TraitorShop() {
        items.add(new BodyArmour());
        items.add(new CreeperEgg());
        items.add(new FlareGun());
        items.add(new Disguiser());
        items.add(new Knife());
        items.add(new Radar());
        items.add(new C4());
        items.add(new Jihad());
    }
}

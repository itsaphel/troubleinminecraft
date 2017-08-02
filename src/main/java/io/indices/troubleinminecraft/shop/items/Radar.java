package io.indices.troubleinminecraft.shop.items;

import io.indices.troubleinminecraft.abilities.RadarAbility;

public class Radar extends ShopItem {
    public Radar() {
        name = "Radar";
        cost = 1;
        itemStack = RadarAbility.itemStack;
        addAbility(RadarAbility.class);
    }
}

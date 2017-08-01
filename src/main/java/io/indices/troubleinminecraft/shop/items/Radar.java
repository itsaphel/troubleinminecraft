package io.indices.troubleinminecraft.shop.items;

import io.indices.troubleinminecraft.abilities.RadarAbility;

public class Radar extends ShopItem {
    public Radar() {
        itemStack = RadarAbility.itemStack;
        addAbility(RadarAbility.class);
    }
}

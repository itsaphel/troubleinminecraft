package io.indices.troubleinminecraft.shop.items;


import io.indices.troubleinminecraft.abilities.DisguiserAbility;

public class Disguiser extends ShopItem {
    public Disguiser() {
        itemStack = null;
        addAbility(DisguiserAbility.class);
    }
}

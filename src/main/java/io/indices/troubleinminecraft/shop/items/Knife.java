package io.indices.troubleinminecraft.shop.items;

import io.indices.troubleinminecraft.abilities.KnifeAbility;

public class Knife extends ShopItem {
    public Knife() {
        name = "Knife";
        cost = 2;
        itemStack = KnifeAbility.itemStack;
        addAbility(KnifeAbility.class);
    }
}

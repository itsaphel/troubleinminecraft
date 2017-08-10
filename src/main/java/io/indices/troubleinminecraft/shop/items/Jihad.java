package io.indices.troubleinminecraft.shop.items;

import io.indices.troubleinminecraft.abilities.JihadAbility;

public class Jihad extends ShopItem {
    public Jihad() {
        name = "Jihad Bomb";
        cost = 2;
        itemStack = JihadAbility.ITEM_STACK;
        addAbility(JihadAbility.class);
    }
}

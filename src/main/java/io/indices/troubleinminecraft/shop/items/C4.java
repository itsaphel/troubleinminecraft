package io.indices.troubleinminecraft.shop.items;

import io.indices.troubleinminecraft.abilities.C4Ability;

public class C4 extends ShopItem {
    public C4() {
        name = "C4";
        cost = 2;
        itemStack = C4Ability.ITEM_STACK;
        addAbility(C4Ability.class);
    }
}

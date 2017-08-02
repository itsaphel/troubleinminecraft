package io.indices.troubleinminecraft.shop.items;

import io.indices.troubleinminecraft.abilities.CreeperEggAbility;

public class CreeperEgg extends ShopItem {
    public CreeperEgg() {
        name = "Creeper Eggs";
        cost = 2;
        itemStack = CreeperEggAbility.itemStack;
    }
}

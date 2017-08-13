package io.indices.troubleinminecraft.shop.items.role;

import io.indices.troubleinminecraft.abilities.CreeperEggAbility;

public class CreeperEgg extends RoleItem {
    public CreeperEgg() {
        name = "Creeper Eggs";
        cost = 2;
        itemStack = CreeperEggAbility.ITEM_STACK;
    }
}

package io.indices.troubleinminecraft.shop.items.role;

import io.indices.troubleinminecraft.abilities.HarpoonAbility;

public class Harpoon extends RoleItem {
    public Harpoon() {
        name = "Harpoon";
        cost = 2;
        itemStack = HarpoonAbility.ITEM_STACK;
        addAbility(HarpoonAbility.class);
    }
}

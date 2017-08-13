package io.indices.troubleinminecraft.shop.items.role;

import io.indices.troubleinminecraft.abilities.C4Ability;

public class C4 extends RoleItem {
    public C4() {
        name = "C4";
        cost = 2;
        itemStack = C4Ability.ITEM_STACK;
        addAbility(C4Ability.class);
    }
}

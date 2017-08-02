package io.indices.troubleinminecraft.shop.items;

import io.indices.troubleinminecraft.abilities.KnockbackStickAbility;

public class KnockbackStick extends ShopItem {
    public KnockbackStick() {
        name = "Knockback Stick";
        cost = 1;
        itemStack = KnockbackStickAbility.itemStack;
        addAbility(KnockbackStickAbility.class);
    }
}

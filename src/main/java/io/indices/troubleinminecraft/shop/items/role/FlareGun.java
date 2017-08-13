package io.indices.troubleinminecraft.shop.items.role;

import io.indices.troubleinminecraft.abilities.FlareGunAbility;

public class FlareGun extends RoleItem {
    public FlareGun() {
        name = "Flare Gun";
        cost = 2;
        itemStack = FlareGunAbility.ITEM_STACK;
        addAbility(FlareGunAbility.class);
    }
}

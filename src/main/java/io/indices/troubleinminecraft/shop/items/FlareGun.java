package io.indices.troubleinminecraft.shop.items;

import io.indices.troubleinminecraft.abilities.FlareGunAbility;

public class FlareGun extends ShopItem {
    public FlareGun() {
        name = "Flare Gun";
        cost = 2;
        itemStack = FlareGunAbility.ITEM_STACK;
        addAbility(FlareGunAbility.class);
    }
}

package io.indices.troubleinminecraft.shop.items;

import io.indices.troubleinminecraft.abilities.BodyArmourAbility;

public class BodyArmour extends ShopItem {
    public BodyArmour() {
        itemStack = null;
        addAbility(BodyArmourAbility.class);
    }
}

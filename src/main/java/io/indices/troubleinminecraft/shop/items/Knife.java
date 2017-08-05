package io.indices.troubleinminecraft.shop.items;

import com.voxelgameslib.voxelgameslib.lang.Lang;
import io.indices.troubleinminecraft.abilities.KnifeAbility;
import io.indices.troubleinminecraft.lang.TIMLangKey;

public class Knife extends ShopItem {
    public Knife() {
        name = Lang.string(TIMLangKey.ITEM_KNIFE_TITLE);
        cost = 2;
        itemStack = KnifeAbility.itemStack;
        addAbility(KnifeAbility.class);
    }
}

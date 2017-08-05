package io.indices.troubleinminecraft.shop.items;


import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.utils.ItemBuilder;

import io.indices.troubleinminecraft.lang.TIMLangKey;
import org.bukkit.Material;

import io.indices.troubleinminecraft.abilities.DisguiserAbility;

public class Disguiser extends ShopItem {
    public Disguiser() {
        name = "Disguiser";
        cost = 2;
        itemStack = new ItemBuilder(Material.SKULL).amount(1).name(Lang.string(TIMLangKey.ITEM_DISGUISER_TITLE)).lore(Lang.string(TIMLangKey.ITEM_DISGUISER_LORE)).build();
        addAbility(DisguiserAbility.class);
    }
}

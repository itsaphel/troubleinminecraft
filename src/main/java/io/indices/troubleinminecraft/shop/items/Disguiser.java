package io.indices.troubleinminecraft.shop.items;


import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.utils.ItemBuilder;

import io.indices.troubleinminecraft.lang.TIMLangKey;
import net.kyori.text.LegacyComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import io.indices.troubleinminecraft.abilities.DisguiserAbility;

public class Disguiser extends ShopItem {
    public Disguiser() {
        name = "Disguiser";
        cost = 2;
        itemStack = new ItemBuilder(Material.SKULL_ITEM).amount(1).name(LegacyComponent.to(Lang.trans(TIMLangKey.ITEM_DISGUISER_TITLE))).lore(LegacyComponent.to(Lang.trans(TIMLangKey.ITEM_DISGUISER_LORE))).build();
        addAbility(DisguiserAbility.class);
    }
}

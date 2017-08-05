package io.indices.troubleinminecraft.shop.items;

import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.utils.ItemBuilder;

import io.indices.troubleinminecraft.lang.TIMLangKey;
import net.kyori.text.LegacyComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import io.indices.troubleinminecraft.abilities.BodyArmourAbility;

public class BodyArmour extends ShopItem {
    public BodyArmour() {
        name = "Body Armour";
        cost = 1;
        itemStack = new ItemBuilder(Material.IRON_CHESTPLATE).amount(1).name(LegacyComponent.to(Lang.trans(TIMLangKey.ITEM_BODY_ARMOUR_TITLE))).build();
        addAbility(BodyArmourAbility.class);
    }
}

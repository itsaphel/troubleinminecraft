package io.indices.troubleinminecraft.shop.items;

import com.voxelgameslib.voxelgameslib.utils.ItemBuilder;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import io.indices.troubleinminecraft.abilities.BodyArmourAbility;

public class BodyArmour extends ShopItem {
    public BodyArmour() {
        name = "Body Armour";
        cost = 1;
        itemStack = new ItemBuilder(Material.DIAMOND_CHESTPLATE).amount(1).name(ChatColor.RED + "Standard-issue Traitor Armour").build();
        addAbility(BodyArmourAbility.class);
    }
}

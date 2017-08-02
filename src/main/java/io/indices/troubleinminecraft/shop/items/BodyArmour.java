package io.indices.troubleinminecraft.shop.items;

import io.indices.troubleinminecraft.abilities.BodyArmourAbility;
import me.minidigger.voxelgameslib.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class BodyArmour extends ShopItem {
    public BodyArmour() {
        name = "Body Armour";
        cost = 1;
        itemStack = new ItemBuilder(Material.DIAMOND_CHESTPLATE).amount(1).name(ChatColor.RED + "Standard-issue Traitor Armour").build();
        addAbility(BodyArmourAbility.class);
    }
}

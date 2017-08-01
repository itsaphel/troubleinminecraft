package io.indices.troubleinminecraft.shop.items;

import me.minidigger.voxelgameslib.components.ability.Ability;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class ShopItem {
    private ItemStack itemStack;
    private List<Ability> abilities;

    ShopItem() {
        //
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public void addAbility(Ability ability) {
        abilities.add(ability);
    }
}

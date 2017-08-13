package io.indices.troubleinminecraft.shop.items;

import com.voxelgameslib.voxelgameslib.user.User;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public abstract class ShopItem {
    protected String name;
    protected int cost;
    protected ItemStack itemStack;

    @Nonnull
    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    @Nonnull
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Handle purchase logic
     */
    abstract public void purchase(User buyer);
}

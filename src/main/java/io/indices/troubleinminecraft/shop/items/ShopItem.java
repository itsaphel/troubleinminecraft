package io.indices.troubleinminecraft.shop.items;

import com.voxelgameslib.voxelgameslib.components.ability.Ability;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

import org.bukkit.inventory.ItemStack;

public abstract class ShopItem {
    protected String name;
    protected int cost;
    protected ItemStack itemStack;
    private List<Class<? extends Ability>> abilities = new ArrayList<>();

    ShopItem() {
        //
    }

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

    @Nonnull
    public List<Class<? extends Ability>> getAbilities() {
        return abilities;
    }

    public <T extends Ability> void addAbility(@Nonnull Class<T> abilityClass) {
        abilities.add(abilityClass);
    }
}

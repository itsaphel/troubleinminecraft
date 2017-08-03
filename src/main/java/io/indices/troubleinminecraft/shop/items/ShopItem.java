package io.indices.troubleinminecraft.shop.items;

import com.voxelgameslib.voxelgameslib.components.ability.Ability;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class ShopItem {
    protected String name;
    protected int cost;
    protected ItemStack itemStack;
    private List<Class<? extends Ability>> abilities = new ArrayList<>();

    ShopItem() {
        //
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public List<Class<? extends Ability>> getAbilities() {
        return abilities;
    }

    public <T extends Ability> void addAbility(Class<T> abilityClass) {
        abilities.add(abilityClass);
    }
}

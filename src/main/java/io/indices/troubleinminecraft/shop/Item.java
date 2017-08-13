package io.indices.troubleinminecraft.shop;

import java.util.ArrayList;
import java.util.List;

import com.voxelgameslib.voxelgameslib.components.ability.Ability;

import org.bukkit.inventory.ItemStack;

public class Item {

    private ItemStack itemStack;
    private int cost;
    private List<Class<? extends Ability>> abilities = new ArrayList<>();

    public ItemStack itemStack() {
        return itemStack;
    }

    public Item itemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    public int cost() {
        return cost;
    }

    public Item cost(int cost) {
        this.cost = cost;
        return this;
    }

    public List<Class<? extends Ability>> abilities() {
        return abilities;
    }

    public Item addAbility(Class<? extends Ability> ability) {
        abilities.add(ability);
        return this;
    }
}

package io.indices.troubleinminecraft.shop;

import com.voxelgameslib.voxelgameslib.components.ability.Ability;
import com.voxelgameslib.voxelgameslib.utils.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class Item {
    private String id;
    private ItemStack itemStack;
    private int cost;
    private List<Class<? extends Ability>> abilities = new ArrayList<>();

    public String id() {
        return id;
    }

    public Item id(String id) {
        this.id = id;
        return this;
    }

    public ItemStack itemStack() {
        return new ItemBuilder(itemStack).lore("Credits: " + cost).build();
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

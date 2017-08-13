package io.indices.troubleinminecraft.shop;

import com.voxelgameslib.voxelgameslib.components.ability.Ability;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Item {
    private ItemStack itemStack;
    private List<Class<? extends Ability>> abilities = new ArrayList<>();

    public ItemStack itemStack() {
        return itemStack;
    }

    public Item itemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
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

package io.indices.troubleinminecraft.shop;

import com.voxelgameslib.voxelgameslib.utils.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.inventory.ItemStack;

import io.indices.troubleinminecraft.abilities.TTTAbility;
import io.indices.troubleinminecraft.abilities.modifiers.AbilityModifier;

public class Item {
    private String id;
    private ItemStack itemStack;
    private int cost;
    private List<Class<? extends TTTAbility>> abilities = new ArrayList<>();
    private List<Class<AbilityModifier>> abilityModifiers = new ArrayList<>();

    @Nonnull
    public String id() {
        return id;
    }

    public Item id(@Nonnull String id) {
        this.id = id;
        return this;
    }

    @Nonnull
    public ItemStack itemStack() {
        return new ItemBuilder(itemStack).lore("Credits: " + cost).build();
    }

    public Item itemStack(@Nonnull ItemStack itemStack) {
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

    @Nonnull
    public List<Class<? extends TTTAbility>> abilities() {
        return abilities;
    }

    public Item addAbility(@Nonnull Class<? extends TTTAbility> ability) {
        abilities.add(ability);
        return this;
    }

    @Nonnull
    public List<Class<AbilityModifier>> abilityModifiers() {
        return abilityModifiers;
    }

    public Item abilityModifier(@Nonnull Class<AbilityModifier> abilityModifier) {
        this.abilityModifiers.add(abilityModifier);
        return this;
    }
}

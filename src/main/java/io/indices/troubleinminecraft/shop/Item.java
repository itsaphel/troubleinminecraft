package io.indices.troubleinminecraft.shop;

import com.voxelgameslib.voxelgameslib.utils.ItemBuilder;
import io.indices.troubleinminecraft.abilities.TTTAbility;
import io.indices.troubleinminecraft.abilities.modifiers.AbilityModifier;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Item {
    private String id;
    private ItemStack itemStack;
    private int cost;
    private List<Class<? extends TTTAbility>> abilities = new ArrayList<>();
    private List<Class<? extends AbilityModifier>> abilityModifiers = new ArrayList<>();

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

    public Item ability(@Nonnull Class<? extends TTTAbility> ability) {
        abilities.add(ability);
        return this;
    }

    @Nonnull
    public List<Class<? extends AbilityModifier>> abilityModifiers() {
        return abilityModifiers;
    }

    public Item abilityModifier(@Nonnull Class<? extends AbilityModifier> abilityModifier) {
        this.abilityModifiers.add(abilityModifier);
        return this;
    }
}

package io.indices.troubleinminecraft.shop;

import io.indices.troubleinminecraft.abilities.modifiers.AbilityModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PointShopUser {
    private UUID uuid;
    private List<Class<? extends AbilityModifier>> modifiers = new ArrayList<>();

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public List<Class<? extends AbilityModifier>> getModifiers() {
        return modifiers;
    }

    public void addModifier(Class<? extends AbilityModifier> modifier) {
        this.modifiers.add(modifier);
    }

    public void setModifiers(List<Class<? extends AbilityModifier>> modifiers) {
        this.modifiers = modifiers;
    }
}

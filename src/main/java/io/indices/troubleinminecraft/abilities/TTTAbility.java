package io.indices.troubleinminecraft.abilities;

import com.voxelgameslib.voxelgameslib.components.ability.Ability;
import com.voxelgameslib.voxelgameslib.user.User;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

import io.indices.troubleinminecraft.abilities.modifiers.AbilityModifier;

public abstract class TTTAbility extends Ability {
    protected List<AbilityModifier> activeModifiers = new ArrayList<>();
    protected List<Class<? extends AbilityModifier>> acceptedModifiers = new ArrayList<>();

    /**
     * Create a new ability
     *
     * @param user the user the ability will affect/apply to
     */
    public TTTAbility(@Nonnull User user) {
        super(user);
    }

    @Override
    public void start() {
        activeModifiers.forEach(mod -> mod.affect(this));
    }

    public void addModifier(AbilityModifier modifier) {
        this.activeModifiers.add(modifier);
    }

    public List<Class<? extends AbilityModifier>> getAcceptedModifiers() {
        return acceptedModifiers;
    }
}

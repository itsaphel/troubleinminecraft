package io.indices.troubleinminecraft.abilities;

import com.voxelgameslib.voxelgameslib.components.ability.Ability;
import com.voxelgameslib.voxelgameslib.user.User;

import javax.annotation.Nonnull;

public class DisguiserAbility extends Ability {
    /**
     * @see Ability#Ability(User)
     */
    public DisguiserAbility(@Nonnull User user) {
        super(user);
    }

    @Override
    public void start() {
        // todo disguise the player
        // we will require LibsDisguises to do this, fuck more packet manipulation for no real reason
    }

    @Override
    public void stop() {

    }

    @Override
    public void tick() {

    }
}

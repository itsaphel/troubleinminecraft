package io.indices.troubleinminecraft.abilities;

import me.minidigger.voxelgameslib.components.ability.Ability;
import me.minidigger.voxelgameslib.user.User;

public class DisguiserAbility extends Ability {
    /**
     * @see Ability#Ability(User)
     */
    public DisguiserAbility(User user) {
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

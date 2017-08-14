package io.indices.troubleinminecraft.abilities;

import com.voxelgameslib.voxelgameslib.components.ability.Ability;
import com.voxelgameslib.voxelgameslib.game.Game;
import com.voxelgameslib.voxelgameslib.game.GameHandler;
import com.voxelgameslib.voxelgameslib.user.User;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nonnull;

public abstract class ExplosionAbility extends TTTAbility {

    /**
     * @see Ability#Ability(User)
     */
    public ExplosionAbility(@Nonnull User user) {
        super(user);
    }

    public void explode(GameHandler gameHandler, Location location, int radius) {
        location.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, location, 1);

        Game game = gameHandler.getGames(affected.getUuid(), false).get(0);

        if (game != null) {
            game.getPlayers().forEach(user -> {
                if (user.getPlayer().getLocation().distanceSquared(location) < radius * radius) {
                    // cya m8
                    user.getPlayer().setHealth(0);
                }
            });
        }

        unregister(true);
    }
}

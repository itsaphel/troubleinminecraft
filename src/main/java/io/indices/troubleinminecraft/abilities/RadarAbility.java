package io.indices.troubleinminecraft.abilities;

import io.indices.troubleinminecraft.TroubleInMinecraftPlugin;
import me.minidigger.voxelgameslib.components.ability.Ability;
import me.minidigger.voxelgameslib.game.Game;
import me.minidigger.voxelgameslib.game.GameHandler;
import me.minidigger.voxelgameslib.user.User;
import me.minidigger.voxelgameslib.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;

public class RadarAbility extends Ability {
    @Inject
    private GameHandler gameHandler;

    public static ItemStack itemStack = new ItemBuilder(Material.COMPASS).amount(1).name("Innocent Tracker")
            .lore("This will point you towards the nearest innocent player.").build();

    /**
     * @see Ability#Ability(User)
     */
    public RadarAbility(@Nonnull User user) {
        super(user);
    }

    @Override
    public void start() {
        affected.getPlayer().getInventory().addItem(itemStack);
    }

    @Override
    public void stop() {

    }

    @Override
    public void tick() {
        // todo make this more efficient if it's going to run every tick. it could also run less often.

        List<Game> games = gameHandler.getGames(affected.getUuid(), false);
        if (games.size() == 1) {
            Game game = games.get(0);

            User nearest = getNearestPlayer(game.getPlayers());

            if (nearest != null) {
                affected.getPlayer().setCompassTarget(nearest.getPlayer().getLocation());
            }
        }
    }

    private User getNearestPlayer(List<User> targets) {
        double nearestDistance = 0.0D;
        User nearest = null;

        for (User target : targets) {
            double distance = target.getPlayer().getLocation().distanceSquared(affected.getPlayer().getLocation());

            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = target;
            }
        }

        return nearest;
    }
}

package io.indices.troubleinminecraft.abilities;

import com.voxelgameslib.voxelgameslib.components.ability.Ability;
import com.voxelgameslib.voxelgameslib.game.Game;
import com.voxelgameslib.voxelgameslib.game.GameHandler;
import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.user.User;
import com.voxelgameslib.voxelgameslib.utils.ItemBuilder;

import net.kyori.text.LegacyComponent;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.indices.troubleinminecraft.lang.TIMLangKey;

public class RadarAbility extends Ability {
    @Inject
    private GameHandler gameHandler;

    public static ItemStack itemStack = new ItemBuilder(Material.COMPASS).amount(1).name(LegacyComponent.to(Lang.trans(TIMLangKey.ITEM_RADAR_TITLE)))
            .lore(LegacyComponent.to(Lang.trans(TIMLangKey.ITEM_RADAR_LORE))).build();

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
        List<Game> games = gameHandler.getGames(affected.getUuid(), false);
        if (games.size() == 1) {
            Game game = games.get(0);

            User nearest = getNearestPlayer(game.getPlayers().stream().filter(user -> !user.getUuid().equals(affected.getUuid())).collect(Collectors.toList()));

            if (nearest != null) {
                affected.getPlayer().setCompassTarget(nearest.getPlayer().getLocation());
            }
        }
    }

    @Nullable
    private User getNearestPlayer(@Nonnull List<User> targets) {
        double nearestDistance = 0.0D;
        User nearest = null;

        for (User target : targets) {
            double distance = target.getPlayer().getLocation().distanceSquared(affected.getPlayer().getLocation());

            if (nearest == null || distance < nearestDistance) {
                nearestDistance = distance;
                nearest = target;
            }
        }

        return nearest;
    }
}

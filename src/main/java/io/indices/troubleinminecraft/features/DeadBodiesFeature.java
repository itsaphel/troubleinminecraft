package io.indices.troubleinminecraft.features;

import com.voxelgameslib.voxelgameslib.event.GameEvent;
import com.voxelgameslib.voxelgameslib.feature.AbstractFeature;
import com.voxelgameslib.voxelgameslib.feature.features.PersonalScoreboardFeature;
import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.user.User;
import io.indices.troubleinminecraft.game.ChatUtils;
import io.indices.troubleinminecraft.game.DeadPlayer;
import io.indices.troubleinminecraft.game.TIMData;
import io.indices.troubleinminecraft.lang.TIMLangKey;
import net.kyori.text.LegacyComponent;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class DeadBodiesFeature extends AbstractFeature {

    @Override
    @Nonnull
    public Class[] getSoftDependencies() {
        return new Class[]{GameFeature.class, PersonalScoreboardFeature.class};
    }

    @Nonnull
    public Zombie spawnBody(@Nonnull Location location) {
        Zombie zombie = location.getWorld().spawn(location, Zombie.class);
        zombie.setCustomName(LegacyComponent.to(Lang.trans(TIMLangKey.UNIDENTIFIED_BODY)));
        zombie.setBaby(false);

        return zombie;
    }

    @GameEvent
    public void rightClickZombie(@Nonnull PlayerInteractEntityEvent event, @Nonnull User user) {
        if (event.getRightClicked() instanceof Zombie && event.getHand() == EquipmentSlot.HAND) {

            getPhase().getOptionalFeature(GameFeature.class).ifPresent(feature -> {
                Map<Entity, DeadPlayer> zombiePlayerMap = feature.getZombiePlayerMap();
                if (zombiePlayerMap.containsKey(event.getRightClicked())) {
                    DeadPlayer deadPlayer = zombiePlayerMap.get(event.getRightClicked());

                    if (!deadPlayer.isIdentified()) {
                        feature.decrementVisiblePlayersLeft();
                        getPhase().getFeature(PersonalScoreboardFeature.class).getGlobalScoreboard().getLines("players-left").forEach(line -> line.setValue(getPhase().getFeature(GameFeature.class).getVisiblePlayersLeft() + ""));

                        getPhase().getGame().getPlayers().forEach(otherPlayer -> {
                                    Lang.msg(otherPlayer, TIMLangKey.THE_BODY_OF_X_HAS_BEEN_FOUND, deadPlayer.getDisplayName());
                                    otherPlayer.sendMessage(TextComponent.of("They were a(n) ").color(TextColor.BLUE).append(TextComponent.of(ChatUtils.formatRoleName(deadPlayer.getRole()) + "").append(TextComponent.of("!").color(TextColor.BLUE))));
                                }
                        );

                        event.getRightClicked().setCustomName(feature.getRole(user).getColour() + user.getRawDisplayName());
                        deadPlayer.setIdentified(true);
                    } else {
                        user.sendMessage(TextComponent.of("This is the body of " + deadPlayer.getDisplayName() + ". They were a(n) " + ChatUtils.formatRoleName(deadPlayer.getRole())).color(TextColor.BLUE));
                    }
                }
            });
        }
    }

    @GameEvent
    public void onZombieDamage(@Nonnull EntityDamageEvent event) {
        if (getZombiePlayerMap().containsKey(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @GameEvent
    public void onEntityBurn(@Nonnull EntityCombustEvent event) {
        if (getZombiePlayerMap().containsKey(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @GameEvent
    public void onZombieTarget(@Nonnull EntityTargetEvent event) {
        if (getZombiePlayerMap().containsKey(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    private Map<Entity, DeadPlayer> getZombiePlayerMap() {
        if (getPhase().getOptionalFeature(GameFeature.class).isPresent()) {
            return getPhase().getFeature(GameFeature.class).getZombiePlayerMap();
        } else {
            if (getPhase().getGame().getGameData(TIMData.class).isPresent()) {
                return getPhase().getGame().getGameData(TIMData.class).get().getZombiePlayerMap();
            } else {
                return new HashMap<>();
            }
        }
    }
}

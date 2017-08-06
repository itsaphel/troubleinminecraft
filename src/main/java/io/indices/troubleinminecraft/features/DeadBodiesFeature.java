package io.indices.troubleinminecraft.features;

import com.voxelgameslib.voxelgameslib.event.GameEvent;
import com.voxelgameslib.voxelgameslib.feature.AbstractFeature;
import com.voxelgameslib.voxelgameslib.feature.features.PersonalScoreboardFeature;
import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.user.User;

import net.kyori.text.LegacyComponent;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;

import java.util.Map;
import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import io.indices.troubleinminecraft.game.ChatUtils;
import io.indices.troubleinminecraft.game.DeadPlayer;
import io.indices.troubleinminecraft.lang.TIMLangKey;
import io.indices.troubleinminecraft.phases.PostGamePhase;

public class DeadBodiesFeature extends AbstractFeature {
    @Override
    public void init() {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void tick() {

    }

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

            if (getPhase() instanceof PostGamePhase) {
                return;
            }

            Map<Entity, DeadPlayer> zombiePlayerMap = getPhase().getFeature(GameFeature.class).getZombiePlayerMap();
            if (zombiePlayerMap.containsKey(event.getRightClicked())) {
                DeadPlayer deadPlayer = zombiePlayerMap.get(event.getRightClicked());

                if (!deadPlayer.isIdentified()) {
                    getPhase().getFeature(GameFeature.class).decrementVisiblePlayersLeft();
                    getPhase().getFeature(PersonalScoreboardFeature.class).getGlobalScoreboard().getLines("players-left").forEach(line -> line.setValue(getPhase().getFeature(GameFeature.class).getVisiblePlayersLeft() + ""));

                    getPhase().getGame().getPlayers().forEach(otherPlayer -> {
                        Lang.msg(otherPlayer, TIMLangKey.THE_BODY_OF_X_HAS_BEEN_FOUND, deadPlayer.getDisplayName());
                                otherPlayer.sendMessage(TextComponent.of("They were a(n) ").color(TextColor.BLUE).append(TextComponent.of(ChatUtils.formatRoleName(deadPlayer.getRole()) + "").append(TextComponent.of("!").color(TextColor.BLUE))));
                            }
                    );

                    event.getRightClicked().setCustomName(getPhase().getFeature(GameFeature.class).getRole(user).getColour() + user.getRawDisplayName());
                    deadPlayer.setIdentified(true);
                } else {
                    user.sendMessage(TextComponent.of("This is the body of " + deadPlayer.getDisplayName() + ". They were a(n) " + ChatUtils.formatRoleName(deadPlayer.getRole())).color(TextColor.BLUE));
                }
            }
        }
    }

    @GameEvent
    public void onZombieDamage(@Nonnull EntityDamageEvent event) {
        if (getPhase().getFeature(GameFeature.class).getZombiePlayerMap().containsKey(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @GameEvent
    public void onEntityBurn(@Nonnull EntityCombustEvent event) {
        if (getPhase().getFeature(GameFeature.class).getZombiePlayerMap().containsKey(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @GameEvent
    public void onZombieTarget(@Nonnull EntityTargetEvent event) {
        if (getPhase().getFeature(GameFeature.class).getZombiePlayerMap().containsKey(event.getEntity())) {
            event.setTarget(null);
        }
    }
}

package io.indices.troubleinminecraft.features;

import com.voxelgameslib.voxelgameslib.event.GameEvent;
import com.voxelgameslib.voxelgameslib.feature.AbstractFeature;
import com.voxelgameslib.voxelgameslib.feature.features.PersonalScoreboardFeature;
import com.voxelgameslib.voxelgameslib.user.User;

import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;

import java.util.Map;

import org.bukkit.ChatColor;
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
    public Class[] getSoftDependencies() {
        return new Class[]{GameFeature.class, PersonalScoreboardFeature.class};
    }

    public Zombie spawnBody(Location location) {
        Zombie zombie = location.getWorld().spawn(location, Zombie.class);
        zombie.setCustomName(ChatColor.RESET + "??????????");
        zombie.setBaby(false);

        return zombie;
    }

    @GameEvent
    public void rightClickZombie(PlayerInteractEntityEvent event, User user) {
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
                                otherPlayer.sendMessage(TextComponent.of("The body of " + deadPlayer.getDisplayName() + " has been found!").color(TextColor.BLUE));
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
    public void onZombieDamage(EntityDamageEvent event) {
        if (getPhase().getFeature(GameFeature.class).getZombiePlayerMap().containsKey(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @GameEvent
    public void onEntityBurn(EntityCombustEvent event) {
        if (getPhase().getFeature(GameFeature.class).getZombiePlayerMap().containsKey(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @GameEvent
    public void onZombieTarget(EntityTargetEvent event) {
        if (getPhase().getFeature(GameFeature.class).getZombiePlayerMap().containsKey(event.getEntity())) {
            event.setTarget(null);
        }
    }
}

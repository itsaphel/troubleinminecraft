package io.indices.troubleinminecraft.features;

import lombok.Setter;
import me.minidigger.voxelgameslib.event.events.player.PlayerEliminationEvent;
import me.minidigger.voxelgameslib.feature.AbstractFeature;
import me.minidigger.voxelgameslib.feature.features.ScoreboardFeature;
import me.minidigger.voxelgameslib.scoreboard.Scoreboard;
import me.minidigger.voxelgameslib.user.UserHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import javax.inject.Inject;

public class GameFeature extends AbstractFeature {

    @Inject
    private UserHandler userHandler;

    @Setter
    private Scoreboard scoreboard;

    @Override
    public void init() {

    }

    @Override
    public void start() {
        // randomly assign classes
    }

    @Override
    public void stop() {

    }

    @Override
    public void tick() {

    }

    @Override
    public Class[] getDependencies() {
        return new Class[]{ScoreboardFeature.class};
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        userHandler.getUser(event.getEntity().getUniqueId()).ifPresent(user -> {
            if(getPhase().getGame().getPlayers().contains(user)) {
                Bukkit.getPluginManager().callEvent(new PlayerEliminationEvent(user, getPhase().getGame()));
                getPhase().getGame().leave(user);
                getPhase().getGame().spectate(user);
            }
        });
    }
}

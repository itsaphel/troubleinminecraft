package io.indices.troubleinminecraft.features;

import com.voxelgameslib.voxelgameslib.feature.AbstractFeature;
import com.voxelgameslib.voxelgameslib.feature.features.PersonalScoreboardFeature;
import com.voxelgameslib.voxelgameslib.phase.TimedPhase;

import org.bukkit.ChatColor;

import lombok.Setter;

public class PreGameFeature extends AbstractFeature {

    @Setter
    private PersonalScoreboardFeature.GlobalScoreboard scoreboard;

    @Override
    public void init() {

    }

    @Override
    public void start() {
        if (getPhase() instanceof TimedPhase) {
            getPhase().getGame().getPlayers().forEach(user -> user.getPlayer().sendTitle(ChatColor.RED.toString() + (((TimedPhase) getPhase()).getTicks() / 20) + " seconds", "to find a weapon!"));
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void tick() {

    }

    @Override
    public Class[] getDependencies() {
        return new Class[]{PersonalScoreboardFeature.class, GameFeature.class};
    }
}

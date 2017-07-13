package io.indices.troubleinminecraft.features;

import lombok.Setter;
import me.minidigger.voxelgameslib.feature.AbstractFeature;
import me.minidigger.voxelgameslib.feature.features.ScoreboardFeature;
import me.minidigger.voxelgameslib.scoreboard.BukkitStringScoreboardLine;
import me.minidigger.voxelgameslib.scoreboard.Scoreboard;
import org.bukkit.ChatColor;

public class PreGameFeature extends AbstractFeature {

    @Setter
    private Scoreboard scoreboard;

    @Override
    public void init() {

    }

    @Override
    public void start() {
        scoreboard.getLine(8).ifPresent(line -> line.setValue(ChatColor.MAGIC + "????????"));
    }

    @Override
    public void stop() {

    }

    @Override
    public void tick() {

    }

    @Override
    public Class[] getDependencies() {
        return new Class[]{ScoreboardFeature.class, GameFeature.class};
    }
}

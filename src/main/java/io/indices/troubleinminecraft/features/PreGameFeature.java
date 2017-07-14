package io.indices.troubleinminecraft.features;

import lombok.Setter;
import me.minidigger.voxelgameslib.feature.AbstractFeature;
import me.minidigger.voxelgameslib.feature.features.PersonalScoreboardFeature;
import org.bukkit.ChatColor;

public class PreGameFeature extends AbstractFeature {

    @Setter
    private PersonalScoreboardFeature.GlobalScoreboard scoreboard;

    @Override
    public void init() {

    }

    @Override
    public void start() {
        scoreboard = getPhase().getFeature(PersonalScoreboardFeature.class).getGlobalScoreboard();
        scoreboard.getLines(8).forEach(line -> line.setValue(ChatColor.MAGIC + "????????"));
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

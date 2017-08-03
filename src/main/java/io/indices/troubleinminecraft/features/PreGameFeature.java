package io.indices.troubleinminecraft.features;

import lombok.Setter;
import com.voxelgameslib.voxelgameslib.feature.AbstractFeature;
import com.voxelgameslib.voxelgameslib.feature.features.PersonalScoreboardFeature;
import org.bukkit.ChatColor;

public class PreGameFeature extends AbstractFeature {

    @Setter
    private PersonalScoreboardFeature.GlobalScoreboard scoreboard;

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
    public Class[] getDependencies() {
        return new Class[]{PersonalScoreboardFeature.class, GameFeature.class};
    }
}

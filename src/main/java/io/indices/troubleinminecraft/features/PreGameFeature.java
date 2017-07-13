package io.indices.troubleinminecraft.features;

import lombok.Setter;
import me.minidigger.voxelgameslib.feature.AbstractFeature;
import me.minidigger.voxelgameslib.feature.features.ScoreboardFeature;
import me.minidigger.voxelgameslib.scoreboard.Scoreboard;

public class PreGameFeature extends AbstractFeature {

    @Setter
    private Scoreboard scoreboard;

    @Override
    public void init() {

    }

    @Override
    public void start() {
        scoreboard.createAndAddLine("role", "???");
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

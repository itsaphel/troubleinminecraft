package io.indices.troubleinminecraft.phases;

import io.indices.troubleinminecraft.features.GameFeature;
import me.minidigger.voxelgameslib.GameConstants;
import me.minidigger.voxelgameslib.feature.features.*;
import me.minidigger.voxelgameslib.phase.AbstractPhase;
import me.minidigger.voxelgameslib.phase.TimedPhase;
import org.bukkit.GameMode;

public class ActivePhase extends TimedPhase {

    @Override
    public void init() {
        setName("ActivePhase");
        super.init();
        setAllowJoin(false);
        setAllowSpectate(true);
        setTicks(120 * GameConstants.TPS);

        MapFeature mapFeature = getGame().createFeature(MapFeature.class, this);
        mapFeature.setShouldUnload(false);
        addFeature(mapFeature);

        SpawnFeature spawnFeature = getGame().createFeature(SpawnFeature.class, this);
        addFeature(spawnFeature);

        ScoreboardFeature scoreboardFeature = getGame().createFeature(ScoreboardFeature.class, this);
        addFeature(scoreboardFeature);

        NoBlockBreakFeature noBlockBreakFeature = getGame()
                .createFeature(NoBlockBreakFeature.class, this);
        addFeature(noBlockBreakFeature);

        NoBlockPlaceFeature noBlockPlaceFeature = getGame()
                .createFeature(NoBlockPlaceFeature.class, this);
        addFeature(noBlockPlaceFeature);

        HealFeature healFeature = getGame().createFeature(HealFeature.class, this);
        addFeature(healFeature);

        GameModeFeature gameModeFeature = getGame().createFeature(GameModeFeature.class, this);
        gameModeFeature.setGameMode(GameMode.SURVIVAL);
        addFeature(gameModeFeature);

        GameFeature gameFeature = getGame().createFeature(GameFeature.class, this);
        gameFeature.setScoreboard(scoreboardFeature.getScoreboard());
        addFeature(gameFeature);
    }
}

package io.indices.troubleinminecraft.phases;

import io.indices.troubleinminecraft.features.GameFeature;
import com.voxelgameslib.voxelgameslib.feature.features.*;
import com.voxelgameslib.voxelgameslib.phase.AbstractPhase;
import org.bukkit.GameMode;

public class ActivePhase extends AbstractPhase {

    @Override
    public void init() {
        setName("ActivePhase");
        super.init();
        setAllowJoin(false);
        setAllowSpectate(true);

        MapFeature mapFeature = getGame().createFeature(MapFeature.class, this);
        mapFeature.setShouldUnload(false);
        addFeature(mapFeature);

        SpawnFeature spawnFeature = getGame().createFeature(SpawnFeature.class, this);
        spawnFeature.setInitialSpawn(false);
        spawnFeature.setRespawn(false);
        addFeature(spawnFeature);

        PersonalScoreboardFeature scoreboardFeature = getGame().createFeature(PersonalScoreboardFeature.class, this);
        addFeature(scoreboardFeature);

        NoBlockBreakFeature noBlockBreakFeature = getGame().createFeature(NoBlockBreakFeature.class, this);
        addFeature(noBlockBreakFeature);

        NoBlockPlaceFeature noBlockPlaceFeature = getGame().createFeature(NoBlockPlaceFeature.class, this);
        addFeature(noBlockPlaceFeature);

        NoHungerLossFeature noHungerLossFeature = getGame().createFeature(NoHungerLossFeature.class, this);
        addFeature(noHungerLossFeature);

        HealFeature healFeature = getGame().createFeature(HealFeature.class, this);
        addFeature(healFeature);

        GameModeFeature gameModeFeature = getGame().createFeature(GameModeFeature.class, this);
        gameModeFeature.setGameMode(GameMode.SURVIVAL);
        addFeature(gameModeFeature);

        GameFeature gameFeature = getGame().createFeature(GameFeature.class, this);
        addFeature(gameFeature);
    }
}

package io.indices.troubleinminecraft.phases;

import io.indices.troubleinminecraft.features.GameFeature;
import me.minidigger.voxelgameslib.GameConstants;
import me.minidigger.voxelgameslib.feature.features.*;
import me.minidigger.voxelgameslib.phase.TimedPhase;
import org.bukkit.GameMode;

public class GracePhase extends TimedPhase {

    @Override
    public void init() {
        setName("GracePhase");
        super.init();
        setAllowJoin(false);
        setAllowSpectate(true);
        setTicks(30 * GameConstants.TPS); // 30 seconds

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

        ClearInventoryFeature clearInventoryFeature = getGame()
                .createFeature(ClearInventoryFeature.class, this);
        addFeature(clearInventoryFeature);

        NoDamageFeature noDamageFeature = getGame().createFeature(NoDamageFeature.class, this);
        addFeature(noDamageFeature);

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

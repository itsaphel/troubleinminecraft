package io.indices.troubleinminecraft.phases;

import com.voxelgameslib.voxelgameslib.api.gameConstants;
import com.voxelgameslib.voxelgameslib.api.feature.features.ClearInventoryFeature;
import com.voxelgameslib.voxelgameslib.api.feature.features.GameModeFeature;
import com.voxelgameslib.voxelgameslib.api.feature.features.HealFeature;
import com.voxelgameslib.voxelgameslib.api.feature.features.MapFeature;
import com.voxelgameslib.voxelgameslib.api.feature.features.NoBlockBreakFeature;
import com.voxelgameslib.voxelgameslib.api.feature.features.NoBlockPlaceFeature;
import com.voxelgameslib.voxelgameslib.api.feature.features.NoDamageFeature;
import com.voxelgameslib.voxelgameslib.api.feature.features.PersonalScoreboardFeature;
import com.voxelgameslib.voxelgameslib.api.feature.features.SpawnFeature;
import com.voxelgameslib.voxelgameslib.api.phase.TimedPhase;
import io.indices.troubleinminecraft.features.ChestItemPickupFeature;
import io.indices.troubleinminecraft.features.GameFeature;
import io.indices.troubleinminecraft.features.PreGameFeature;
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

        PersonalScoreboardFeature scoreboardFeature = getGame().createFeature(PersonalScoreboardFeature.class, this);
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

        ChestItemPickupFeature chestItemPickupFeature = getGame().createFeature(ChestItemPickupFeature.class, this);
        addFeature(chestItemPickupFeature);

        GameFeature gameFeature = getGame().createFeature(GameFeature.class, this);
        addFeature(gameFeature);

        PreGameFeature preGameFeature = getGame().createFeature(PreGameFeature.class, this);
        addFeature(preGameFeature);
    }
}

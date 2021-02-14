package io.indices.troubleinminecraft.phases;

import com.voxelgameslib.voxelgameslib.GameConstants;
import com.voxelgameslib.voxelgameslib.api.feature.features.GameModeFeature;
import com.voxelgameslib.voxelgameslib.api.feature.features.MapFeature;
import com.voxelgameslib.voxelgameslib.api.feature.features.NoBlockBreakFeature;
import com.voxelgameslib.voxelgameslib.api.feature.features.NoBlockPlaceFeature;
import com.voxelgameslib.voxelgameslib.api.feature.features.NoDamageFeature;
import com.voxelgameslib.voxelgameslib.api.feature.features.PersonalScoreboardFeature;
import com.voxelgameslib.voxelgameslib.api.feature.features.SpawnFeature;
import com.voxelgameslib.voxelgameslib.api.phase.TimedPhase;
import io.indices.troubleinminecraft.features.DeadBodiesFeature;
import io.indices.troubleinminecraft.features.PostGameFeature;
import org.bukkit.GameMode;

public class PostGamePhase extends TimedPhase {

    @Override
    public void init() {
        setName("PostGamePhase");
        super.init();
        setAllowJoin(false);
        setAllowSpectate(true);
        setTicks(20 * GameConstants.TPS); // 20 seconds

        MapFeature mapFeature = getGame().createFeature(MapFeature.class, this);
        mapFeature.setShouldUnload(false);
        addFeature(mapFeature);

        SpawnFeature spawnFeature = getGame().createFeature(SpawnFeature.class, this);
        spawnFeature.setInitialSpawn(false);
        addFeature(spawnFeature);

        PersonalScoreboardFeature scoreboardFeature = getGame().createFeature(PersonalScoreboardFeature.class, this);
        addFeature(scoreboardFeature);

        NoBlockBreakFeature noBlockBreakFeature = getGame()
                .createFeature(NoBlockBreakFeature.class, this);
        addFeature(noBlockBreakFeature);

        NoBlockPlaceFeature noBlockPlaceFeature = getGame()
                .createFeature(NoBlockPlaceFeature.class, this);
        addFeature(noBlockPlaceFeature);

        NoDamageFeature noDamageFeature = getGame().createFeature(NoDamageFeature.class, this);
        addFeature(noDamageFeature);

        GameModeFeature gameModeFeature = getGame().createFeature(GameModeFeature.class, this);
        gameModeFeature.setGameMode(GameMode.ADVENTURE);
        addFeature(gameModeFeature);

        PostGameFeature postGameFeature = getGame().createFeature(PostGameFeature.class, this);
        addFeature(postGameFeature);

        DeadBodiesFeature deadBodiesFeature = getGame().createFeature(DeadBodiesFeature.class, this);
        addFeature(deadBodiesFeature);
    }
}

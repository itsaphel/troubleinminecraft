package io.indices.troubleinminecraft;

import io.indices.troubleinminecraft.phases.ActivePhase;
import io.indices.troubleinminecraft.phases.GracePhase;
import io.indices.troubleinminecraft.phases.PostGamePhase;
import com.voxelgameslib.voxelgameslib.feature.features.VoteFeature;
import com.voxelgameslib.voxelgameslib.game.AbstractGame;
import com.voxelgameslib.voxelgameslib.game.GameDefinition;
import com.voxelgameslib.voxelgameslib.game.GameInfo;
import com.voxelgameslib.voxelgameslib.phase.phases.LobbyWithVotePhase;

import javax.annotation.Nonnull;

@GameInfo(name = "TroubleInMinecraft", author = "aphel", version = "1.0", description = "Trouble in Terrorist Town, in Minecraft")
public class TroubleInMinecraftGame extends AbstractGame {

    public TroubleInMinecraftGame() {
        super(TroubleInMinecraftPlugin.GAMEMODE);
    }

    @Override
    public void initGameFromModule() {
        setMinPlayers(2);
        setMaxPlayers(2);

        LobbyWithVotePhase lobbyPhase = createPhase(LobbyWithVotePhase.class);
        GracePhase gracePhase = createPhase(GracePhase.class);
        ActivePhase mainPhase = createPhase(ActivePhase.class);
        PostGamePhase postGamePhase = createPhase(PostGamePhase.class);

        lobbyPhase.getFeature(VoteFeature.class).setEnableVoteMenu(true);

        lobbyPhase.setNextPhase(gracePhase);
        gracePhase.setNextPhase(mainPhase);
        mainPhase.setNextPhase(postGamePhase);

        activePhase = lobbyPhase;

        loadMap();
    }

    @Override
    public void initGameFromDefinition(@Nonnull GameDefinition gameDefinition) {
        super.initGameFromDefinition(gameDefinition);

        loadMap();
    }
}

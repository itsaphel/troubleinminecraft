package io.indices.troubleinminecraft.features;

import io.indices.troubleinminecraft.team.Role;
import io.indices.troubleinminecraft.util.ChatUtils;
import lombok.Setter;
import me.minidigger.voxelgameslib.feature.AbstractFeature;
import me.minidigger.voxelgameslib.feature.features.ScoreboardFeature;
import me.minidigger.voxelgameslib.phase.TimedPhase;
import me.minidigger.voxelgameslib.scoreboard.Scoreboard;
import net.kyori.text.TextComponent;

public class PostGameFeature extends AbstractFeature {

    @Setter
    private Scoreboard scoreboard;

    @Override
    public void init() {

    }

    @Override
    public void start() {
        Object winner = getPhase().getGame().getGameData("winner");
        if (winner != null && winner instanceof Role) {
            getPhase().getGame().getAllUsers().forEach(user -> user.sendMessage(TextComponent.of(ChatUtils.formatRoleName(((Role) winner))+ " win!")));
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void tick() {
        if(((TimedPhase) getPhase()).getTicks() == 1) {
            // todo, a team for each role
            getPhase().getGame().endGame(null, null);
        }
    }

    @Override
    public Class[] getDependencies() {
        return new Class[]{ScoreboardFeature.class};
    }
}

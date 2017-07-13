package io.indices.troubleinminecraft.features;

import lombok.Setter;
import me.minidigger.voxelgameslib.feature.AbstractFeature;
import me.minidigger.voxelgameslib.feature.features.ScoreboardFeature;
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
        if (winner != null && winner instanceof Enum) {
            getPhase().getGame().getAllUsers().forEach(user -> user.sendMessage(TextComponent.of(((Enum) winner).toString() + " win!")));
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void tick() {

    }

    @Override
    public Class[] getDependencies() {
        return new Class[]{ScoreboardFeature.class};
    }
}

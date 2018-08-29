package io.indices.troubleinminecraft.features;

import com.voxelgameslib.voxelgameslib.feature.AbstractFeature;
import com.voxelgameslib.voxelgameslib.feature.Feature;
import com.voxelgameslib.voxelgameslib.feature.features.PersonalScoreboardFeature;
import com.voxelgameslib.voxelgameslib.phase.TimedPhase;
import lombok.Setter;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class PreGameFeature extends AbstractFeature {

    @Setter
    private PersonalScoreboardFeature.GlobalScoreboard scoreboard;

    @Override
    public void enable() {
        if (getPhase() instanceof TimedPhase) {
            getPhase().getGame().getPlayers().forEach(user -> user.getPlayer().sendTitle(ChatColor.RED.toString() + (((TimedPhase) getPhase()).getTicks() / 20) + " seconds", "to find a weapon!"));
        }
    }

    @Override
    @Nonnull
    public List<Class<? extends Feature>> getDependencies() {
        List<Class<? extends Feature>> list = new ArrayList<>();

        list.add(PersonalScoreboardFeature.class);
        list.add(GameFeature.class);

        return list;
    }
}

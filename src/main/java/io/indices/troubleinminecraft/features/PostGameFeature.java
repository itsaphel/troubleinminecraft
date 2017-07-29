package io.indices.troubleinminecraft.features;

import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import me.minidigger.voxelgameslib.components.scoreboard.Scoreboard;
import me.minidigger.voxelgameslib.components.team.Team;
import me.minidigger.voxelgameslib.feature.AbstractFeature;
import me.minidigger.voxelgameslib.feature.features.ScoreboardFeature;
import me.minidigger.voxelgameslib.phase.TimedPhase;
import me.minidigger.voxelgameslib.user.User;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;

import io.indices.troubleinminecraft.TroubleInMinecraftPlugin;
import io.indices.troubleinminecraft.game.ChatUtils;
import io.indices.troubleinminecraft.game.DeadPlayer;
import io.indices.troubleinminecraft.game.TIMData;
import io.indices.troubleinminecraft.team.Innocent;
import io.indices.troubleinminecraft.team.Role;
import io.indices.troubleinminecraft.team.Traitor;
import lombok.Setter;

public class PostGameFeature extends AbstractFeature {

    @Setter
    private Scoreboard scoreboard;

    private Map<Entity, DeadPlayer> zombiePlayerMap = new HashMap<>();
    private Role winner;

    @Override
    public void init() {

    }

    @Override
    public void start() {
        TIMData timData = getPhase().getGame().getGameData(TIMData.class).orElse(new TIMData());
        // just to keep events going
        zombiePlayerMap = timData.getZombiePlayerMap();

        if (timData.getWinner() != null ) {
            this.winner = timData.getWinner();
            TextColor textColor;

            if (this.winner == Role.TRAITOR) {
                textColor = Role.TRAITOR.getTextColour();
            } else {
                textColor = Role.TRAITOR.getTextColour();
            }

            List<User> traitors;
            if (timData.getTraitors() != null) {
                traitors = timData.getTraitors();
            } else {
                traitors = new ArrayList<>();
            }

            String traitorListString = traitors.stream()
                    .map(User::getRawDisplayName)
                    .collect(Collectors.joining(", "));


            getPhase().getGame().getAllUsers().forEach(user -> {
                user.sendMessage(TextComponent.of("The " + ChatUtils.formatRoleName(this.winner) + " have won the game!").color(textColor));
                user.sendMessage(TextComponent.of("The traitors were: ").color(textColor).append(TextComponent.of(traitorListString).color(TextColor.DARK_RED)));
            });
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void tick() {
        if (((TimedPhase) getPhase()).getTicks() == 1) {
            Team winningTeam;
            TIMData timData = getPhase().getGame().getGameData(TIMData.class).orElse(new TIMData());

            if (winner == Role.TRAITOR) {
                winningTeam = new Traitor();
                timData.getTraitors().forEach(traitor -> winningTeam.addPlayer(traitor, traitor.getRating(TroubleInMinecraftPlugin.GAMEMODE)));
            } else {
                winningTeam = new Innocent();
                timData.getInnocents().forEach(innocent -> winningTeam.addPlayer(innocent, innocent.getRating(TroubleInMinecraftPlugin.GAMEMODE)));
                timData.getDetectives().forEach(detective -> winningTeam.addPlayer(detective, detective.getRating(TroubleInMinecraftPlugin.GAMEMODE)));
            }

            getPhase().getGame().endGame(winningTeam, null);
        }
    }

    @Override
    public Class[] getDependencies() {
        return new Class[]{ScoreboardFeature.class};
    }

    @EventHandler
    public void onZombieDamage(EntityDamageEvent event) {
        if (zombiePlayerMap.containsKey(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityBurn(EntityCombustEvent event) {
        if (zombiePlayerMap.containsKey(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onZombieTarget(EntityTargetEvent event) {
        if (zombiePlayerMap.containsKey(event.getEntity())) {
            event.setTarget(null);
        }
    }
}

package io.indices.troubleinminecraft.features;

import com.google.gson.annotations.Expose;
import com.google.inject.Injector;

import net.kyori.text.LegacyComponent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import com.voxelgameslib.voxelgameslib.components.scoreboard.Scoreboard;
import com.voxelgameslib.voxelgameslib.components.team.Team;
import com.voxelgameslib.voxelgameslib.event.GameEvent;
import com.voxelgameslib.voxelgameslib.event.events.player.PlayerEliminationEvent;
import com.voxelgameslib.voxelgameslib.feature.AbstractFeature;
import com.voxelgameslib.voxelgameslib.feature.features.MapFeature;
import com.voxelgameslib.voxelgameslib.feature.features.PersonalScoreboardFeature;
import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.phase.TimedPhase;
import com.voxelgameslib.voxelgameslib.user.User;
import com.voxelgameslib.voxelgameslib.user.UserHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.indices.troubleinminecraft.game.ChatUtils;
import io.indices.troubleinminecraft.game.DeadPlayer;
import io.indices.troubleinminecraft.game.TIMData;
import io.indices.troubleinminecraft.game.TIMPlayer;
import io.indices.troubleinminecraft.lang.TIMLangKey;
import io.indices.troubleinminecraft.phases.ActivePhase;
import io.indices.troubleinminecraft.shop.ShopRegistry;
import io.indices.troubleinminecraft.team.Role;

public class GameFeature extends AbstractFeature {
    @Inject
    private Injector injector;
    @Inject
    private UserHandler userHandler;

    private PersonalScoreboardFeature.GlobalScoreboard globalScoreboard;

    private Team innocents;
    private Team traitors;
    private Team detectives;

    private List<User> aliveInnocents = new ArrayList<>(); // detectives are classed as innocents for these purposes
    private List<User> aliveTraitors = new ArrayList<>();

    private Map<User, TIMPlayer> playerMap = new HashMap<>();
    private Map<Entity, DeadPlayer> zombiePlayerMap = new HashMap<>();

    private int visiblePlayersLeft;
    private boolean notifiedPlayers = false;

    @Expose
    private int innocentKillTraitorKarma = 20;
    @Expose
    private int innocentKillInnocentKarma = -20;
    @Expose
    private int innocentKillDetectiveKarma = -40;
    @Expose
    private int detectiveKillTraitorKarma = 20;
    @Expose
    private int detectiveKillInnocentKarma = -20;
    @Expose
    private int detectiveKillDetectiveKarma = -40;
    @Expose
    private int traitorKillDetectiveKarma = 40;
    @Expose
    private int traitorKillInnocentKarma = 20;
    @Expose
    private int traitorKillTraitorKarma = -40;

    @Override
    public void start() {
        // randomly assign classes
        TIMData timData = getPhase().getGame().getGameData(TIMData.class).orElse(new TIMData());
        boolean gameStarted = timData.isGameStarted();

        if (!gameStarted) {
            // initialise game
            ShopRegistry shopRegistry = injector.getInstance(ShopRegistry.class);
            shopRegistry.register(getPhase().getGame());
            timData.setShopRegistry(shopRegistry);

            createPlayers();
            assignRoles();
        } else {
            innocents = timData.getInnocents();
            traitors = timData.getTraitors();
            detectives = timData.getDetectives();
            aliveInnocents = timData.getAliveInnocents();
            aliveTraitors = timData.getAliveTraitors();
            playerMap = timData.getPlayerMap();
        }

        visiblePlayersLeft = getPhase().getGame().getPlayers().size();

        // we have to do this each time the feature is loaded
        initScoreboard();

        if (gameStarted) {
            notifyRoles();
        }

        timData.setGameStarted(true);
        getPhase().getGame().putGameData(timData);
    }

    @Override
    public void stop() {
        TIMData timData = getPhase().getGame().getGameData(TIMData.class).orElse(new TIMData());
        timData.setInnocents(innocents);
        timData.setDetectives(detectives);
        timData.setTraitors(traitors);
        timData.setAliveInnocents(aliveInnocents);
        timData.setAliveTraitors(aliveTraitors);
        timData.setZombiePlayerMap(zombiePlayerMap);
        timData.setPlayerMap(playerMap);
        getPhase().getGame().putGameData(timData);
    }

    @Override
    public void tick() {
        if (notifiedPlayers) {
            traitors.getPlayers().forEach(this::updateCredits);
            detectives.getPlayers().forEach(this::updateCredits);
        }

        if (getPhase() instanceof TimedPhase && getPhase() instanceof ActivePhase) {
            if (((TimedPhase) getPhase()).getTicks() == 1) {
                // time ran out
                setWinner(Role.INNOCENT);
                getPhase().getGame().getAllUsers().forEach(user -> Lang.msg(user, TIMLangKey.TIME_RAN_OUT_INNOCENTS_WIN));
                getPhase().getGame().endPhase();
            }
        }
    }

    @Override
    @Nonnull
    public Class[] getDependencies() {
        return new Class[]{PersonalScoreboardFeature.class, MapFeature.class};
    }

    @Nullable
    public Role getRole(@Nonnull User user) {
        if (traitors.contains(user)) {
            return Role.TRAITOR;
        } else if (detectives.contains(user)) {
            return Role.DETECTIVE;
        } else if (innocents.contains(user)) {
            return Role.INNOCENT;
        } else {
            return null;
        }
    }

    @Nonnull
    public Map<Entity, DeadPlayer> getZombiePlayerMap() {
        return zombiePlayerMap;
    }

    public int getVisiblePlayersLeft() {
        return visiblePlayersLeft;
    }

    public void decrementVisiblePlayersLeft() {
        visiblePlayersLeft--;
    }

    /**
     * Create players
     */
    private void createPlayers() {
        getPhase().getGame().getPlayers().forEach(user -> playerMap.put(user, new TIMPlayer(user)));
    }

    /**
     * Initialise scoreboard
     */
    private void initScoreboard() {
        globalScoreboard = getPhase().getFeature(PersonalScoreboardFeature.class).getGlobalScoreboard();

        globalScoreboard.setTitle(LegacyComponent.to(Lang.trans(TIMLangKey.SCOREBOARD_TIM)));

        // read this upside down ;) scoreboards suck

        globalScoreboard.createAndAddLine("karma", "1000");
        globalScoreboard.createAndAddLine(LegacyComponent.to(Lang.trans(TIMLangKey.SCOREBOARD_KARMA)));

        globalScoreboard.createAndAddLine(ChatColor.RESET + ChatColor.RESET.toString() + ChatColor.RESET.toString() + "");

        globalScoreboard.createAndAddLine("kills", "0");
        globalScoreboard.createAndAddLine(LegacyComponent.to(Lang.trans(TIMLangKey.SCOREBOARD_KILLS)));

        globalScoreboard.createAndAddLine(ChatColor.RESET + ChatColor.RESET.toString() + "");

        globalScoreboard.createAndAddLine("players-left", visiblePlayersLeft + "");
        globalScoreboard.createAndAddLine(LegacyComponent.to(Lang.trans(TIMLangKey.SCOREBOARD_PLAYERS_LEFT)));

        globalScoreboard.createAndAddLine(ChatColor.RESET + "");

        globalScoreboard.createAndAddLine("role", ChatColor.MAGIC + "????????");
        globalScoreboard.createAndAddLine(LegacyComponent.to(Lang.trans(TIMLangKey.SCOREBOARD_ROLE)));

        globalScoreboard.createAndAddLine(ChatColor.RESET + ChatColor.RESET.toString() + ChatColor.RESET.toString() + "");

        // initialise player-specific variables

        getPhase().getGame().getPlayers().forEach(user -> {
            TIMPlayer player = playerMap.get(user);
            Scoreboard scoreboard = getPhase().getFeature(PersonalScoreboardFeature.class).getScoreboardForUser(user);

            scoreboard.getLine("karma").ifPresent(line -> line.setValue(player.getKarma() + ""));
        });
    }

    /**
     * Choose innocents, traitors and detectives. Do not notify them in this method, as this is called in the
     * GracePhase.
     */
    private void assignRoles() {
        int playerCount = getPhase().getGame().getPlayers().size();
        int traitorAmount = (playerCount / 4) + 1; // 1 traitor per 4 players, follows the TTT spec in gmod
        int detectiveAmount = (playerCount / 8); // 1 detective each 8 players, follows the TTT spec in gmod

        innocents = new Team(playerCount - traitorAmount - detectiveAmount, Role.INNOCENT.getName(), Role.INNOCENT.getColour(), getPhase().getGame());
        traitors = new Team(traitorAmount, Role.TRAITOR.getName(), Role.TRAITOR.getColour(), getPhase().getGame());
        detectives = new Team(detectiveAmount, Role.DETECTIVE.getName(), Role.DETECTIVE.getColour(), getPhase().getGame());

        for (int i = 0; i < traitorAmount; i++) {
            int n = ThreadLocalRandom.current().nextInt(playerCount);

            User traitor = getPhase().getGame().getPlayers().get(n);

            if (traitors.contains(traitor)) {
                // choose again
                i--;
            } else {
                traitors.join(traitor, traitor.getRating(getPhase().getGame().getGameMode()));
                TIMPlayer timPlayer = playerMap.get(traitor);
                timPlayer.setRole(Role.TRAITOR);
                timPlayer.setCredits(50);
            }
        }

        for (int i = 0; i < detectiveAmount; i++) {
            int n = ThreadLocalRandom.current().nextInt(playerCount - 1);

            User detective = getPhase().getGame().getPlayers().get(n);

            if (traitors.contains(detective) || detectives.contains(detective)) {
                // choose again
                i--;
            } else {
                detectives.join(detective, detective.getRating(getPhase().getGame().getGameMode()));
                TIMPlayer timPlayer = playerMap.get(detective);
                timPlayer.setRole(Role.DETECTIVE);
                timPlayer.setCredits(1);
            }
        }

        getPhase().getGame().getPlayers().stream().filter(u -> !traitors.contains(u) && !detectives.contains(u)).forEach(innocent -> {
            innocents.join(innocent, innocent.getRating(getPhase().getGame().getGameMode()));
            playerMap.get(innocent).setRole(Role.INNOCENT);
        });
    }

    /**
     * Notifies the roles of their role
     */
    private void notifyRoles() {
        aliveTraitors.addAll(traitors.getPlayers());
        aliveInnocents.addAll(detectives.getPlayers());
        aliveInnocents.addAll(innocents.getPlayers());

        traitors.getPlayers().forEach(user -> {
            getPhase().getFeature(PersonalScoreboardFeature.class).getScoreboardForUser(user).getLine("role").ifPresent(line -> line.setValue(ChatUtils.formatRoleName(Role.TRAITOR, true)));

            String traitorListString = traitors.getPlayers().stream()
                    .filter(u -> !u.getUuid().equals(user.getUuid()))
                    .map(User::getRawDisplayName)
                    .collect(Collectors.joining(", "));

            Lang.msg(user, TIMLangKey.YOU_ARE_A_TRAITOR);

            if (traitorListString != null && !traitorListString.isEmpty() && traitors.getPlayers().size() >= 2) {
                Lang.msg(user, TIMLangKey.YOUR_FELLOW_TRAITORS_ARE, traitorListString);
            }
        });

        detectives.getPlayers().forEach(user -> {
            getPhase().getFeature(PersonalScoreboardFeature.class).getScoreboardForUser(user).getLine("role").ifPresent(line -> line.setValue(ChatUtils.formatRoleName(Role.DETECTIVE, true)));

            String detectiveListString = detectives.getPlayers().stream()
                    .filter(u -> !u.getUuid().equals(user.getUuid()))
                    .map(User::getRawDisplayName)
                    .collect(Collectors.joining(", "));

            Lang.msg(user, TIMLangKey.YOU_ARE_A_DETECTIVE);

            if (detectiveListString != null && !detectiveListString.isEmpty() && detectives.getPlayers().size() >= 2) {
                Lang.msg(user, TIMLangKey.YOUR_FELLOW_DETECTIVES_ARE, detectiveListString);
            }
        });

        innocents.getPlayers().forEach(user -> {
            getPhase().getFeature(PersonalScoreboardFeature.class).getScoreboardForUser(user).getLine("role").ifPresent(line -> line.setValue(ChatUtils.formatRoleName(Role.INNOCENT, true)));
            Lang.msg(user, TIMLangKey.YOU_ARE_AN_INNOCENT);

            if (detectives.getPlayers().size() != 0) {
                String detectiveListString = detectives.getPlayers().stream()
                        .map(User::getRawDisplayName)
                        .collect(Collectors.joining(", "));

                Lang.msg(user, TIMLangKey.YOUR_DETECTIVES_ARE, detectiveListString);
            }
        });

        notifiedPlayers = true;
    }

    /**
     * Set a user's credits
     *
     * @param user user to adjust credits for
     */
    private void updateCredits(@Nonnull User user) {
        TIMPlayer timPlayer = playerMap.get(user);

        user.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(LegacyComponent.to(Lang.transVar(TIMLangKey.ACTION_BAR_CREDITS, timPlayer.getCredits()))).create());
    }

    /**
     * Wrapper to alter the karma for a player
     *
     * @param player player to alter for
     * @param change change to make (positive or negative)
     */
    private void updateKarma(@Nonnull TIMPlayer player, int change) {
        player.setKarma(player.getKarma() + change);
    }

    /**
     * Set the winner of the game (for the next Phase)
     *
     * @param winner the winning role (either innocent or traitor, detectives are innocents for all intents and
     *               purposes)
     */
    private void setWinner(@Nonnull Role winner) {
        TIMData data = getPhase().getGame().getGameData(TIMData.class).orElse(new TIMData());
        data.setWinner(winner);
        getPhase().getGame().putGameData(data);
    }

    @SuppressWarnings("Duplicates")
    @GameEvent
    public void onDeath(@Nonnull PlayerDeathEvent event, @Nonnull User user) {
        event.setDeathMessage(null);

        Bukkit.getPluginManager().callEvent(new PlayerEliminationEvent(user, getPhase().getGame()));

        user.getPlayer().spigot().respawn(); // prevents a glitch where they are teleported while dead
        //getPhase().getGame().leave(user);
        getPhase().getGame().spectate(user);

        TIMPlayer player = playerMap.get(user);

        // put a mob there
        Zombie zombie = getPhase().getFeature(DeadBodiesFeature.class).spawnBody(event.getEntity().getLocation());
        DeadPlayer deadPlayer = new DeadPlayer();
        deadPlayer.setDisplayName(user.getRawDisplayName());
        deadPlayer.setIdentified(false);
        deadPlayer.setRole(getRole(user));
        deadPlayer.setUuid(user.getUuid());
        zombiePlayerMap.put(zombie, deadPlayer);

        if (event.getEntity().getKiller() != null) {
            userHandler.getUser(event.getEntity().getKiller().getUniqueId()).ifPresent(killer -> {
                if (getPhase().getGame().getPlayers().contains(killer)) {
                    TIMPlayer killerPlayer = playerMap.get(killer);
                    killerPlayer.setKills(killerPlayer.getKills() + 1);

                    // award karma to the killing player
                    // let's see if you've been a naughty boy (or girl) -- santa's bad list incoming (somewhat relevant: https://xkcd.com/838/ (p.s. not really relevant))

                    if (killerPlayer.getRole() == Role.INNOCENT) {
                        switch (player.getRole()) {
                            case TRAITOR:
                                updateKarma(killerPlayer, innocentKillTraitorKarma);
                                break;
                            case INNOCENT:
                                updateKarma(killerPlayer, innocentKillInnocentKarma);
                                break;
                            case DETECTIVE:
                                updateKarma(killerPlayer, innocentKillDetectiveKarma);
                                break;
                        }
                    } else if (killerPlayer.getRole() == Role.DETECTIVE) {
                        switch (player.getRole()) {
                            case TRAITOR:
                                updateKarma(killerPlayer, detectiveKillTraitorKarma);
                                break;
                            case INNOCENT:
                                updateKarma(killerPlayer, detectiveKillInnocentKarma);
                                break;
                            case DETECTIVE:
                                updateKarma(killerPlayer, detectiveKillDetectiveKarma);
                                break;
                        }
                    } else if (killerPlayer.getRole() == Role.TRAITOR) {
                        switch (player.getRole()) {
                            case TRAITOR:
                                updateKarma(killerPlayer, traitorKillTraitorKarma);
                                break;
                            case INNOCENT:
                                player.setCredits(player.getCredits() + 1);
                                updateKarma(killerPlayer, traitorKillInnocentKarma);
                                break;
                            case DETECTIVE:
                                player.setCredits(player.getCredits() + 3);
                                updateKarma(killerPlayer, traitorKillDetectiveKarma);
                                break;
                        }
                    }

                    Scoreboard killerScoreboard = getPhase().getFeature(PersonalScoreboardFeature.class).getScoreboardForUser(killer);

                    killerScoreboard.getLine("kills").ifPresent(line -> {
                        line.setValue(killerPlayer.getKills() + "");
                    });

                    killerScoreboard.getLine("karma").ifPresent(line -> {
                        line.setValue(killerPlayer.getKarma() + "");
                    });
                }
            });
        }

        if (traitors.contains(user)) {
            aliveTraitors.remove(user);
            if (aliveTraitors.size() == 0) {
                // innocents win
                setWinner(Role.INNOCENT);
                getPhase().getGame().endPhase();
            }
        } else {
            aliveInnocents.remove(user);
            if (aliveInnocents.size() == 0) {
                // traitors win
                setWinner(Role.TRAITOR);
                getPhase().getGame().endPhase();
            }
        }
    }

    @GameEvent
    public void onQuit(@Nonnull PlayerQuitEvent event, @Nonnull User user) {
        // put a mob there
        Zombie zombie = getPhase().getFeature(DeadBodiesFeature.class).spawnBody(event.getPlayer().getLocation());
        DeadPlayer deadPlayer = new DeadPlayer();
        deadPlayer.setDisplayName(user.getRawDisplayName());
        deadPlayer.setIdentified(false);
        deadPlayer.setRole(getRole(user));
        deadPlayer.setUuid(user.getUuid());
        zombiePlayerMap.put(zombie, deadPlayer);
    }
}

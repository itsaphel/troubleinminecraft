package io.indices.troubleinminecraft.features;

import com.google.gson.annotations.Expose;
import com.voxelgameslib.voxelgameslib.components.scoreboard.Scoreboard;
import com.voxelgameslib.voxelgameslib.components.team.Team;
import com.voxelgameslib.voxelgameslib.event.GameEvent;
import com.voxelgameslib.voxelgameslib.event.events.player.PlayerEliminationEvent;
import com.voxelgameslib.voxelgameslib.feature.AbstractFeature;
import com.voxelgameslib.voxelgameslib.feature.features.MapFeature;
import com.voxelgameslib.voxelgameslib.feature.features.PersonalScoreboardFeature;
import com.voxelgameslib.voxelgameslib.user.User;
import com.voxelgameslib.voxelgameslib.user.UserHandler;
import io.indices.troubleinminecraft.game.ChatUtils;
import io.indices.troubleinminecraft.game.DeadPlayer;
import io.indices.troubleinminecraft.game.TIMData;
import io.indices.troubleinminecraft.game.TIMPlayer;
import io.indices.troubleinminecraft.team.Role;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class GameFeature extends AbstractFeature {
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
    public void init() {

    }

    @Override
    public void start() {
        // randomly assign classes
        TIMData timData = getPhase().getGame().getGameData(TIMData.class).orElse(new TIMData());
        boolean gameStarted = timData.isGameStarted();

        if (!gameStarted) {
            // initialise game
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
    }

    @Override
    public Class[] getDependencies() {
        return new Class[]{PersonalScoreboardFeature.class, MapFeature.class};
    }

    public Role getRole(User user) {
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

        globalScoreboard.setTitle(ChatColor.BLUE + "TIMC");

        // read this upside down ;) scoreboards suck

        globalScoreboard.createAndAddLine("karma", "1000");
        globalScoreboard.createAndAddLine(ChatColor.RED + ChatColor.BOLD.toString() + "Karma");

        globalScoreboard.createAndAddLine(ChatColor.RESET + ChatColor.RESET.toString() + ChatColor.RESET.toString() + "");

        globalScoreboard.createAndAddLine("kills", "0");
        globalScoreboard.createAndAddLine(ChatColor.AQUA + ChatColor.BOLD.toString() + "Kills");

        globalScoreboard.createAndAddLine(ChatColor.RESET + ChatColor.RESET.toString() + "");

        globalScoreboard.createAndAddLine("players-left", visiblePlayersLeft + "");
        globalScoreboard.createAndAddLine(ChatColor.GREEN + ChatColor.BOLD.toString() + "Players left");

        globalScoreboard.createAndAddLine(ChatColor.RESET + "");

        globalScoreboard.createAndAddLine("role", ChatColor.MAGIC + "????????");
        globalScoreboard.createAndAddLine(ChatColor.GOLD + ChatColor.BOLD.toString() + "Role");

        globalScoreboard.createAndAddLine(ChatColor.RESET + ChatColor.RESET.toString() + ChatColor.RESET.toString() + "");

        // initialise player-specific variables

        getPhase().getGame().getPlayers().forEach(user -> {
            TIMPlayer player = playerMap.get(user);
            Scoreboard scoreboard = getPhase().getFeature(PersonalScoreboardFeature.class).getScoreboardForUser(user);

            scoreboard.getLine("karma").ifPresent(line -> line.setValue(player.getKarma() + ""));
        });
    }

    /**
     * Choose innocents, traitors and detectives. Do not notify them in this method, as this is
     * called in the GracePhase.
     */
    private void assignRoles() {
        int playerCount = getPhase().getGame().getPlayers().size();
        int traitorAmount = (playerCount / 4) + 1; // 1 traitor per 4 players, follows the TTT spec in gmod
        int detectiveAmount = (playerCount / 8); // 1 detective each 8 players, follows the TTT spec in gmod

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
                timPlayer.setCredits(1);
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

            user.sendMessage(TextComponent.of("You are a traitor! Work with your fellow traitors to kill the innocents. Watch out for the detectives, they have the tools to get you too.").color(TextColor.RED));

            if (traitorListString != null && !traitorListString.isEmpty() && traitors.getPlayers().size() >= 2) {
                user.sendMessage(TextComponent.of("Your fellow traitors are: ").color(TextColor.RED).append(TextComponent.of(traitorListString).color(TextColor.DARK_RED)));
            }
        });

        detectives.getPlayers().forEach(user -> {
            getPhase().getFeature(PersonalScoreboardFeature.class).getScoreboardForUser(user).getLine("role").ifPresent(line -> line.setValue(ChatUtils.formatRoleName(Role.DETECTIVE, true)));

            String detectiveListString = detectives.getPlayers().stream()
                    .filter(u -> !u.getUuid().equals(user.getUuid()))
                    .map(User::getRawDisplayName)
                    .collect(Collectors.joining(", "));

            user.sendMessage(TextComponent.of("You are a detective! It is your job to save the innocents from the traitors.").color(TextColor.BLUE));

            if (detectiveListString != null && !detectiveListString.isEmpty() && detectives.getPlayers().size() >= 2) {
                user.sendMessage(TextComponent.of("Your fellow detectives are: ").color(TextColor.BLUE).append(TextComponent.of(detectiveListString).color(TextColor.DARK_BLUE)));
            }
        });

        innocents.getPlayers().forEach(user -> {
            getPhase().getFeature(PersonalScoreboardFeature.class).getScoreboardForUser(user).getLine("role").ifPresent(line -> line.setValue(ChatUtils.formatRoleName(Role.INNOCENT, true)));
            user.sendMessage(TextComponent.of("You are an innocent. Find weapons and try to survive against the traitors. Work with the detectives to find and kill them. Stay alert!").color(TextColor.GREEN));

            if (detectives.getPlayers().size() != 0) {
                String detectiveListString = detectives.getPlayers().stream()
                        .map(User::getRawDisplayName)
                        .collect(Collectors.joining(", "));

                user.sendMessage(TextComponent.of("Your detectives are: ").color(TextColor.GREEN).append(TextComponent.of(detectiveListString).color(TextColor.BLUE)));
            }
        });

        notifiedPlayers = true;
    }

    /**
     * Set a user's credits
     *
     * @param user user to adjust credits for
     */
    private void updateCredits(User user) {
        TIMPlayer timPlayer = playerMap.get(user);

        user.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("Credits: " + timPlayer.getCredits()).color(net.md_5.bungee.api.ChatColor.GOLD).create());
    }

    /**
     * Wrapper to alter the karma for a player
     *
     * @param player player to alter for
     * @param change change to make (positive or negative)
     */
    private void updateKarma(TIMPlayer player, int change) {
        player.setKarma(player.getKarma() + change);
    }

    @SuppressWarnings("Duplicates")
    @GameEvent
    public void onDeath(PlayerDeathEvent event, User user) {
        event.setDeathMessage(null);

        Bukkit.getPluginManager().callEvent(new PlayerEliminationEvent(user, getPhase().getGame()));

        user.getPlayer().spigot().respawn(); // prevents a glitch where they are teleported while dead
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
                TIMData data = getPhase().getGame().getGameData(TIMData.class).orElse(new TIMData());
                data.setWinner(Role.INNOCENT);
                getPhase().getGame().putGameData(data);
                getPhase().getGame().endPhase();
            }
        } else {
            aliveInnocents.remove(user);
            if (aliveInnocents.size() == 0) {
                // traitors win
                TIMData data = getPhase().getGame().getGameData(TIMData.class).orElse(new TIMData());
                data.setWinner(Role.TRAITOR);
                getPhase().getGame().putGameData(data);
                getPhase().getGame().endPhase();
            }
        }
    }

    @GameEvent
    public void onQuit(PlayerQuitEvent event, User user) {
        // todo spawn a dead body in their place
        //
    }
}

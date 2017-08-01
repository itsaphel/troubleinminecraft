package io.indices.troubleinminecraft.features;

import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import javax.inject.Inject;

import me.minidigger.voxelgameslib.event.events.player.PlayerEliminationEvent;
import me.minidigger.voxelgameslib.feature.AbstractFeature;
import me.minidigger.voxelgameslib.feature.features.MapFeature;
import me.minidigger.voxelgameslib.feature.features.PersonalScoreboardFeature;
import me.minidigger.voxelgameslib.map.Marker;
import me.minidigger.voxelgameslib.map.Vector3D;
import me.minidigger.voxelgameslib.user.User;
import me.minidigger.voxelgameslib.user.UserHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.indices.troubleinminecraft.game.ChatUtils;
import io.indices.troubleinminecraft.game.DeadPlayer;
import io.indices.troubleinminecraft.game.TIMData;
import io.indices.troubleinminecraft.team.Role;

public class GameFeature extends AbstractFeature {

    @Inject
    private UserHandler userHandler;

    private PersonalScoreboardFeature.GlobalScoreboard globalScoreboard;

    private List<User> innocents = new ArrayList<>();
    private List<User> traitors = new ArrayList<>();
    private List<User> detectives = new ArrayList<>();

    private List<User> aliveInnocents = new ArrayList<>(); // detectives are classed as innocents for these purposes
    private List<User> aliveTraitors = new ArrayList<>();

    private List<Vector3D> chests = new ArrayList<>();

    private Map<Entity, DeadPlayer> zombiePlayerMap = new HashMap<>();

    private int visiblePlayersLeft;

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
            assignRoles();
            createChests();
        } else {
            gameStarted = true;
            innocents = timData.getInnocents();
            traitors = timData.getTraitors();
            detectives = timData.getDetectives();
            aliveInnocents = timData.getInnocents();
            aliveTraitors = timData.getAliveTraitors();
            chests = timData.getChests();
        }

        visiblePlayersLeft = getPhase().getGame().getPlayers().size();

        // we have to do this each time the feature is loaded
        initScoreboard();

        if (gameStarted) {
            notifyRoles();
        }

        gameStarted = true;
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
        timData.setChests(chests);
        timData.setZombiePlayerMap(zombiePlayerMap);
        getPhase().getGame().putGameData(timData);
    }

    @Override
    public void tick() {

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

    /**
     * Initialise scoreboard
     */
    private void initScoreboard() {
        globalScoreboard = getPhase().getFeature(PersonalScoreboardFeature.class).getGlobalScoreboard();

        globalScoreboard.setTitle(ChatColor.BLUE + "TIMC");

        globalScoreboard.createAndAddLine("kills", "0");
        globalScoreboard.createAndAddLine(ChatColor.AQUA + ChatColor.BOLD.toString() + "Kills");

        globalScoreboard.createAndAddLine(ChatColor.RESET + ChatColor.RESET.toString() + "");

        globalScoreboard.createAndAddLine("players-left", visiblePlayersLeft + "");
        globalScoreboard.createAndAddLine(ChatColor.GREEN + ChatColor.BOLD.toString() + "Players left");

        globalScoreboard.createAndAddLine(ChatColor.RESET + "");

        globalScoreboard.createAndAddLine("role", ChatColor.MAGIC + "????????");
        globalScoreboard.createAndAddLine(ChatColor.GOLD + ChatColor.BOLD.toString() + "Role");

        globalScoreboard.createAndAddLine(ChatColor.RESET + ChatColor.RESET.toString() + ChatColor.RESET.toString() + "");
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
                traitors.add(traitor);
            }
        }

        for (int i = 0; i < detectiveAmount; i++) {
            int n = ThreadLocalRandom.current().nextInt(playerCount - 1);

            User detective = getPhase().getGame().getPlayers().get(n);

            if (traitors.contains(detective) || detectives.contains(detective)) {
                // choose again
                i--;
            } else {
                detectives.add(detective);
            }
        }

        getPhase().getGame().getPlayers().forEach(user -> {
            if (!traitors.contains(user) && !detectives.contains(user)) {
                innocents.add(user);
            }
        });
    }

    /**
     * Notifies the roles of their role
     */
    private void notifyRoles() {
        aliveTraitors.addAll(traitors);
        aliveInnocents.addAll(detectives);
        aliveInnocents.addAll(innocents);

        traitors.forEach(user -> {
            getPhase().getFeature(PersonalScoreboardFeature.class).getScoreboardForUser(user).getLine("role").ifPresent(line -> line.setValue(ChatUtils.formatRoleName(Role.TRAITOR, true)));

            String traitorListString = traitors.stream()
                    .filter(u -> !u.getUuid().equals(user.getUuid()))
                    .map(User::getRawDisplayName)
                    .collect(Collectors.joining(", "));

            user.sendMessage(TextComponent.of("You are a traitor! Work with your fellow traitors to kill the innocents. Watch out for the detectives, they have the tools to get you too.").color(TextColor.RED));

            if (traitorListString != null && !traitorListString.isEmpty()) {
                user.sendMessage(TextComponent.of("Your fellow traitors are: ").color(TextColor.RED).append(TextComponent.of(traitorListString).color(TextColor.DARK_RED)));
            }

        });

        detectives.forEach(user -> {
            getPhase().getFeature(PersonalScoreboardFeature.class).getScoreboardForUser(user).getLine("role").ifPresent(line -> line.setValue(ChatUtils.formatRoleName(Role.DETECTIVE, true)));

            String detectiveListString = detectives.stream()
                    .filter(u -> !u.getUuid().equals(user.getUuid()))
                    .map(User::getRawDisplayName)
                    .collect(Collectors.joining(", "));

            user.sendMessage(TextComponent.of("You are a detective! It is your job to save the innocents from the traitors.").color(TextColor.BLUE));

            if (detectiveListString != null && !detectiveListString.isEmpty()) {
                user.sendMessage(TextComponent.of("Your fellow detectives are: ").color(TextColor.BLUE).append(TextComponent.of(detectiveListString).color(TextColor.DARK_BLUE)));
            }
        });

        innocents.forEach(user -> {
            getPhase().getFeature(PersonalScoreboardFeature.class).getScoreboardForUser(user).getLine("role").ifPresent(line -> line.setValue(ChatUtils.formatRoleName(Role.INNOCENT, true)));
            user.sendMessage(TextComponent.of("You are an innocent. Find weapons and try to survive against the traitors. Work with the detectives to find and kill them. Stay alert!").color(TextColor.GREEN));

            String detectiveListString = detectives.stream()
                    .map(User::getRawDisplayName)
                    .collect(Collectors.joining(", "));

            user.sendMessage(TextComponent.of("Your detectives are: ").color(TextColor.GREEN).append(TextComponent.of(detectiveListString).color(TextColor.BLUE)));
        });
    }

    /**
     * Create chests from markers
     */
    private void createChests() {
        me.minidigger.voxelgameslib.map.Map map = getPhase().getFeature(MapFeature.class).getMap();
        for (Marker marker : map.getMarkers()) {
            if (marker.getData().startsWith("chest")) {
                // tbh, you can just have a chest and not set a marker at all
                marker.getLoc().toLocation(map.getWorldName()).getBlock().setType(Material.CHEST);
                chests.add(marker.getLoc());
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        userHandler.getUser(event.getEntity().getUniqueId()).ifPresent(user -> {
            if (getPhase().getGame().getPlayers().contains(user)) {
                event.setDeathMessage(null);

                Bukkit.getPluginManager().callEvent(new PlayerEliminationEvent(user, getPhase().getGame()));
                getPhase().getGame().spectate(user);

                // put a mob there
                Zombie zombie = event.getEntity().getLocation().getWorld().spawn(event.getEntity().getLocation(), Zombie.class);
                zombie.setCustomName(getRole(user).getColour() + user.getRawDisplayName());
                zombie.setBaby(false);
                DeadPlayer deadPlayer = new DeadPlayer();
                deadPlayer.setDisplayName(user.getRawDisplayName());
                deadPlayer.setIdentified(false);
                deadPlayer.setRole(getRole(user));
                deadPlayer.setUuid(user.getUuid());
                zombiePlayerMap.put(zombie, deadPlayer);

                if (event.getEntity().getKiller() != null) {
                    userHandler.getUser(event.getEntity().getKiller().getUniqueId()).ifPresent(killer -> {
                        if (getPhase().getGame().getPlayers().contains(killer)) {
                            getPhase().getFeature(PersonalScoreboardFeature.class).getScoreboardForUser(killer).getLine(2).ifPresent(line -> {
                                int kills = Integer.parseInt(line.getValue());
                                line.setValue(++kills + "");
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
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        userHandler.getUser(event.getPlayer().getUniqueId()).ifPresent(user -> {
            if (getPhase().getGame().getPlayers().contains(user)) {
                //
            }
        });
    }

    @EventHandler
    public void rightClickZombie(PlayerInteractEntityEvent event) {
        userHandler.getUser(event.getPlayer().getUniqueId()).ifPresent(user -> {
            if (getPhase().getGame().getPlayers().contains(user)) {
                if (event.getRightClicked() instanceof Zombie && event.getHand() == EquipmentSlot.HAND) {
                    if (zombiePlayerMap.containsKey(event.getRightClicked())) {
                        DeadPlayer deadPlayer = zombiePlayerMap.get(event.getRightClicked());

                        if (!deadPlayer.isIdentified()) {
                            visiblePlayersLeft--;
                            globalScoreboard.getLines("players-left").forEach(line -> line.setValue(visiblePlayersLeft + ""));

                            getPhase().getGame().getPlayers().forEach(otherPlayer -> {
                                        otherPlayer.sendMessage(TextComponent.of("The body of " + deadPlayer.getDisplayName() + " has been found!").color(TextColor.BLUE));
                                        otherPlayer.sendMessage(TextComponent.of("They were a(n) ").color(TextColor.BLUE).append(TextComponent.of(ChatUtils.formatRoleName(deadPlayer.getRole()) + "").append(TextComponent.of("!").color(TextColor.BLUE))));
                                    }
                            );

                            //event.getRightClicked().setCustomName(deadPlayer.getDisplayName());
                            deadPlayer.setIdentified(true);
                        } else {
                            user.sendMessage(TextComponent.of("This is the body of " + deadPlayer.getDisplayName() + ". They were a(n) " + ChatUtils.formatRoleName(deadPlayer.getRole())).color(TextColor.BLUE));
                        }
                    }
                }
            }
        });
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

    @EventHandler
    public void interactWithChest(PlayerInteractEvent event) {
        userHandler.getUser(event.getPlayer().getUniqueId()).ifPresent(user -> {
            if (getPhase().getGame().getPlayers().contains(user)) {
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.CHEST) {

                    event.getClickedBlock().setType(Material.AIR); // remove the chest

                    Inventory playerInv = event.getPlayer().getInventory();

                    int chance = ThreadLocalRandom.current().nextInt(3);

                    if (chance == 0) {
                        playerInv.addItem(new ItemStack(Material.WOOD_SWORD));
                    } else if (chance == 1) {
                        playerInv.addItem(new ItemStack(Material.STONE_SWORD));
                    } else {
                        if (!playerInv.contains(Material.BOW)) {
                            playerInv.addItem(new ItemStack(Material.BOW));
                        }
                        playerInv.addItem(new ItemStack(Material.ARROW, 32));
                    }

                    event.setCancelled(true);
                }
            }
        });
    }
}

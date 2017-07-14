package io.indices.troubleinminecraft.features;

import io.indices.troubleinminecraft.player.DeadPlayer;
import io.indices.troubleinminecraft.team.Role;
import io.indices.troubleinminecraft.util.ChatUtils;
import lombok.Setter;
import me.minidigger.voxelgameslib.event.events.player.PlayerEliminationEvent;
import me.minidigger.voxelgameslib.feature.AbstractFeature;
import me.minidigger.voxelgameslib.feature.features.MapFeature;
import me.minidigger.voxelgameslib.feature.features.ScoreboardFeature;
import me.minidigger.voxelgameslib.map.Marker;
import me.minidigger.voxelgameslib.map.Vector3D;
import me.minidigger.voxelgameslib.scoreboard.Scoreboard;
import me.minidigger.voxelgameslib.user.User;
import me.minidigger.voxelgameslib.user.UserHandler;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class GameFeature extends AbstractFeature {

    @Inject
    private UserHandler userHandler;

    @Setter
    private Scoreboard scoreboard;

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
        Object gameStarted = getPhase().getGame().getGameData("gameStarted");

        if (gameStarted == null || !(gameStarted instanceof Boolean) || !((Boolean) gameStarted)) {
            // initialise game
            assignRoles();
            createChests();

            getPhase().getGame().putGameData("gameStarted", true);
            // todo save data
            getPhase().getGame().putGameData("innocents", innocents);
            getPhase().getGame().putGameData("traitors", traitors);
            getPhase().getGame().putGameData("detectives", detectives);
            getPhase().getGame().putGameData("aliveInnocents", aliveInnocents);
            getPhase().getGame().putGameData("aliveTraitors", aliveTraitors);
            getPhase().getGame().putGameData("chests", chests);
        } else {
            // todo load values from previous phase
            innocents = (ArrayList<User>) getPhase().getGame().getGameData("innocents");
            traitors = (ArrayList<User>) getPhase().getGame().getGameData("traitors");
            detectives = (ArrayList<User>) getPhase().getGame().getGameData("detectives");
            aliveInnocents = (ArrayList<User>) getPhase().getGame().getGameData("aliveInnocents");
            aliveTraitors = (ArrayList<User>) getPhase().getGame().getGameData("aliveTraitors");
            chests = (ArrayList<Vector3D>) getPhase().getGame().getGameData("chests");
        }

        visiblePlayersLeft = getPhase().getGame().getPlayers().size();

        // we have to do this each time the feature is loaded
        initScoreboard();
    }

    @Override
    public void stop() {

    }

    @Override
    public void tick() {

    }

    @Override
    public Class[] getDependencies() {
        return new Class[]{ScoreboardFeature.class, MapFeature.class};
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
        scoreboard.setTitle(ChatColor.BLUE + "TIMC");

        // yes, i know this goes upside down. string keys are rip in VGL. will change this tomorrow. it's 5AM rn...
        scoreboard.createAndAddLine(9, ChatColor.GOLD + ChatColor.BOLD.toString() + "Role");
        scoreboard.createAndAddLine(8, "test");

        scoreboard.createAndAddLine(7, ChatColor.RESET + "");

        scoreboard.createAndAddLine(6, ChatColor.GREEN + ChatColor.BOLD.toString() + "Players left");
        scoreboard.createAndAddLine(5, visiblePlayersLeft + "");

        scoreboard.createAndAddLine(4, ChatColor.RESET + ChatColor.RESET.toString() + "");

        scoreboard.createAndAddLine(3, ChatColor.AQUA + ChatColor.BOLD.toString() + "Kills");
        scoreboard.createAndAddLine(2, "test");

        scoreboard.createAndAddLine(1, ChatColor.RESET + ChatColor.RESET.toString() + ChatColor.RESET.toString() + "");
    }

    /**
     * Choose innocents, traitors and detectives
     */
    private void assignRoles() {
        int playerCount = getPhase().getGame().getPlayers().size();
        int traitorAmount = (playerCount / 5) + 1; // 1 traitor per 5 players
        int detectiveAmount = (playerCount / 10); // 1 detective each 10 players

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

        // send messages and roles
        traitors.forEach(user -> {
            aliveTraitors.add(user);
            user.sendMessage(TextComponent.of(ChatColor.RED + "You are a traitor! Go kill all the innocents!")); // todo, lang api and a better message lol
            // todo set their scoreboard role, currently not possible in VGL per-player using a friendly API, should create a PersonalScoreboard feature
        });

        detectives.forEach(user -> {
            aliveInnocents.add(user);
            user.sendMessage(TextComponent.of(ChatColor.BLUE + "You are a detective! Go save all the people!"));
        });

        innocents.forEach(user -> {
            aliveInnocents.add(user);
            user.sendMessage(TextComponent.of(ChatColor.GREEN + "You are an innocent. Find weapons and try to survive against those pesky traitors."));
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
                Entity zombie = event.getEntity().getLocation().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.ZOMBIE);
                DeadPlayer deadPlayer = new DeadPlayer();
                deadPlayer.setDisplayName(user.getRawDisplayName());
                deadPlayer.setIdentified(false);
                deadPlayer.setRole(getRole(user));
                deadPlayer.setUuid(user.getUuid());
                zombiePlayerMap.put(zombie, deadPlayer);

                if (event.getEntity().getKiller() != null) {
                    userHandler.getUser(event.getEntity().getKiller().getUniqueId()).ifPresent(killer -> {
                        if (getPhase().getGame().getPlayers().contains(killer)) {
                            // todo set their scoreboard kills, currently not possible in VGL per-player using a friendly API
                        }
                    });
                }

                if (traitors.contains(user)) {
                    aliveTraitors.remove(user);
                    if (aliveTraitors.size() == 0) {
                        // innocents win
                        getPhase().getGame().putGameData("winner", Role.INNOCENT);
                        getPhase().getGame().endPhase();
                    }
                } else {
                    aliveInnocents.remove(user);

                    if (aliveInnocents.size() == 0) {
                        // traitors win
                        getPhase().getGame().putGameData("winner", Role.TRAITOR);
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

                        if(!deadPlayer.isIdentified()) {
                            visiblePlayersLeft--;
                            scoreboard.getLine(5).ifPresent(line -> line.setValue(visiblePlayersLeft + ""));

                            getPhase().getGame().getPlayers().forEach(otherPlayer -> {
                                        otherPlayer.sendMessage(TextComponent.of("The body of " + deadPlayer.getDisplayName() + " has been found!").color(TextColor.BLUE));
                                        otherPlayer.sendMessage(TextComponent.of("They were a ").color(TextColor.BLUE).append(TextComponent.of(ChatUtils.formatRoleName(deadPlayer.getRole()) + "").append(TextComponent.of("!").color(TextColor.BLUE))));
                                    }
                            );

                            //event.getRightClicked().setCustomName(deadPlayer.getDisplayName());
                            deadPlayer.setIdentified(true);
                        } else {
                            user.sendMessage(TextComponent.of("This is the body of " + deadPlayer.getDisplayName() + ". They were a " + ChatUtils.formatRoleName(deadPlayer.getRole())));
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
    public void onEntityBurn(EntityDamageEvent event) {

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

package io.indices.troubleinminecraft.features;

import io.indices.troubleinminecraft.game.Role;
import lombok.Setter;
import me.minidigger.voxelgameslib.event.events.player.PlayerEliminationEvent;
import me.minidigger.voxelgameslib.feature.AbstractFeature;
import me.minidigger.voxelgameslib.feature.features.MapFeature;
import me.minidigger.voxelgameslib.feature.features.ScoreboardFeature;
import me.minidigger.voxelgameslib.map.Map;
import me.minidigger.voxelgameslib.map.Marker;
import me.minidigger.voxelgameslib.map.Vector3D;
import me.minidigger.voxelgameslib.scoreboard.Scoreboard;
import me.minidigger.voxelgameslib.user.User;
import me.minidigger.voxelgameslib.user.UserHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GameFeature extends AbstractFeature {

    @Inject
    private UserHandler userHandler;

    @Setter
    private Scoreboard scoreboard;

    private List<User> innocents = new ArrayList<>();
    private List<User> traitors = new ArrayList<>();
    private List<User> detectives = new ArrayList<>();

    private List<Vector3D> chests = new ArrayList<>();

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
        }

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
        return new Class[]{ScoreboardFeature.class};
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
        scoreboard.createAndAddLine("roleTitle", ChatColor.GOLD + ChatColor.BOLD.toString() + "Role");
        scoreboard.createAndAddLine("role", "test");

        scoreboard.createAndAddLine("nil-1", ChatColor.RESET + "");

        scoreboard.createAndAddLine("playersLeftTitle", ChatColor.GREEN + ChatColor.BOLD.toString() + "Players left");
        scoreboard.createAndAddLine("playersLeft", getPhase().getGame().getPlayers().size() + "");

        scoreboard.createAndAddLine("nil-2", ChatColor.RESET + ChatColor.RESET.toString() + "");

        scoreboard.createAndAddLine("killsTitle", ChatColor.AQUA + ChatColor.BOLD.toString() + "Kills");
        scoreboard.createAndAddLine("kills", "test");

        scoreboard.createAndAddLine("nil-3", ChatColor.RESET + ChatColor.RESET.toString() + ChatColor.RESET.toString() + "");
    }

    /**
     * Choose innocents, traitors and detectives
     */
    private void assignRoles() {
        int playerCount = getPhase().getGame().getPlayers().size();
        int traitorAmount = (playerCount / 5) + 1; // 1 traitor per 5 players
        int detectiveAmount = (playerCount / 10); // 1 detective each 10 players

        for (int i = 0; i < traitorAmount; i++) {
            int n = ThreadLocalRandom.current().nextInt(playerCount - 1);

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
            // todo set their scoreboard role, currently not possible in VGL per-player using a friendly API
        });
    }

    /**
     * Create chests from markers
     */
    private void createChests() {
        Map map = getPhase().getFeature(MapFeature.class).getMap();
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
                getPhase().getGame().leave(user);
                getPhase().getGame().spectate(user);

                // put a mob there
                event.getEntity().getLocation().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.ZOMBIE);

                scoreboard.createAndAddLine("playersLeft", getPhase().getGame().getPlayers().size() + "");

                userHandler.getUser(event.getEntity().getKiller().getUniqueId()).ifPresent(killer -> {
                    if (getPhase().getGame().getPlayers().contains(killer)) {
                        // todo set their scoreboard kills, currently not possible in VGL per-player using a friendly API
                    }
                });
            }
        });
    }

    @EventHandler
    public void rightClickZombie(PlayerInteractEntityEvent event) {
        userHandler.getUser(event.getPlayer().getUniqueId()).ifPresent(user -> {
            if (getPhase().getGame().getPlayers().contains(user)) {
                if (event.getRightClicked() instanceof Zombie) {
                    // todo identify the corpse
                }
            }
        });
    }

    @EventHandler
    public void interactWithChest(PlayerInteractEvent event) {
        userHandler.getUser(event.getPlayer().getUniqueId()).ifPresent(user -> {
            if (getPhase().getGame().getPlayers().contains(user)) {
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getMaterial() == Material.CHEST) {
                    Inventory playerInv = event.getPlayer().getInventory();

                    // not exactly random, but fair
                    if (playerInv.contains(Material.WOOD_SWORD)) {
                        int chance = ThreadLocalRandom.current().nextInt(1);
                        if (chance == 0) {
                            playerInv.addItem(new ItemStack(Material.STONE_SWORD));
                        } else {
                            playerInv.addItem(new ItemStack(Material.BOW));
                            playerInv.addItem(new ItemStack(Material.ARROW, 32));
                        }
                    } else if (playerInv.contains(Material.STONE_SWORD)) {
                        int chance = ThreadLocalRandom.current().nextInt(1);
                        if (chance == 0) {
                            playerInv.addItem(new ItemStack(Material.WOOD_SWORD));
                        } else {
                            playerInv.addItem(new ItemStack(Material.BOW));
                            playerInv.addItem(new ItemStack(Material.ARROW, 32));
                        }
                    } else if (playerInv.contains(Material.BOW)) {
                        int chance = ThreadLocalRandom.current().nextInt(1);
                        if (chance == 0) {
                            playerInv.addItem(new ItemStack(Material.WOOD_SWORD));
                        } else {
                            playerInv.addItem(new ItemStack(Material.STONE_SWORD));
                        }
                    } else {
                        int chance = ThreadLocalRandom.current().nextInt(2);
                        if (chance == 0) {
                            playerInv.addItem(new ItemStack(Material.WOOD_SWORD));
                        } else if (chance == 1) {
                            playerInv.addItem(new ItemStack(Material.STONE_SWORD));
                        } else {
                            playerInv.addItem(new ItemStack(Material.BOW));
                            playerInv.addItem(new ItemStack(Material.ARROW, 32));
                        }
                    }

                    event.setCancelled(true);
                }
            }
        });
    }
}

package io.indices.troubleinminecraft.abilities;

import com.google.gson.annotations.Expose;
import com.voxelgameslib.voxelgameslib.components.ability.Ability;
import com.voxelgameslib.voxelgameslib.game.Game;
import com.voxelgameslib.voxelgameslib.game.GameHandler;
import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.user.User;
import com.voxelgameslib.voxelgameslib.utils.ItemBuilder;
import io.indices.troubleinminecraft.TroubleInMinecraftPlugin;
import io.indices.troubleinminecraft.lang.TIMLangKey;
import net.kyori.text.LegacyComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class C4Ability extends Ability {
    public static ItemStack ITEM_STACK = new ItemBuilder(Material.TNT).amount(1).name(LegacyComponent.to(Lang.trans(TIMLangKey.ITEM_C4_TITLE))).lore(LegacyComponent.to(Lang.trans(TIMLangKey.ITEM_C4_LORE))).build();

    @Inject
    private TroubleInMinecraftPlugin plugin;
    @Inject
    private GameHandler gameHandler;

    private Location plantedLocation;

    @Expose
    private int bombTickTime = 30;
    private int remainingTime = 0;

    /**
     * @see Ability#Ability(User)
     */
    public C4Ability(@Nonnull User user) {
        super(user);
    }

    @Override
    public void start() {
        affected.getPlayer().getInventory().addItem(ITEM_STACK);
    }

    @Override
    public void stop() {

    }

    @Override
    public void tick() {
        if (plantedLocation != null) {
            plantedLocation.getWorld().playSound(plantedLocation, Sound.UI_BUTTON_CLICK, Math.max(0F, (1.0F / remainingTime) - 0.1F), 1.0F);
            remainingTime--;
        }
    }

    public void explode() {
        plantedLocation.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, plantedLocation, 1);

        Game game = gameHandler.getGames(affected.getUuid(), false).get(0);

        if (game != null) {
            game.getPlayers().forEach(user -> {
                if (user.getPlayer().getLocation().distanceSquared(plantedLocation) < 10 * 10) {
                    // cya m8
                    user.getPlayer().setHealth(0);
                }
            });
        }

        unregister(true);
    }

    @EventHandler
    public void onPlant(PlayerInteractEvent event) {
        if (event.getPlayer().getUniqueId().equals(affected.getUuid())) {
            if (event.getItem().equals(ITEM_STACK) && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                plantedLocation = event.getClickedBlock().getRelative(event.getBlockFace()).getLocation();

                plantedLocation.getBlock().setType(Material.AIR);
                // todo spawn a falling block of TNT here

                remainingTime = bombTickTime;

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        explode();
                    }
                }.runTaskLater(plugin, bombTickTime * 20);
            }
        }
    }
}

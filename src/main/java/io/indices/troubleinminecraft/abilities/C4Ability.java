package io.indices.troubleinminecraft.abilities;

import com.google.gson.annotations.Expose;
import com.voxelgameslib.voxelgameslib.api.game.GameHandler;
import com.voxelgameslib.voxelgameslib.components.ability.Ability;
import com.voxelgameslib.voxelgameslib.components.user.User;
import com.voxelgameslib.voxelgameslib.internal.lang.Lang;
import com.voxelgameslib.voxelgameslib.util.utils.ItemBuilder;
import io.indices.troubleinminecraft.TroubleInMinecraftPlugin;
import io.indices.troubleinminecraft.lang.TIMLangKey;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class C4Ability extends ExplosionAbility {

    public static ItemStack ITEM_STACK = new ItemBuilder(Material.TNT)
            .amount(1)
            .name(Lang.legacy(TIMLangKey.ITEM_C4_TITLE))
            .lore(Lang.legacy(TIMLangKey.ITEM_C4_LORE))
            .build();

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
    public void enable() {
        affected.getPlayer().getInventory().addItem(ITEM_STACK);
    }

    @Override
    public void disable() {

    }

    @Override
    public void tick() {
        if (plantedLocation != null) {
            plantedLocation.getWorld().playSound(plantedLocation, Sound.UI_BUTTON_CLICK, Math.max(0F, (1.0F / remainingTime) - 0.1F), 1.0F);
            remainingTime--;
        }
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
                        explode(gameHandler, plantedLocation, 12);
                    }
                }.runTaskLater(plugin, bombTickTime * 20);
            }
        }
    }
}

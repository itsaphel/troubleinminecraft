package io.indices.troubleinminecraft.features;

import com.voxelgameslib.voxelgameslib.event.GameEvent;
import com.voxelgameslib.voxelgameslib.feature.AbstractFeature;
import com.voxelgameslib.voxelgameslib.feature.features.MapFeature;
import com.voxelgameslib.voxelgameslib.map.Marker;
import com.voxelgameslib.voxelgameslib.map.Vector3D;
import com.voxelgameslib.voxelgameslib.user.User;
import com.voxelgameslib.voxelgameslib.user.UserHandler;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import java.util.concurrent.ThreadLocalRandom;

public class ChestItemPickupFeature extends AbstractFeature {
    @Inject
    private UserHandler userHandler;

    //private List<Vector3D> chests = new ArrayList<>(); // markers not used here currently

    @Override
    public void init() {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void tick() {

    }

    /*
     * Currently not in use

    /#**
     * Create chests from markers
     *#/
    private void createChests() {
        com.voxelgameslib.voxelgameslib.map.Map map = getPhase().getFeature(MapFeature.class).getMap();
        for (Marker marker : map.getMarkers()) {
            if (marker.getData().startsWith("chest")) {
                // tbh, you can just have a chest and not set a marker at all
                marker.getLoc().toLocation(map.getWorldName()).getBlock().setType(Material.CHEST);
                chests.add(marker.getLoc());
            }
        }
    }*/

    @GameEvent
    public void interactWithChest(PlayerInteractEvent event, User user) {
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
}

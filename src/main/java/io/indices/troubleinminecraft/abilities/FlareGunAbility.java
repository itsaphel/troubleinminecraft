package io.indices.troubleinminecraft.abilities;

import com.voxelgameslib.voxelgameslib.components.user.User;
import com.voxelgameslib.voxelgameslib.internal.lang.Lang;
import com.voxelgameslib.voxelgameslib.util.utils.ItemBuilder;
import io.indices.troubleinminecraft.lang.TIMLangKey;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class FlareGunAbility extends TTTAbility {

    public static ItemStack ITEM_STACK = new ItemBuilder(Material.BLAZE_ROD)
            .amount(1)
            .name(Lang.legacy(TIMLangKey.ITEM_FLARE_GUN_TITLE))
            .lore(Lang.legacy(TIMLangKey.ITEM_FLARE_GUN_LORE))
            .build();


    public FlareGunAbility(@Nonnull User user) {
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

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onZombieDamageByPlayer(@Nonnull EntityDamageByEntityEvent event) {
        if (event.getDamager().getUniqueId().equals(affected.getUuid())) {
            event.setCancelled(true);
            event.getEntity().setFireTicks(Integer.MAX_VALUE);
        }
    }
}

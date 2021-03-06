package io.indices.troubleinminecraft.abilities;

import com.voxelgameslib.voxelgameslib.internal.lang.Lang;
import com.voxelgameslib.voxelgameslib.components.user.User;
import com.voxelgameslib.voxelgameslib.util.utils.ItemBuilder;
import io.indices.troubleinminecraft.lang.TIMLangKey;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class KnifeAbility extends TTTAbility {

    public static ItemStack ITEM_STACK = new ItemBuilder(Material.DIAMOND_SWORD)
            .name(Lang.legacy(TIMLangKey.ITEM_KNIFE_TITLE))
            .lore(Lang.legacy(TIMLangKey.ITEM_KNIFE_LORE))
            .amount(1)
            .durability(Material.DIAMOND_SWORD.getMaxDurability() - 1)
            .build();

    public KnifeAbility(@Nonnull User user) {
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

    @EventHandler
    public void onDamage(@Nonnull EntityDamageByEntityEvent event) {
        if (event.getDamager().getUniqueId().equals(affected.getUuid())) {
            Player entity = (Player) event.getDamager();

            Bukkit.getLogger().info(entity.getItemInHand().toString());
            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK && entity.getInventory().getItemInMainHand().equals(ITEM_STACK)) {
                entity.getInventory().getItemInMainHand().setDurability(Material.DIAMOND_SWORD.getMaxDurability());
                event.setDamage(9999);

                unregister(true);
            }
        }
    }

    @EventHandler
    public void onItemSwitch(@Nonnull PlayerItemHeldEvent event) {
        if (event.getPlayer().getUniqueId().equals(affected.getUuid())) {
            ItemStack newItem = event.getPlayer().getInventory().getItem(event.getNewSlot());
            if (newItem != null && newItem.equals(ITEM_STACK)) {
                event.getPlayer().getLocation().getWorld().playSound(event.getPlayer().getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.2F, 1F);
            }
        }
    }
}

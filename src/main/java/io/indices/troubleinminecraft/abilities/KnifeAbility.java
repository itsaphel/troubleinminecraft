package io.indices.troubleinminecraft.abilities;

import net.kyori.text.LegacyComponent;

import javax.annotation.Nonnull;

import com.voxelgameslib.voxelgameslib.components.ability.Ability;
import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.user.User;
import com.voxelgameslib.voxelgameslib.utils.ItemBuilder;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import io.indices.troubleinminecraft.lang.TIMLangKey;

public class KnifeAbility extends TTTAbility {

    public static ItemStack ITEM_STACK = new ItemBuilder(Material.DIAMOND_SWORD)
            .name(LegacyComponent.to(Lang.trans(TIMLangKey.ITEM_KNIFE_TITLE)))
            .lore(LegacyComponent.to(Lang.trans(TIMLangKey.ITEM_KNIFE_LORE)))
            .amount(1)
            .durability(Material.DIAMOND_SWORD.getMaxDurability() - 1)
            .build();

    public KnifeAbility(@Nonnull User user) {
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

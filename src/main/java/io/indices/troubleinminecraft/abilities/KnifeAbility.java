package io.indices.troubleinminecraft.abilities;

import com.voxelgameslib.voxelgameslib.components.ability.Ability;
import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.user.User;
import com.voxelgameslib.voxelgameslib.utils.ItemBuilder;

import io.indices.troubleinminecraft.lang.TIMLangKey;
import net.kyori.text.LegacyComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class KnifeAbility extends Ability {
    public static ItemStack itemStack = new ItemBuilder(Material.DIAMOND_SWORD).name(LegacyComponent.to(Lang.trans(TIMLangKey.ITEM_KNIFE_TITLE)))
            .lore(LegacyComponent.to(Lang.trans(TIMLangKey.ITEM_KNIFE_LORE))).amount(1).durability(Material.DIAMOND_SWORD.getMaxDurability() - 1).build();

    public KnifeAbility(User user) {
        super(user);
    }

    @Override
    public void start() {
        affected.getPlayer().getInventory().addItem(itemStack);
    }

    @Override
    public void stop() {

    }

    @Override
    public void tick() {

    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager().getUniqueId().equals(affected.getUuid())) {
            Player entity = (Player) event.getDamager();

            Bukkit.getLogger().info(entity.getItemInHand().toString());
            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK && entity.getInventory().getItemInMainHand().equals(itemStack)) {
                entity.getInventory().getItemInMainHand().setDurability(Material.DIAMOND_SWORD.getMaxDurability());
                event.setDamage(9999);

                unregister(true);
            }
        }
    }
}

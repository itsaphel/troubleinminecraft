package io.indices.troubleinminecraft.abilities;

import me.minidigger.voxelgameslib.components.ability.Ability;
import me.minidigger.voxelgameslib.event.GameEvent;
import me.minidigger.voxelgameslib.user.User;
import me.minidigger.voxelgameslib.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class KnifeAbility extends Ability {
    public static ItemStack itemStack = new ItemBuilder(Material.DIAMOND_SWORD).name(ChatColor.RED + "Knife")
            .lore("This will instantly kill the player you hit with it. Careful, it may make a noise when you set this as your selected item!").amount(1).durability(99).build();

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

    @GameEvent
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.PLAYER && event.getEntity().getUniqueId().equals(affected.getUuid())) {
            Player entity = (Player) event.getEntity();

            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK && entity.getInventory().getItemInMainHand() == itemStack) {
                entity.getInventory().getItemInMainHand().setDurability((short) 100);
                event.setDamage(9999);

                // todo unregister ability
            }
        }
    }
}

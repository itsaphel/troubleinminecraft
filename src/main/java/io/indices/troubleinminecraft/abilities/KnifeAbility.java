package io.indices.troubleinminecraft.abilities;

import com.voxelgameslib.voxelgameslib.components.ability.Ability;
import com.voxelgameslib.voxelgameslib.event.GameEvent;
import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.user.User;
import com.voxelgameslib.voxelgameslib.utils.ItemBuilder;

import io.indices.troubleinminecraft.lang.TIMLangKey;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class KnifeAbility extends Ability {
    public static ItemStack itemStack = new ItemBuilder(Material.DIAMOND_SWORD).name(Lang.string(TIMLangKey.ITEM_KNIFE_TITLE))
            .lore(Lang.string(TIMLangKey.ITEM_KNIFE_LORE)).amount(1).durability(Material.DIAMOND_SWORD.getMaxDurability() - 1).build();

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
                entity.getInventory().getItemInMainHand().setDurability(Material.DIAMOND_SWORD.getMaxDurability());
                event.setDamage(9999);

                // todo unregister ability

            }
        }
    }
}

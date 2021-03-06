package io.indices.troubleinminecraft.abilities;

import com.voxelgameslib.voxelgameslib.components.user.User;
import com.voxelgameslib.voxelgameslib.internal.lang.Lang;
import com.voxelgameslib.voxelgameslib.util.utils.ItemBuilder;
import io.indices.troubleinminecraft.lang.TIMLangKey;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;

import javax.annotation.Nonnull;

public class CreeperEggAbility extends TTTAbility {

    public static ItemStack ITEM_STACK = new ItemBuilder(Material.LEGACY_MONSTER_EGG)
            .meta((itemMeta) -> ((SpawnEggMeta) itemMeta).setSpawnedType(EntityType.CREEPER))
            .name(Lang.legacy(TIMLangKey.ITEM_CREEPER_EGG_TITLE))
            .lore(Lang.legacy(TIMLangKey.ITEM_CREEPER_EGG_LORE))
            .build();

    public CreeperEggAbility(@Nonnull User user) {
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
    public void firedArrowLands(@Nonnull ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getEntity();

            if (arrow.getShooter() instanceof Player && ((Player) arrow.getShooter()).getUniqueId().equals(affected.getUuid())) {
                if (affected.getPlayer().getInventory().contains(ITEM_STACK)) {
                    Location spawnLoc;

                    if (event.getHitBlock() != null) {
                        spawnLoc = event.getHitBlock().getLocation();
                    } else {
                        spawnLoc = event.getHitEntity().getLocation();
                    }

                    for (ItemStack stack : affected.getPlayer().getInventory().getContents()) {
                        if (stack == ITEM_STACK) {
                            if (stack.getAmount() == 1) {
                                affected.getPlayer().getInventory().remove(ITEM_STACK);
                            } else {
                                stack.setAmount(stack.getAmount() - 1);
                            }
                        }
                    }

                    Bukkit.getWorld(spawnLoc.getWorld().getName()).spawnEntity(spawnLoc, EntityType.CREEPER);
                }
            }
        }
    }
}

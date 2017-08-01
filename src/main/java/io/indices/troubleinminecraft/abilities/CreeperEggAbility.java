package io.indices.troubleinminecraft.abilities;

import me.minidigger.voxelgameslib.components.ability.Ability;
import me.minidigger.voxelgameslib.event.GameEvent;
import me.minidigger.voxelgameslib.user.User;
import me.minidigger.voxelgameslib.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;

public class CreeperEggAbility extends Ability {
    public static ItemStack itemStack = new ItemBuilder(Material.MONSTER_EGG).meta((itemMeta) -> ((SpawnEggMeta) itemMeta).setSpawnedType(EntityType.CREEPER))
            .name(ChatColor.GREEN + "Creeper Eggs").lore("Spawn a creeper where your arrows lands.").build();

    public CreeperEggAbility(User user) {
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
    public void firedArrowLands(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getEntity();

            if (arrow.getShooter() instanceof Player && ((Player) arrow.getShooter()).getUniqueId().equals(affected.getUuid())) {
                if (affected.getPlayer().getInventory().contains(itemStack)) {
                    Location spawnLoc;

                    if (event.getHitBlock() != null) {
                        spawnLoc = event.getHitBlock().getLocation();
                    } else {
                        spawnLoc = event.getHitEntity().getLocation();
                    }

                    for (ItemStack stack : affected.getPlayer().getInventory().getContents()) {
                        if (stack == itemStack) {
                            if (stack.getAmount() == 1) {
                                affected.getPlayer().getInventory().remove(itemStack);
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

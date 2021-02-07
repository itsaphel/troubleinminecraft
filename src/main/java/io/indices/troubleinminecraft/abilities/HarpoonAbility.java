package io.indices.troubleinminecraft.abilities;

import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.user.User;
import com.voxelgameslib.voxelgameslib.utils.ItemBuilder;
import io.indices.troubleinminecraft.abilities.modifiers.TripleHarpoonModifier;
import io.indices.troubleinminecraft.lang.TIMLangKey;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

public class HarpoonAbility extends TTTAbility {

    public static ItemStack ITEM_STACK = new ItemBuilder(Material.SNOW_BALL)
            .amount(1)
            .name(Lang.legacy(TIMLangKey.ITEM_HARPOON_TITLE))
            .lore(Lang.legacy(TIMLangKey.ITEM_HARPOON_LORE))
            .build();

    private long shot;
    private int quantity = 1;

    public HarpoonAbility(User user) {
        super(user);
        acceptedModifiers.add(TripleHarpoonModifier.class);
    }

    @Override
    public void enable() {
        super.enable();
        affected.getPlayer().getInventory().addItem(ITEM_STACK);
    }

    @Override
    public void disable() {

    }

    @Override
    public void tick() {
        if (shot != 0 && shot < System.currentTimeMillis() - (1000 * 20)) {
            unregister();
        }
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @EventHandler
    public void onThrow(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player && ((Player) event.getEntity().getShooter()).getUniqueId().equals(affected.getUuid())) {
            if (quantity > 1) {
                Location centralLocation = event.getEntity().getLocation();

                // this is for very basic math here... quantity should always be an odd number for this to look good
                boolean left = true;

                // spawn additional projectiles
                for (int i = 1; i < quantity; i++) {
                    Location projectileLocation = centralLocation.clone();

                    if (left) {
                        projectileLocation.add(0, 0, 0);
                        left = false;
                    } else {
                        left = true;
                    }

                    Entity projectile = centralLocation.getWorld().spawn(centralLocation, Snowball.class);
                    projectile.setVelocity(event.getEntity().getVelocity());
                }
            }

            shot = System.currentTimeMillis();
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Snowball && ((Snowball) event.getDamager()).getShooter() instanceof Player) {
            Player attacker = (Player) ((Snowball) event.getDamager()).getShooter();

            if (attacker.getUniqueId().equals(affected.getUuid())) {
                event.setDamage(Integer.MAX_VALUE);
                event.getEntity().getLocation().getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_IRONGOLEM_ATTACK, 1F, 1F);
                unregister();
            }
        }
    }
}

package io.indices.troubleinminecraft.abilities;

import com.voxelgameslib.voxelgameslib.components.ability.Ability;
import com.voxelgameslib.voxelgameslib.user.User;
import com.voxelgameslib.voxelgameslib.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class KnockbackStickAbility extends Ability {
    public static ItemStack itemStack = new ItemBuilder(Material.STICK).enchantment(Enchantment.KNOCKBACK, 2)
            .name("The Detective's Stick").amount(1).build();

    public KnockbackStickAbility(User user) {
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
}

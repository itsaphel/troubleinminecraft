package io.indices.troubleinminecraft.abilities;

import com.voxelgameslib.voxelgameslib.internal.lang.Lang;
import com.voxelgameslib.voxelgameslib.components.user.User;
import com.voxelgameslib.voxelgameslib.util.utils.ItemBuilder;
import io.indices.troubleinminecraft.lang.TIMLangKey;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class KnockbackStickAbility extends TTTAbility {

    public static ItemStack ITEM_STACK = new ItemBuilder(Material.STICK)
            .enchantment(Enchantment.KNOCKBACK, 2)
            .name(Lang.legacy(TIMLangKey.ITEM_KNOCKBACK_STICK_TITLE))
            .amount(1)
            .build();

    public KnockbackStickAbility(@Nonnull User user) {
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
}

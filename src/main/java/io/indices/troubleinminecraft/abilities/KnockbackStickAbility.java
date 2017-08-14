package io.indices.troubleinminecraft.abilities;

import net.kyori.text.LegacyComponent;

import javax.annotation.Nonnull;

import com.voxelgameslib.voxelgameslib.components.ability.Ability;
import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.user.User;
import com.voxelgameslib.voxelgameslib.utils.ItemBuilder;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import io.indices.troubleinminecraft.lang.TIMLangKey;

public class KnockbackStickAbility extends TTTAbility {

    public static ItemStack ITEM_STACK = new ItemBuilder(Material.STICK)
            .enchantment(Enchantment.KNOCKBACK, 2)
            .name(LegacyComponent.to(Lang.trans(TIMLangKey.ITEM_KNOCKBACK_STICK_TITLE)))
            .amount(1)
            .build();

    public KnockbackStickAbility(@Nonnull User user) {
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
}

package io.indices.troubleinminecraft.shop;

import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.utils.ItemBuilder;
import io.indices.troubleinminecraft.abilities.BodyArmourAbility;
import io.indices.troubleinminecraft.lang.TIMLangKey;
import lombok.Data;
import net.kyori.text.LegacyComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Data
public class ShopRegistry {
    private Shop traitorShop, detectiveShop, pointsShop;

    public void register() {
        ItemStack bodyArmourStack = new ItemBuilder(Material.IRON_CHESTPLATE).amount(1).name(LegacyComponent.to(Lang.trans(TIMLangKey.ITEM_BODY_ARMOUR_TITLE))).build();

        traitorShop = new Shop()
                .title(LegacyComponent.to(Lang.trans(TIMLangKey.SHOP_TRAITOR_INV_TITLE)))
                .currency(Currency.CREDITS)
                .addItem(new Item()
                        .itemStack(bodyArmourStack)
                        .addAbility(BodyArmourAbility.class))
                .onPurchase(bodyArmourStack, (user) -> user.getPlayer().getInventory().addItem(bodyArmourStack));

        detectiveShop = new Shop()
                .title(LegacyComponent.to(Lang.trans(TIMLangKey.SHOP_DETECTIVE_INV_TITLE)))
                .currency(Currency.CREDITS);

        pointsShop = new Shop()
                .title(LegacyComponent.to(Lang.trans(TIMLangKey.SHOP_POINTS_SHOP_INV_TITLE)))
                .currency(Currency.POINTS);
    }
}

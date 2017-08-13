package io.indices.troubleinminecraft.shop;

import com.google.inject.Injector;
import com.voxelgameslib.voxelgameslib.components.ability.Ability;
import com.voxelgameslib.voxelgameslib.components.inventory.InventoryHandler;
import com.voxelgameslib.voxelgameslib.game.Game;
import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.user.User;
import com.voxelgameslib.voxelgameslib.utils.ItemBuilder;
import io.indices.troubleinminecraft.TroubleInMinecraftPlugin;
import io.indices.troubleinminecraft.abilities.BodyArmourAbility;
import io.indices.troubleinminecraft.game.TIMData;
import io.indices.troubleinminecraft.game.TIMPlayer;
import io.indices.troubleinminecraft.lang.TIMLangKey;
import lombok.Data;
import net.kyori.text.LegacyComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;

@Data
public class ShopRegistry {
    @Inject
    private Injector injector;
    @Inject
    private InventoryHandler inventoryHandler;
    @Inject
    private TroubleInMinecraftPlugin plugin;

    private Game game;
    private Shop traitorShop, detectiveShop, pointsShop;

    /**
     * Register the ShopRegistry
     *
     * @param game the game this registry applies to
     */
    public void register(Game game) {
        this.game = game;

        ItemStack bodyArmourStack = new ItemBuilder(Material.IRON_CHESTPLATE).amount(1).name(LegacyComponent.to(Lang.trans(TIMLangKey.ITEM_BODY_ARMOUR_TITLE))).build();

        traitorShop = new Shop(inventoryHandler)
                .title(LegacyComponent.to(Lang.trans(TIMLangKey.SHOP_TRAITOR_INV_TITLE)))
                .currency(Currency.CREDITS)
                .addItem(new Item()
                        .itemStack(bodyArmourStack)
                        .addAbility(BodyArmourAbility.class))
                .onPurchase(this::purchaseRoleItem);

        detectiveShop = new Shop(inventoryHandler)
                .title(LegacyComponent.to(Lang.trans(TIMLangKey.SHOP_DETECTIVE_INV_TITLE)))
                .currency(Currency.CREDITS)
                .onPurchase(this::purchaseRoleItem);

        pointsShop = new Shop(inventoryHandler)
                .title(LegacyComponent.to(Lang.trans(TIMLangKey.SHOP_POINTS_SHOP_INV_TITLE)))
                .currency(Currency.POINTS)
                .onPurchase((user, item) -> {
                });
    }

    private void purchaseRoleItem(User user, Item item) {
        game.getGameData(TIMData.class).ifPresent(timData -> {
            if (!timData.isGameStarted()) {
                return;
            }

            TIMPlayer timPlayer = timData.getPlayerMap().get(user);

            if (item.cost() <= timPlayer.getCredits()) {
                // woopie, time to buy
                item.abilities().forEach(aClass -> {
                    try {
                        Ability ability = aClass.getConstructor(User.class).newInstance(user);
                        injector.injectMembers(ability);
                        ability.start();
                        Bukkit.getPluginManager().registerEvents(ability, plugin);
                        game.getActivePhase().addTickable(ability.getIdentifier(), ability);

                        timPlayer.setCredits(timPlayer.getCredits() - item.cost());
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                });
                Lang.msg(user, TIMLangKey.SHOP_YOU_HAVE_BOUGHT_X, item.itemStack().getItemMeta().getDisplayName());
            } else {
                Lang.msg(user, TIMLangKey.SHOP_YOU_DO_NOT_HAVE_ENOUGH_CREDITS, item.cost() - timPlayer.getCredits());
            }
        });
    }
}

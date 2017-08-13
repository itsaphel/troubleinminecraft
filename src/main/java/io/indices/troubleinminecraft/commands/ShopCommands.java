package io.indices.troubleinminecraft.commands;

import com.google.inject.Injector;

import com.voxelgameslib.voxelgameslib.components.inventory.InventoryHandler;
import com.voxelgameslib.voxelgameslib.game.Game;
import com.voxelgameslib.voxelgameslib.game.GameHandler;
import com.voxelgameslib.voxelgameslib.user.User;

import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;


import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import io.indices.troubleinminecraft.TroubleInMinecraftPlugin;
import io.indices.troubleinminecraft.game.TIMData;
@Singleton
@CommandAlias("shop")
public class ShopCommands extends BaseCommand {
    @Inject
    private TroubleInMinecraftPlugin plugin;
    @Inject
    private Injector injector;
    @Inject
    private GameHandler gameHandler;
    @Inject
    private InventoryHandler inventoryHandler;

    @Default
    @CommandPermission("%user")
    public void openShop(@Nonnull User sender) {
        List<Game> games = gameHandler.getGames(sender.getUuid(), false);

        if (games.size() == 1) {
            Game game = games.get(0);

            game.getGameData(TIMData.class).ifPresent(timData -> {
                if (!timData.isGameStarted()) {
                    return;
                }

                boolean isTraitor = timData.getTraitors().contains(sender);
                boolean isDetective = timData.getDetectives().contains(sender);

                if (isTraitor || isDetective) {
                    int index = 0;
                    String title;

                    /*if (isTraitor) {
                        title = LegacyComponent.to(Lang.trans(TIMLangKey.SHOP_TRAITOR_INV_TITLE));
                    } else {
                        title = LegacyComponent.to(Lang.trans(TIMLangKey.SHOP_DETECTIVE_INV_TITLE));
                    }*/

                    /*BasicInventory shopInv = inventoryHandler.createInventory(BasicInventory.class, sender.getPlayer(), title, 9);
                    List<? extends ShopItem> items;

                    if (isTraitor) {
                        items = injector.getInstance(TraitorShop.class).getItems();
                    } else {
                        items = injector.getInstance(DetectiveShop.class).getItems();
                    }

                    for (ShopItem item : items) {
                        shopInv.getBukkitInventory().setItem(index++, item.getItemStack());
                        shopInv.addClickAction(item.getItemStack(), (itemStack, inventoryClickEvent) -> {
                            // let's see if you can purchase the item
                            item.purchase(sender);

                            shopInv.close();
                            inventoryHandler.removeInventory(shopInv.getIdentifier());
                        });
                    }

                    sender.getPlayer().openInventory(shopInv.getBukkitInventory());*/
                }
            });
        }
    }
}

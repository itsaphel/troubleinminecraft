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
import io.indices.troubleinminecraft.game.TIMData;
import io.indices.troubleinminecraft.shop.Shop;
import io.indices.troubleinminecraft.shop.ShopRegistry;

@Singleton
@CommandAlias("shop")
public class ShopCommands extends BaseCommand {
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
                    ShopRegistry shopRegistry = timData.getShopRegistry();

                    Shop shop;

                    if (isTraitor) {
                        shop = shopRegistry.getTraitorShop();
                    } else {
                        shop = shopRegistry.getDetectiveShop();
                    }

                    sender.getPlayer().openInventory(shop.make(sender).getBukkitInventory());
                }
            });
        }
    }
}

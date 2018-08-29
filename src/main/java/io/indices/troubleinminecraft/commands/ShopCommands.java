package io.indices.troubleinminecraft.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import com.voxelgameslib.voxelgameslib.game.Game;
import com.voxelgameslib.voxelgameslib.game.GameHandler;
import com.voxelgameslib.voxelgameslib.user.User;
import io.indices.troubleinminecraft.game.TIMData;
import io.indices.troubleinminecraft.shop.Shop;
import io.indices.troubleinminecraft.shop.ShopRegistry;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class ShopCommands extends BaseCommand {
    @Inject
    private GameHandler gameHandler;

    @CommandAlias("shop|s")
    @CommandPermission("%user")
    public void openRoleShop(@Nonnull User sender) {
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

                    shop.openForPlayer(sender.getPlayer());
                }
            });
        }
    }

    @CommandAlias("pointshop|ps")
    @CommandPermission("%user%")
    public void openPointShop(@Nonnull User sender) {
        List<Game> games = gameHandler.getGames(sender.getUuid(), false);

        if (games.size() == 1) {
            Game game = games.get(0);

            game.getGameData(TIMData.class).ifPresent(timData -> {
                ShopRegistry shopRegistry = timData.getShopRegistry();
                Shop shop = shopRegistry.getPointsShop();

                shop.openForPlayer(sender.getPlayer());
            });
        }
    }
}

package io.indices.troubleinminecraft.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import io.indices.troubleinminecraft.TroubleInMinecraftPlugin;
import io.indices.troubleinminecraft.game.TIMData;
import me.minidigger.voxelgameslib.components.inventory.BasicInventory;
import me.minidigger.voxelgameslib.components.inventory.InventoryHandler;
import me.minidigger.voxelgameslib.game.GameHandler;
import me.minidigger.voxelgameslib.user.User;
import org.bukkit.ChatColor;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@CommandAlias("shop")
public class ShopCommands extends BaseCommand {
    @Inject
    private GameHandler gameHandler;
    @Inject
    private InventoryHandler inventoryHandler;

    @Default
    @CommandPermission("%user")
    public void openShop(User sender) {
        gameHandler.findGame(sender, TroubleInMinecraftPlugin.GAMEMODE).ifPresent(game -> {
            game.getGameData(TIMData.class).ifPresent(timData -> {
                if (timData.getTraitors().contains(sender)) {
                    BasicInventory traitorShop = inventoryHandler.createInventory(BasicInventory.class, sender.getPlayer(), ChatColor.RED + "Traitor Shop", 9);
                } else if (timData.getDetectives().contains(sender)) {
                    BasicInventory detectiveShop = inventoryHandler.createInventory(BasicInventory.class, sender.getPlayer(), ChatColor.BLUE + "Detective Shop", 9);
                }
            });
        });
    }
}

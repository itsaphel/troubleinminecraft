package io.indices.troubleinminecraft.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.google.inject.Injector;
import io.indices.troubleinminecraft.TroubleInMinecraftPlugin;
import io.indices.troubleinminecraft.game.TIMData;
import io.indices.troubleinminecraft.game.TIMPlayer;
import io.indices.troubleinminecraft.shop.DetectiveShop;
import io.indices.troubleinminecraft.shop.TraitorShop;
import io.indices.troubleinminecraft.shop.items.ShopItem;
import com.voxelgameslib.voxelgameslib.components.ability.Ability;
import com.voxelgameslib.voxelgameslib.components.inventory.BasicInventory;
import com.voxelgameslib.voxelgameslib.components.inventory.InventoryHandler;
import com.voxelgameslib.voxelgameslib.game.Game;
import com.voxelgameslib.voxelgameslib.game.GameHandler;
import com.voxelgameslib.voxelgameslib.user.GamePlayer;
import com.voxelgameslib.voxelgameslib.user.User;
import com.voxelgameslib.voxelgameslib.utils.ChatUtil;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

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
    public void openShop(User sender) {
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

                    if (isTraitor) {
                        title = ChatColor.DARK_RED + "Traitor Shop";
                    } else {
                        title = ChatColor.BLUE + "Detective Shop";
                    }

                    BasicInventory shopInv = inventoryHandler.createInventory(BasicInventory.class, sender.getPlayer(), title, 9);
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
                            TIMPlayer timPlayer = timData.getPlayerMap().get(sender);
                            if (item.getCost() <= timPlayer.getCredits()) {
                                // woopie, time to buy
                                item.getAbilities().forEach(aClass -> {
                                    try {
                                        Ability ability = aClass.getConstructor(User.class).newInstance(sender);
                                        injector.injectMembers(ability);
                                        ability.start();
                                        Bukkit.getPluginManager().registerEvents(ability, plugin);
                                        game.getActivePhase().addTickable(ability.getIdentifier(), ability);

                                        timPlayer.setCredits(timPlayer.getCredits() - item.getCost());
                                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                                        e.printStackTrace();
                                    }
                                });
                                ChatUtil.sendMessage((GamePlayer) sender, TextComponent.of("You have bought " + item.getName() + ".").color(TextColor.GREEN));
                            } else {
                                ChatUtil.sendMessage((GamePlayer) sender, TextComponent.of("You do not have enough credits to purchase this item! You need " + (item.getCost() - timPlayer.getCredits()) + " more credits.").color(TextColor.RED));
                            }

                            shopInv.close();
                            inventoryHandler.removeInventory(shopInv.getIdentifier());
                        });
                    }

                    sender.getPlayer().openInventory(shopInv.getBukkitInventory());
                }
            });
        }
    }
}

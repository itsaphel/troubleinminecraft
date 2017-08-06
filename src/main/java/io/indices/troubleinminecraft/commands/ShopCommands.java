package io.indices.troubleinminecraft.commands;

import com.google.inject.Injector;

import com.voxelgameslib.voxelgameslib.components.ability.Ability;
import com.voxelgameslib.voxelgameslib.components.inventory.BasicInventory;
import com.voxelgameslib.voxelgameslib.components.inventory.InventoryHandler;
import com.voxelgameslib.voxelgameslib.game.Game;
import com.voxelgameslib.voxelgameslib.game.GameHandler;
import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.user.User;

import net.kyori.text.LegacyComponent;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.bukkit.Bukkit;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import io.indices.troubleinminecraft.TroubleInMinecraftPlugin;
import io.indices.troubleinminecraft.game.TIMData;
import io.indices.troubleinminecraft.game.TIMPlayer;
import io.indices.troubleinminecraft.lang.TIMLangKey;
import io.indices.troubleinminecraft.shop.DetectiveShop;
import io.indices.troubleinminecraft.shop.TraitorShop;
import io.indices.troubleinminecraft.shop.items.ShopItem;

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

                    if (isTraitor) {
                        title = LegacyComponent.to(Lang.trans(TIMLangKey.SHOP_TRAITOR_INV_TITLE));
                    } else {
                        title = LegacyComponent.to(Lang.trans(TIMLangKey.SHOP_DETECTIVE_INV_TITLE));
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
                                Lang.msg(sender, TIMLangKey.SHOP_YOU_HAVE_BOUGHT_X, item.getName());
                            } else {
                                Lang.msg(sender, TIMLangKey.SHOP_YOU_DO_NOT_HAVE_ENOUGH_CREDITS, item.getCost() - timPlayer.getCredits());
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

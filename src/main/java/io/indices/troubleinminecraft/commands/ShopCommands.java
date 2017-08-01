package io.indices.troubleinminecraft.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.google.inject.Injector;
import io.indices.troubleinminecraft.TroubleInMinecraftPlugin;
import io.indices.troubleinminecraft.game.TIMData;
import io.indices.troubleinminecraft.shop.DetectiveShop;
import io.indices.troubleinminecraft.shop.TraitorShop;
import io.indices.troubleinminecraft.shop.items.ShopItem;
import me.minidigger.voxelgameslib.components.ability.Ability;
import me.minidigger.voxelgameslib.components.inventory.BasicInventory;
import me.minidigger.voxelgameslib.components.inventory.InventoryHandler;
import me.minidigger.voxelgameslib.game.GameHandler;
import me.minidigger.voxelgameslib.user.GamePlayer;
import me.minidigger.voxelgameslib.user.User;
import me.minidigger.voxelgameslib.utils.ChatUtil;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;
import org.bukkit.ChatColor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

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
    public void openShop(User sender) {
        gameHandler.findGame(sender, TroubleInMinecraftPlugin.GAMEMODE).ifPresent(game -> {
            game.getGameData(TIMData.class).ifPresent(timData -> {
                boolean isTraitor = timData.getTraitors().contains(sender);
                boolean isDetective = timData.getDetectives().contains(sender);

                if (isTraitor || isDetective) {
                    int index = 0;
                    String title;

                    if (isTraitor) {
                        title = ChatColor.RED + "Traitor Shop";
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
                            int credits = timData.getPlayerCredits().getOrDefault(sender, 0);
                            if (item.getCost() <= credits) {
                                // woopie, time to buy
                                sender.getPlayer().getInventory().addItem(item.getItemStack());
                                item.getAbilities().forEach(aClass -> {
                                    try {
                                        Ability ability = aClass.getConstructor(User.class).newInstance(sender);
                                        game.getActivePhase().addTickable(ability);
                                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                                        e.printStackTrace();
                                    }
                                });
                                ChatUtil.sendMessage((GamePlayer) sender, TextComponent.of("You have bought " + item.getName() + ".").color(TextColor.GREEN));
                            } else {
                                ChatUtil.sendMessage((GamePlayer) sender, TextComponent.of("You do not have enough credits to purchase this item! You need " + (item.getCost() - credits) + " more credits.").color(TextColor.RED));
                            }

                            shopInv.close(sender.getPlayer());
                            shopInv.destroy();
                        });
                    }

                    sender.getPlayer().openInventory(shopInv.getBukkitInventory());
                }
            });
        });
    }
}

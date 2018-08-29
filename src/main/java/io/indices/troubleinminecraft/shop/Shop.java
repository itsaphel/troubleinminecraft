package io.indices.troubleinminecraft.shop;

import com.voxelgameslib.voxelgameslib.user.User;
import com.voxelgameslib.voxelgameslib.user.UserHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.inventivetalent.menubuilder.inventory.InventoryMenuBuilder;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class Shop {
    @Inject
    private UserHandler userHandler;

    private InventoryMenuBuilder inventoryBuilder;

    private String title;
    private Currency currency;
    private int nextSlot = 0;
    private Map<ItemStack, Item> items = new HashMap<>();
    private BiConsumer<User, Item> purchaseAction;

    public Shop() {
        this.inventoryBuilder = new InventoryMenuBuilder();
    }

    public Shop title(String title) {
        this.title = title;
        this.inventoryBuilder.withTitle(title);
        return this;
    }

    public Shop currency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public Map<ItemStack, Item> items() {
        return items;
    }

    public Shop addItem(Item item) {
        items.put(item.itemStack(), item);
        item.setSlot(nextSlot);
        nextSlot++;

        return this;
    }

    public Shop onPurchase(BiConsumer<User, Item> action) {
        // todo, check if player can afford etc.
        purchaseAction = action;
        return this;
    }

    public void purchase(User purchaser, ItemStack itemStack) {
        Item item = items.get(itemStack);

        if (item != null) {
            purchaseAction.accept(purchaser, item);
        }
    }

    public Shop make() {
        items.forEach(((itemStack, item) -> {
            inventoryBuilder.withItem(item.getSlot(), itemStack, (player, clickType, boughtItemStack) -> {
                userHandler.getUser(player.getUniqueId()).ifPresent(user -> purchase(user, boughtItemStack));
            });
        }));

        return this;
    }

    public void openForPlayer(Player player) {
        inventoryBuilder.show(player);
    }
}

package io.indices.troubleinminecraft.shop;

import com.voxelgameslib.voxelgameslib.components.inventory.BasicInventory;
import com.voxelgameslib.voxelgameslib.components.inventory.InventoryHandler;
import com.voxelgameslib.voxelgameslib.user.User;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class Shop {
    private InventoryHandler inventoryHandler;

    private String title;
    private Currency currency;
    private Map<ItemStack, Item> items = new HashMap<>();
    private BiConsumer<User, Item> purchaseAction;

    /**
     * Initialise dependencies. We gotta do this until we can get modules the ability to inject Guice dependencies
     * or a custom injector
     *
     * @param inventoryHandler dependency
     */
    public Shop(InventoryHandler inventoryHandler) {
        this.inventoryHandler = inventoryHandler;
    }

    public Shop title(String title) {
        this.title = title;
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

        purchaser.getPlayer().closeInventory();
    }

    public BasicInventory make(User user) {
        BasicInventory inventory = inventoryHandler.createInventory(BasicInventory.class, user, title, items.size());

        items.forEach(((itemStack, item) -> inventory.addClickAction(itemStack, (is, u) -> purchase(u, is))));

        return inventory;
    }
}

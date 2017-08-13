package io.indices.troubleinminecraft.shop;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.inject.Inject;

import com.voxelgameslib.voxelgameslib.components.inventory.BasicInventory;
import com.voxelgameslib.voxelgameslib.components.inventory.InventoryHandler;
import com.voxelgameslib.voxelgameslib.user.User;

import org.bukkit.inventory.ItemStack;

public class Shop {

    @Inject
    private InventoryHandler inventoryHandler; // won't actually work, but it's ok temp while we get this working

    private String title;
    private Currency currency;
    private Map<ItemStack, Item> items;
    private Map<ItemStack, Consumer<User>> purchaseActions = new HashMap<>();

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

    public Shop onPurchase(ItemStack item, Consumer<User> action) {
        // todo, check if player can afford etc.
        purchaseActions.put(item, action);
        return this;
    }

    public void purchase(User purchaser, ItemStack item) {
        purchaseActions.get(item).accept(purchaser);
        purchaser.getPlayer().closeInventory();
    }

    public BasicInventory make(User user) {
        BasicInventory inventory = inventoryHandler.createInventory(BasicInventory.class, user, title, items.size());
        purchaseActions.forEach(((itemStack, userConsumer) -> inventory.addClickAction(itemStack, (is, u) -> purchase(u, is))));
        return inventory;
    }
}

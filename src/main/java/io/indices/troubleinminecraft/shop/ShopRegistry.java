package io.indices.troubleinminecraft.shop;

import com.google.inject.Injector;

import com.voxelgameslib.voxelgameslib.components.ability.Ability;
import com.voxelgameslib.voxelgameslib.components.inventory.InventoryHandler;
import com.voxelgameslib.voxelgameslib.game.Game;
import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.user.User;
import com.voxelgameslib.voxelgameslib.utils.ItemBuilder;
import com.voxelgameslib.voxelgameslib.utils.db.DB;
import com.voxelgameslib.voxelgameslib.utils.db.DbRow;

import net.kyori.text.LegacyComponent;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.inject.Inject;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import io.indices.troubleinminecraft.TroubleInMinecraftPlugin;
import io.indices.troubleinminecraft.abilities.BodyArmourAbility;
import io.indices.troubleinminecraft.abilities.C4Ability;
import io.indices.troubleinminecraft.abilities.CreeperEggAbility;
import io.indices.troubleinminecraft.abilities.DisguiserAbility;
import io.indices.troubleinminecraft.abilities.JihadAbility;
import io.indices.troubleinminecraft.abilities.KnifeAbility;
import io.indices.troubleinminecraft.abilities.KnockbackStickAbility;
import io.indices.troubleinminecraft.abilities.RadarAbility;
import io.indices.troubleinminecraft.game.TIMData;
import io.indices.troubleinminecraft.game.TIMPlayer;
import io.indices.troubleinminecraft.lang.TIMLangKey;
import lombok.Data;

@Data
public class ShopRegistry {
    @Inject
    private Injector injector;
    @Inject
    private InventoryHandler inventoryHandler;
    @Inject
    private TroubleInMinecraftPlugin plugin;

    private Game game;
    private Shop traitorShop, detectiveShop, pointsShop;

    private Map<UUID, Item> playerItemCache = new HashMap<>();

    /**
     * Register the ShopRegistry
     *
     * @param game the game this registry applies to
     */
    public void register(Game game) {
        this.game = game;
        refreshPointShopCache();

        Item bodyArmour = new Item()
            .cost(1)
            .itemStack(new ItemBuilder(Material.IRON_CHESTPLATE).amount(1).name(LegacyComponent.to(Lang.trans(TIMLangKey.ITEM_BODY_ARMOUR_TITLE))).build())
            .addAbility(BodyArmourAbility.class);
        Item c4 = new Item()
            .cost(2)
            .itemStack(C4Ability.ITEM_STACK)
            .addAbility(C4Ability.class);
        Item disguiser = new Item()
            .cost(1)
            .itemStack(new ItemBuilder(Material.SKULL_ITEM).amount(1).name(LegacyComponent.to(Lang.trans(TIMLangKey.ITEM_DISGUISER_TITLE))).lore(LegacyComponent.to(Lang.trans(TIMLangKey.ITEM_DISGUISER_LORE))).build())
            .addAbility(DisguiserAbility.class);
        Item knife = new Item()
            .cost(2)
            .itemStack(KnifeAbility.ITEM_STACK)
            .addAbility(KnifeAbility.class);
        Item creeperEgg = new Item()
            .cost(1)
            .itemStack(CreeperEggAbility.ITEM_STACK)
            .addAbility(CreeperEggAbility.class);
        Item jihad = new Item()
            .cost(2)
            .itemStack(JihadAbility.ITEM_STACK)
            .addAbility(JihadAbility.class);
        Item radar = new Item()
            .cost(1)
            .itemStack(RadarAbility.ITEM_STACK)
            .addAbility(RadarAbility.class);
        Item knockbackStick = new Item()
            .cost(1)
            .itemStack(KnockbackStickAbility.ITEM_STACK)
            .addAbility(KnockbackStickAbility.class);

        traitorShop = new Shop(inventoryHandler)
            .title(LegacyComponent.to(Lang.trans(TIMLangKey.SHOP_TRAITOR_INV_TITLE)))
            .currency(Currency.CREDITS)
            .addItem(bodyArmour)
            .addItem(disguiser)
            .addItem(radar)
            .addItem(knife)
            .addItem(creeperEgg)
            .addItem(c4)
            .addItem(jihad)
            .onPurchase(this::purchaseRoleItem);

        detectiveShop = new Shop(inventoryHandler)
            .title(LegacyComponent.to(Lang.trans(TIMLangKey.SHOP_DETECTIVE_INV_TITLE)))
            .currency(Currency.CREDITS)
            .addItem(bodyArmour)
            .addItem(knockbackStick)
            .onPurchase(this::purchaseRoleItem);

        pointsShop = new Shop(inventoryHandler)
            .title(LegacyComponent.to(Lang.trans(TIMLangKey.SHOP_POINTS_SHOP_INV_TITLE)))
            .currency(Currency.POINTS)
            .onPurchase(this::purchasePointsItem);
    }

    private void purchaseRoleItem(User user, Item item) {
        game.getGameData(TIMData.class).ifPresent(timData -> {
            if (!timData.isGameStarted()) {
                return;
            }

            TIMPlayer timPlayer = timData.getPlayerMap().get(user);

            if (item.cost() <= timPlayer.getCredits()) {
                // woopie, time to buy
                item.abilities().forEach(aClass -> {
                    try {
                        Ability ability = aClass.getConstructor(User.class).newInstance(user);
                        injector.injectMembers(ability);
                        ability.start();
                        Bukkit.getPluginManager().registerEvents(ability, plugin);
                        game.getActivePhase().addTickable(ability.getIdentifier(), ability);

                        timPlayer.setCredits(timPlayer.getCredits() - item.cost());
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                });
                Lang.msg(user, TIMLangKey.SHOP_YOU_HAVE_BOUGHT_X, item.itemStack().getItemMeta().getDisplayName());
            } else {
                Lang.msg(user, TIMLangKey.SHOP_YOU_DO_NOT_HAVE_ENOUGH_CREDITS, item.cost() - timPlayer.getCredits());
            }
        });
    }

    private void purchasePointsItem(User user, Item item) {
        playerItemCache.put(user.getUuid(), item);
        registerPurchase(user, item);
    }

    private void refreshPointShopCache() {
        TroubleInMinecraftPlugin.newChain()
            .asyncFirst(() -> {
                List<DbRow> results = new ArrayList<>();
                try {
                    results = DB.getResults("SELECT * FROM ttt_point_shop_purchases WHERE uuid IN ?", game.getPlayers().stream().map(u -> u.getUuid().toString()).collect(Collectors.joining(",")));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return results;
            })
            .syncLast((results) -> {
                Map<UUID, Item> tempCache = new HashMap<>();

                results.forEach(row -> tempCache.put(row.get("uuid"), row.get("item_id")));

                playerItemCache = tempCache;
            })
            .execute();
    }

    private void registerPurchase(User user, Item item) {
        DB.executeUpdateAsync("INSERT IGNORE INTO ttt_point_shop_purchases VALUES (?,?)", user.getUuid(), item.id());
    }
}

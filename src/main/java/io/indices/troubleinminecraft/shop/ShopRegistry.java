package io.indices.troubleinminecraft.shop;

import co.aikar.idb.DB;
import co.aikar.idb.DbRow;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.voxelgameslib.voxelgameslib.game.Game;
import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.user.User;
import com.voxelgameslib.voxelgameslib.utils.ItemBuilder;
import io.indices.troubleinminecraft.TroubleInMinecraftPlugin;
import io.indices.troubleinminecraft.abilities.BodyArmourAbility;
import io.indices.troubleinminecraft.abilities.C4Ability;
import io.indices.troubleinminecraft.abilities.CreeperEggAbility;
import io.indices.troubleinminecraft.abilities.DisguiserAbility;
import io.indices.troubleinminecraft.abilities.HarpoonAbility;
import io.indices.troubleinminecraft.abilities.JihadAbility;
import io.indices.troubleinminecraft.abilities.KnifeAbility;
import io.indices.troubleinminecraft.abilities.KnockbackStickAbility;
import io.indices.troubleinminecraft.abilities.RadarAbility;
import io.indices.troubleinminecraft.abilities.TTTAbility;
import io.indices.troubleinminecraft.abilities.modifiers.AbilityModifier;
import io.indices.troubleinminecraft.abilities.modifiers.TripleHarpoonModifier;
import io.indices.troubleinminecraft.game.TIMData;
import io.indices.troubleinminecraft.game.TIMPlayer;
import io.indices.troubleinminecraft.lang.TIMLangKey;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class ShopRegistry {
    @Inject
    private Injector injector;
    @Inject
    private TroubleInMinecraftPlugin plugin;

    private Game game;
    private Shop traitorShop, detectiveShop, pointsShop;

    private Map<UUID, PointShopUser> playerItemCache = new HashMap<>();
    private Map<String, Item> itemIdMap = new HashMap<>();

    /**
     * Register the ShopRegistry
     *
     * @param game the game this registry applies to
     */
    public void register(Game game) {
        this.game = game;
        refreshPointShopCache();

        // to readers: yes, i know, it's time to rework the ability API. i'll do it... soon (tm)

        Item bodyArmour = new Item()
                .id("body_armour")
                .cost(1)
                .itemStack(new ItemBuilder(Material.IRON_CHESTPLATE).amount(1).name(Lang.legacy(TIMLangKey.ITEM_BODY_ARMOUR_TITLE)).build())
                .ability(BodyArmourAbility.class);
        Item c4 = new Item()
                .id("c4")
                .cost(2)
                .itemStack(C4Ability.ITEM_STACK)
                .ability(C4Ability.class);
        Item disguiser = new Item()
                .id("disguiser")
                .cost(1)
                .itemStack(new ItemBuilder(Material.LEGACY_SKULL_ITEM).amount(1).name(Lang.legacy(TIMLangKey.ITEM_DISGUISER_TITLE)).lore(Lang.legacy(TIMLangKey.ITEM_DISGUISER_LORE)).build())
                .ability(DisguiserAbility.class);
        Item knife = new Item()
                .id("knife")
                .cost(2)
                .itemStack(KnifeAbility.ITEM_STACK)
                .ability(KnifeAbility.class);
        Item creeperEgg = new Item()
                .id("creeper_egg")
                .cost(1)
                .itemStack(CreeperEggAbility.ITEM_STACK)
                .ability(CreeperEggAbility.class);
        Item jihad = new Item()
                .id("jihad")
                .cost(2)
                .itemStack(JihadAbility.ITEM_STACK)
                .ability(JihadAbility.class);
        Item radar = new Item()
                .id("radar")
                .cost(1)
                .itemStack(RadarAbility.ITEM_STACK)
                .ability(RadarAbility.class);
        Item knockbackStick = new Item()
                .id("knockback_stick")
                .cost(1)
                .itemStack(KnockbackStickAbility.ITEM_STACK)
                .ability(KnockbackStickAbility.class);
        Item harpoon = new Item()
                .id("harpoon")
                .cost(2)
                .itemStack(HarpoonAbility.ITEM_STACK)
                .ability(HarpoonAbility.class);
        Item tripleHarpoonModifier = new Item()
                .id("triple_harpoon_modifier")
                .cost(1)
                .itemStack(new ItemBuilder(Material.SNOWBALL).amount(3).name("Triple Harpoon").lore("Fire 3 harpoons at once").build())
                .abilityModifier(TripleHarpoonModifier.class);

        itemIdMap.put(bodyArmour.id(), bodyArmour);
        itemIdMap.put(c4.id(), c4);
        itemIdMap.put(disguiser.id(), disguiser);
        itemIdMap.put(knife.id(), knife);
        itemIdMap.put(creeperEgg.id(), creeperEgg);
        itemIdMap.put(jihad.id(), jihad);
        itemIdMap.put(radar.id(), radar);
        itemIdMap.put(knockbackStick.id(), knockbackStick);
        itemIdMap.put(harpoon.id(), harpoon);
        itemIdMap.put(tripleHarpoonModifier.id(), tripleHarpoonModifier);

        traitorShop = new Shop()
                .title(Lang.legacy(TIMLangKey.SHOP_TRAITOR_INV_TITLE))
                .currency(Currency.CREDITS)
                .addItem(bodyArmour)
                .addItem(disguiser)
                .addItem(radar)
                .addItem(knife)
                .addItem(creeperEgg)
                .addItem(c4)
                .addItem(jihad)
                .addItem(harpoon)
                .onPurchase(this::purchaseRoleItem)
                .make();

        detectiveShop = new Shop()
                .title(Lang.legacy(TIMLangKey.SHOP_DETECTIVE_INV_TITLE))
                .currency(Currency.CREDITS)
                .addItem(bodyArmour)
                .addItem(knockbackStick)
                .onPurchase(this::purchaseRoleItem);

        pointsShop = new Shop()
                .title(Lang.legacy(TIMLangKey.SHOP_POINTS_SHOP_INV_TITLE))
                .currency(Currency.POINTS)
                .addItem(tripleHarpoonModifier)
                .onPurchase(this::purchasePointsItem);
    }

    private void purchaseRoleItem(User user, Item item) {
        game.getGameData(TIMData.class).ifPresent(timData -> {
            if (!timData.isGameStarted()) {
                return;
            }

            TIMPlayer timPlayer = timData.getPlayerMap().get(user);
            PointShopUser psUser = getPointShopUser(user.getUuid());

            if (item.cost() <= timPlayer.getCredits()) {
                // woopie, time to buy
                item.abilities().forEach(aClass -> {
                    try {
                        TTTAbility ability = aClass.getConstructor(User.class).newInstance(user);
                        injector.injectMembers(ability);

                        // can't use lambdas since we need to catch the exceptions properly
                        for (Class<? extends AbilityModifier> modClass : psUser.getModifiers()) {
                            if (ability.getAcceptedModifiers().contains(modClass)) {
                                ability.addModifier(modClass.newInstance());
                            }
                        }

                        ability.enable();
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
        PointShopUser psUser = getPointShopUser(user.getUuid());
        item.abilityModifiers().forEach(psUser::addModifier);
        registerPurchase(user, item);
    }

    private void refreshPointShopCache() {
        TroubleInMinecraftPlugin.newChain()
                .asyncFirst(() -> {
                    List<DbRow> results = new ArrayList<>();
                    try {
                        results = DB.getResults("SELECT * FROM ttt_point_shop_purchases WHERE uuid IN (?)", game.getPlayers().stream().map(u -> u.getUuid().toString()).collect(Collectors.joining(",")));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return results;
                })
                .syncLast((results) -> {
                    Map<UUID, PointShopUser> tempCache = new HashMap<>();

                    results.forEach(row -> {
                        UUID uuid = UUID.fromString(row.get("uuid"));

                        PointShopUser psUser = getPointShopUser(uuid);
                        itemIdMap.get((String) row.get("modifier_id")).abilityModifiers().forEach(psUser::addModifier);

                        tempCache.put(uuid, psUser);
                    });

                    playerItemCache = tempCache;
                })
                .execute();
    }

    private void registerPurchase(User user, Item item) {
        DB.executeUpdateAsync("INSERT IGNORE INTO ttt_point_shop_purchases VALUES (?,?)", user.getUuid().toString(), item.id());
    }

    private PointShopUser getPointShopUser(UUID uuid) {
        PointShopUser psUser = playerItemCache.get(uuid);

        if (psUser == null) {
            psUser = new PointShopUser();
            psUser.setUuid(uuid);
        }

        return psUser;
    }
}

package io.indices.troubleinminecraft.shop.items.role;

import com.google.inject.Injector;
import com.voxelgameslib.voxelgameslib.components.ability.Ability;
import com.voxelgameslib.voxelgameslib.game.Game;
import com.voxelgameslib.voxelgameslib.game.GameHandler;
import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.user.User;
import io.indices.troubleinminecraft.TroubleInMinecraftPlugin;
import io.indices.troubleinminecraft.game.TIMData;
import io.indices.troubleinminecraft.game.TIMPlayer;
import io.indices.troubleinminecraft.lang.TIMLangKey;
import io.indices.troubleinminecraft.shop.items.ShopItem;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class RoleItem extends ShopItem {
    @Inject
    private Injector injector;

    private List<Class<? extends Ability>> abilities = new ArrayList<>();

    RoleItem() {
        //
    }

    @Nonnull
    public List<Class<? extends Ability>> getAbilities() {
        return abilities;
    }

    public <T extends Ability> void addAbility(@Nonnull Class<T> abilityClass) {
        abilities.add(abilityClass);
    }

    @Override
    public void purchase(User buyer) {
        Game game = injector.getInstance(GameHandler.class).getGames(buyer.getUuid(), false).get(0);

        game.getGameData(TIMData.class).ifPresent(timData -> {
            TIMPlayer timPlayer = timData.getPlayerMap().get(buyer);
            if (getCost() <= timPlayer.getCredits()) {
                // woopie, time to buy
                getAbilities().forEach(aClass -> {
                    try {
                        Ability ability = aClass.getConstructor(User.class).newInstance(buyer);
                        injector.injectMembers(ability);
                        ability.start();
                        Bukkit.getPluginManager().registerEvents(ability, injector.getInstance(TroubleInMinecraftPlugin.class));
                        game.getActivePhase().addTickable(ability.getIdentifier(), ability);

                        timPlayer.setCredits(timPlayer.getCredits() - getCost());
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                });
                Lang.msg(buyer, TIMLangKey.SHOP_YOU_HAVE_BOUGHT_X, getName());
            } else {
                Lang.msg(buyer, TIMLangKey.SHOP_YOU_DO_NOT_HAVE_ENOUGH_CREDITS, getCost() - timPlayer.getCredits());
            }
        });
    }
}

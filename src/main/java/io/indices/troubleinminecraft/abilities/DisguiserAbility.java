package io.indices.troubleinminecraft.abilities;

import com.google.inject.Injector;
import com.voxelgameslib.voxelgameslib.components.ability.Ability;
import com.voxelgameslib.voxelgameslib.game.Game;
import com.voxelgameslib.voxelgameslib.game.GameHandler;
import com.voxelgameslib.voxelgameslib.user.User;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class DisguiserAbility extends TTTAbility {

    @Inject
    private Injector injector;

    /**
     * @see Ability#Ability(User)
     */
    public DisguiserAbility(@Nonnull User user) {
        super(user);
    }

    @Override
    public void start() {
        if (Bukkit.getPluginManager().getPlugin("LibsDisguises") == null) {
            return;
        }
        Game game = injector.getInstance(GameHandler.class).getGames(affected.getUuid(), false).get(0);

        PlayerDisguise playerDisguise;
        int random = ThreadLocalRandom.current().nextInt(game.getPlayers().stream().filter(u -> !u.getUuid().equals(affected.getUuid())).collect(Collectors.toList()).size());
        playerDisguise = new PlayerDisguise(game.getPlayers().get(random).getPlayer().getName());

        DisguiseAPI.disguiseToPlayers(affected.getPlayer(), playerDisguise, game.getAllUsers().stream().filter(u -> !u.getUuid().equals(affected.getUuid())).map(User::getPlayer).collect(Collectors.toList()));
    }

    @Override
    public void stop() {
        DisguiseAPI.undisguiseToAll(affected.getPlayer());
    }

    @Override
    public void tick() {

    }
}

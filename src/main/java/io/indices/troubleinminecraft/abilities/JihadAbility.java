package io.indices.troubleinminecraft.abilities;

import com.voxelgameslib.voxelgameslib.components.ability.Ability;
import com.voxelgameslib.voxelgameslib.game.GameHandler;
import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.user.User;
import com.voxelgameslib.voxelgameslib.utils.ItemBuilder;
import io.indices.troubleinminecraft.lang.TIMLangKey;
import net.kyori.text.LegacyComponent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class JihadAbility extends ExplosionAbility {
    public static ItemStack ITEM_STACK = new ItemBuilder(Material.TNT).amount(1).name(LegacyComponent.to(Lang.trans(TIMLangKey.ITEM_C4_TITLE))).lore(LegacyComponent.to(Lang.trans(TIMLangKey.ITEM_C4_LORE))).build();

    @Inject
    private GameHandler gameHandler;

    /**
     * @see Ability#Ability(User)
     */
    public JihadAbility(@Nonnull User user) {
        super(user);
    }

    @Override
    public void start() {
        affected.getPlayer().getInventory().addItem(ITEM_STACK);
    }

    @Override
    public void stop() {

    }

    @Override
    public void tick() {

    }

    @EventHandler
    public void onTrigger(PlayerInteractEvent event) {
        if (event.getPlayer().getUniqueId().equals(affected.getUuid())) {
            // leeeeeeroyyy jeeeenkins
            //
            // a bit like the game of thrones scene last weekend lmao
            // that guy, jaime, everyone be like: "yo m8, run away, it's a fucking dragon... retreat!!!"
            // jaime: fuck this world. *charges at dragon* *dragon turns around* *burns him in flames*
            // rip jaime
            // he's still alive tho, which is cool, i like jaime
            // anyway, back to writing code
            // if ur still reading this, idk, i hope u were entertained, ty for reading, but srsly just skip to the code below
            // p.s. vid for reference if u dont watch got (wat ru doing with ur life??): https://www.youtube.com/watch?v=-lFjRnQKBsQ

            explode(gameHandler, event.getPlayer().getLocation(), 7);
        }
    }
}

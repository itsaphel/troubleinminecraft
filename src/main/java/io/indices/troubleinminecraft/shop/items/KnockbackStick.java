package io.indices.troubleinminecraft.shop.items;

import com.voxelgameslib.voxelgameslib.lang.Lang;
import io.indices.troubleinminecraft.abilities.KnockbackStickAbility;
import io.indices.troubleinminecraft.lang.TIMLangKey;
import net.kyori.text.LegacyComponent;

public class KnockbackStick extends ShopItem {
    public KnockbackStick() {
        name = LegacyComponent.to(Lang.trans(TIMLangKey.ITEM_KNOCKBACK_STICK_TITLE));
        cost = 1;
        itemStack = KnockbackStickAbility.itemStack;
        addAbility(KnockbackStickAbility.class);
    }
}

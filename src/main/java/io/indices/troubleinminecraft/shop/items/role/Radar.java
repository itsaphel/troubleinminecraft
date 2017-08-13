package io.indices.troubleinminecraft.shop.items.role;

import com.voxelgameslib.voxelgameslib.lang.Lang;
import io.indices.troubleinminecraft.abilities.RadarAbility;
import io.indices.troubleinminecraft.lang.TIMLangKey;
import net.kyori.text.LegacyComponent;

public class Radar extends RoleItem {
    public Radar() {
        name = LegacyComponent.to(Lang.trans(TIMLangKey.ITEM_RADAR_TITLE));
        cost = 1;
        itemStack = RadarAbility.ITEM_STACK;
        addAbility(RadarAbility.class);
    }
}

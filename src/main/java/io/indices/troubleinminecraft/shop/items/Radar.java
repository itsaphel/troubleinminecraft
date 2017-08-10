package io.indices.troubleinminecraft.shop.items;

import com.voxelgameslib.voxelgameslib.lang.Lang;
import io.indices.troubleinminecraft.abilities.RadarAbility;
import io.indices.troubleinminecraft.lang.TIMLangKey;
import net.kyori.text.LegacyComponent;

public class Radar extends ShopItem {
    public Radar() {
        name = LegacyComponent.to(Lang.trans(TIMLangKey.ITEM_RADAR_TITLE));
        cost = 1;
        itemStack = RadarAbility.ITEM_STACK;
        addAbility(RadarAbility.class);
    }
}

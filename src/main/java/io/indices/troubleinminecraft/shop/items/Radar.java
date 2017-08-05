package io.indices.troubleinminecraft.shop.items;

import com.voxelgameslib.voxelgameslib.lang.Lang;
import io.indices.troubleinminecraft.abilities.RadarAbility;
import io.indices.troubleinminecraft.lang.TIMLangKey;

public class Radar extends ShopItem {
    public Radar() {
        name = Lang.string(TIMLangKey.ITEM_RADAR_TITLE);
        cost = 1;
        itemStack = RadarAbility.itemStack;
        addAbility(RadarAbility.class);
    }
}

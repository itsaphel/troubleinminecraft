package io.indices.troubleinminecraft.shop.items.points;

import com.voxelgameslib.voxelgameslib.components.ability.Ability;
import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.utils.ItemBuilder;
import io.indices.troubleinminecraft.abilities.HarpoonAbility;
import io.indices.troubleinminecraft.lang.TIMLangKey;
import net.kyori.text.LegacyComponent;
import org.bukkit.Material;

public class TripleHarpoon extends PointItem {
    public TripleHarpoon() {
        name = "Triple Harpoon";
        cost = 1;
        itemStack = new ItemBuilder(Material.SNOW_BALL)
                .amount(3)
                .name(LegacyComponent.to(Lang.trans(TIMLangKey.ITEM_TRIPLE_HARPOON_TITLE)))
                .lore(LegacyComponent.to(Lang.trans(TIMLangKey.ITEM_TRIPLE_HARPOON_LORE)))
                .build();
        affects = HarpoonAbility.class;
    }

    @Override
    public void affect(Ability ability) {
        if(ability instanceof HarpoonAbility) {
            ((HarpoonAbility) ability).setQuantity(3);
        }
    }
}

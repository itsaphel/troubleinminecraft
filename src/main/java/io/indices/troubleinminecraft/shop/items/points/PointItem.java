package io.indices.troubleinminecraft.shop.items.points;

import com.voxelgameslib.voxelgameslib.components.ability.Ability;
import com.voxelgameslib.voxelgameslib.user.User;
import io.indices.troubleinminecraft.shop.items.ShopItem;

public abstract class PointItem extends ShopItem {
    protected boolean multipleLevels = false;
    protected int costMultiplier = 1;
    protected int levels;
    protected int levelMultiplier = 1;
    protected int modifier = 100;
    protected Class affects;

    abstract public void affect(Ability ability);

    public void purchase(User buyer) {

    }
}

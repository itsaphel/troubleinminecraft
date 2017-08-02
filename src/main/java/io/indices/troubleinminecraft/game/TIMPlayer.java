package io.indices.troubleinminecraft.game;

import io.indices.troubleinminecraft.team.Role;
import lombok.Getter;
import lombok.Setter;
import me.minidigger.voxelgameslib.user.User;

@Getter
@Setter
public class TIMPlayer {
    private User user;
    private Role role;
    private boolean alive = true;
    private int credits = 0;

    public TIMPlayer(User user) {
        this.user = user;
    }
}

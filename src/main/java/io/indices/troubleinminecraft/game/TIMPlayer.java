package io.indices.troubleinminecraft.game;

import io.indices.troubleinminecraft.team.Role;
import lombok.Getter;
import lombok.Setter;
import com.voxelgameslib.voxelgameslib.user.User;

@Getter
@Setter
public class TIMPlayer {
    private User user;
    private Role role;
    private boolean alive = true;
    private int credits = 0;
    private int karma = 1000; // todo persist karma for 20 games
    private int kills = 0;

    public TIMPlayer(User user) {
        this.user = user;
    }
}

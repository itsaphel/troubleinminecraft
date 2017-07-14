package io.indices.troubleinminecraft.player;

import io.indices.troubleinminecraft.team.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeadPlayer {
    private UUID uuid;
    private String displayName;
    private Role role;
    private boolean identified;
}

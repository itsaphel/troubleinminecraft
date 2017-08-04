package io.indices.troubleinminecraft.game;

import java.util.UUID;

import io.indices.troubleinminecraft.team.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeadPlayer {
    private UUID uuid;
    private String displayName;
    private Role role;
    private boolean identified;
}

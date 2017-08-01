package io.indices.troubleinminecraft.game;

import io.indices.troubleinminecraft.team.Role;
import lombok.Getter;
import lombok.Setter;

import me.minidigger.voxelgameslib.components.team.Team;
import me.minidigger.voxelgameslib.game.GameData;
import me.minidigger.voxelgameslib.map.Vector3D;
import me.minidigger.voxelgameslib.user.User;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TIMData implements GameData {
    private Team innocentTeam;
    private Team traitorTeam;
    private Team detectiveTeam;
    private List<User> innocents = new ArrayList<>();
    private List<User> traitors = new ArrayList<>();
    private List<User> detectives = new ArrayList<>();
    private List<User> aliveInnocents = new ArrayList<>();
    private List<User> aliveTraitors = new ArrayList<>();
    private List<Vector3D> chests = new ArrayList<>();
    private Map<Entity, DeadPlayer> zombiePlayerMap = new HashMap<>();

    private Role winner;
    private boolean gameStarted = false;
}

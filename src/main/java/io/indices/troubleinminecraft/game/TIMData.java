package io.indices.troubleinminecraft.game;

import io.indices.troubleinminecraft.team.Role;
import lombok.Getter;
import lombok.Setter;

import com.voxelgameslib.voxelgameslib.components.team.Team;
import com.voxelgameslib.voxelgameslib.game.GameData;
import com.voxelgameslib.voxelgameslib.user.User;
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

    /* todo remove these in favour of Teams */
    private List<User> innocents = new ArrayList<>();
    private List<User> traitors = new ArrayList<>();
    private List<User> detectives = new ArrayList<>();
    /* end removal */

    private List<User> aliveInnocents = new ArrayList<>();
    private List<User> aliveTraitors = new ArrayList<>();
    private Map<User, TIMPlayer> playerMap = new HashMap<>();
    private Map<Entity, DeadPlayer> zombiePlayerMap = new HashMap<>();

    private Role winner;
    private boolean gameStarted = false;
}

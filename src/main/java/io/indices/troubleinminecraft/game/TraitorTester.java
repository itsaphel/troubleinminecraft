package io.indices.troubleinminecraft.game;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

import org.bukkit.Location;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TraitorTester {
    private Location button;
    private List<Location> lights = new ArrayList<>();
    private List<Location> barriers = new ArrayList<>();
    private boolean inUse = false;

    public void addLightLocation(@Nonnull Location location) {
        lights.add(location);
    }

    public void addBarierLocation(@Nonnull Location location) {
        barriers.add(location);
    }
}

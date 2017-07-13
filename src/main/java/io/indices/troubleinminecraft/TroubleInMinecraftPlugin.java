package io.indices.troubleinminecraft;

import me.minidigger.voxelgameslib.game.GameHandler;
import me.minidigger.voxelgameslib.game.GameMode;
import me.minidigger.voxelgameslib.module.Module;
import me.minidigger.voxelgameslib.module.ModuleHandler;
import me.minidigger.voxelgameslib.module.ModuleInfo;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@ModuleInfo(name = "TroubleInMinecraft", authors = "aphel", version = "1.0")
public class TroubleInMinecraftPlugin extends JavaPlugin implements Module {
    public static final GameMode GAMEMODE = new GameMode("TroubleInMinecraft", TroubleInMinecraftGame.class);

    @Inject
    private GameHandler gameHandler;

    @Override
    public void onLoad() {
        ModuleHandler.offerModule(this);
    }

    @Override
    public void enable() {
        gameHandler.registerGameMode(GAMEMODE);
    }

    @Override
    public void disable() {
        //
    }
}

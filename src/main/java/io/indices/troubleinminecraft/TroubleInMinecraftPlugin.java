package io.indices.troubleinminecraft;

import co.aikar.commands.BukkitCommandManager;
import com.google.inject.Injector;
import io.indices.troubleinminecraft.commands.ShopCommands;
import com.voxelgameslib.voxelgameslib.game.GameHandler;
import com.voxelgameslib.voxelgameslib.game.GameMode;
import com.voxelgameslib.voxelgameslib.module.Module;
import com.voxelgameslib.voxelgameslib.module.ModuleHandler;
import com.voxelgameslib.voxelgameslib.module.ModuleInfo;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@ModuleInfo(name = "TroubleInMinecraft", authors = "aphel", version = "1.0")
public class TroubleInMinecraftPlugin extends JavaPlugin implements Module {
    public static final GameMode GAMEMODE = new GameMode("TroubleInMinecraft", TroubleInMinecraftGame.class);

    @Inject
    private Injector injector;
    @Inject
    private BukkitCommandManager commandManager;
    @Inject
    private GameHandler gameHandler;

    @Override
    public void onLoad() {
        ModuleHandler.offerModule(this);
    }

    @Override
    public void enable() {
        gameHandler.registerGameMode(GAMEMODE);

        registerCommands();
    }

    @Override
    public void disable() {
        //
    }

    private void registerCommands() {
        commandManager.registerCommand(injector.getInstance(ShopCommands.class));
    }
}

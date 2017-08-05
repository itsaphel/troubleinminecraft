package io.indices.troubleinminecraft;

import com.google.inject.Injector;

import com.voxelgameslib.voxelgameslib.game.GameHandler;
import com.voxelgameslib.voxelgameslib.game.GameMode;
import com.voxelgameslib.voxelgameslib.lang.LangHandler;
import com.voxelgameslib.voxelgameslib.module.Module;
import com.voxelgameslib.voxelgameslib.module.ModuleHandler;
import com.voxelgameslib.voxelgameslib.module.ModuleInfo;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.indices.troubleinminecraft.lang.TIMLangKey;
import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.BukkitCommandManager;
import io.indices.troubleinminecraft.commands.ShopCommands;

import java.io.File;

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
    @Inject
    private LangHandler langHandler;

    @Override
    public void onLoad() {
        ModuleHandler.offerModule(this);
    }

    @Override
    public void enable() {
        gameHandler.registerGameMode(GAMEMODE);

        langHandler.registerExternalLangProvider(TIMLangKey.TIM, new File(getDataFolder(), "lang"));
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

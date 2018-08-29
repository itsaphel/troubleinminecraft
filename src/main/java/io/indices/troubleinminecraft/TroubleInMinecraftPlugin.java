package io.indices.troubleinminecraft;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.idb.DB;
import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.voxelgameslib.voxelgameslib.game.GameHandler;
import com.voxelgameslib.voxelgameslib.game.GameMode;
import com.voxelgameslib.voxelgameslib.lang.LangHandler;
import com.voxelgameslib.voxelgameslib.module.Module;
import com.voxelgameslib.voxelgameslib.module.ModuleHandler;
import com.voxelgameslib.voxelgameslib.module.ModuleInfo;
import io.indices.troubleinminecraft.commands.ShopCommands;
import io.indices.troubleinminecraft.lang.TIMLangKey;
import org.bukkit.plugin.java.JavaPlugin;

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

    private static TaskChainFactory taskChainFactory;

    @Override
    public void onLoad() {
        ModuleHandler.offerModule(this);
    }

    @Override
    public void onEnable() {
        taskChainFactory = BukkitTaskChainFactory.create(this);
        initialiseDatabase();
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

    private void initialiseDatabase() {
        DB.executeUpdateAsync("CREATE TABLE IF NOT EXISTS `ttt_point_shop_purchases` (" +
                "`uuid` VARCHAR(36) NOT NULL," +
                "`modifier_id` VARCHAR(36) NOT NULL," +
                "UNIQUE KEY `uuid_modifier` (`uuid`, `modifier_id`)" +
                ")");
    }

    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }

    public static <T> TaskChain<T> newSharedChain(String name) {
        return taskChainFactory.newSharedChain(name);
    }
}

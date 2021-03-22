package com.xilitra.monsterslayer;

import com.xilitra.monsterslayer.cmd.MonsterCommand;
import com.xilitra.monsterslayer.config.CustomConfig;
import com.xilitra.monsterslayer.game.GameManager;
import com.xilitra.monsterslayer.gui.ShopGui;
import com.xilitra.monsterslayer.player.event.PlayerListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;

public class MonsterSlayer extends JavaPlugin {

    private static MonsterSlayer INSTANCE;
    private static HashMap<String, CustomConfig> customConfigs = new HashMap<>();
    public static FileConfiguration config ;


    @Override
    public void onEnable(){
        INSTANCE = this;
        getServer().getLogger().info("[MonsterSlayer] Plugin Starting...");
        saveDefaultConfig();

        if(!new File(getDataFolder() + File.separator + "data").exists()){
            new File(getDataFolder() + File.separator + "data").mkdirs();
        }
        loadLangConfig();
        loadShopConfig();
        GameManager.loadCache();

        registerCommand();
        registerListener();

        config = MonsterSlayer.getCustomConfig("message").getConfig();
    }


    public void registerListener(){
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new ShopGui(), this);
    }

    public void registerCommand(){
        getCommand("ms").setExecutor(new MonsterCommand());
    }

    public void loadLangConfig(){
        CustomConfig config = new CustomConfig("message").loadLangConfig();
        customConfigs.put("message", config);
    }

    public void loadShopConfig(){
        CustomConfig config = new CustomConfig("gui").loadShopConfig();
        customConfigs.put("gui", config);
    }

    public static MonsterSlayer getInstance() {
        return INSTANCE;
    }

    public static CustomConfig getCustomConfig(String name){
        return customConfigs.get(name);
    }

    public static CustomConfig createCustomConfig(String name, String folderName){
        CustomConfig config = new CustomConfig(name, folderName).loadConfig();
        customConfigs.put(name, config);
        return config;
    }

    public static CustomConfig createCustomConfig(String name){
        CustomConfig config = new CustomConfig(name).loadConfig();
        customConfigs.put(name, config);
        return config;
    }

    public static void addCacheConfig(CustomConfig customConfig){
        customConfigs.put(customConfig.getFileName(), customConfig);
    }

    public static FileConfiguration getMonsterConfig(){
        return MonsterSlayer.getInstance().getConfig();
    }


    @Override
    public void onDisable(){

    }
}

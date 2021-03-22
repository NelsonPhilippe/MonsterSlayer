package com.xilitra.monsterslayer.game;

import com.xilitra.monsterslayer.MonsterSlayer;
import com.xilitra.monsterslayer.config.CustomConfig;
import com.xilitra.monsterslayer.monster.Spawner;
import com.xilitra.monsterslayer.player.MonsterPlayer;
import com.xilitra.monsterslayer.waves.Wave;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameInstance {

    public static HashMap<Integer, GameInstance> gameInstanceCache = new HashMap<>();
    private GameState gameState;
    private int gameInstance;
    private String worldName;
    public List<Entity> mobspawned = new ArrayList<>();
    private List<MonsterPlayer> playerList = new ArrayList<>();
    private List<Wave> waves = new ArrayList<>();

    public GameInstance(GameState gameState, int gameInstance, String worldName) {
        this.gameState = gameState;
        this.gameInstance = gameInstance;
        this.worldName = worldName;
    }

    public void addMonsterPlayer(MonsterPlayer monsterPlayer){
        playerList.add(monsterPlayer);
    }

    public void removeMonsterPlayer(MonsterPlayer monsterPlayer){
        playerList.remove(monsterPlayer);
    }

    public void addWave(Wave wave){
        waves.add(wave);
    }

    public void removeWave(Wave wave){
        waves.remove(wave);
    }

    public List<Wave> getWaveList(){
        return waves;
    }

    public Wave getWave(int waveId){

        for(Wave waves : getWaveList()){

            if(waves.getWave() == waveId){
                return waves;
            }
        }

        return null;
    }

    public List<MonsterPlayer> getPlayerList(){
        return playerList;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public int getGameInstance() {
        return gameInstance;
    }

    public String getWorldName() {
        return worldName;
    }

    public static GameInstance getGameInstance(int instance){
        return GameInstance.gameInstanceCache.get(instance);
    }

    public void loadGameInstance(){
        CustomConfig config = MonsterSlayer.
                createCustomConfig("game-" + getGameInstance(), "data");
        FileConfiguration fileConfiguration = config.getConfig();

        fileConfiguration.createSection("instance");
        fileConfiguration.createSection("min-player") ;
        fileConfiguration.createSection("max-player");
        fileConfiguration.createSection("world");
        fileConfiguration.createSection("waiting-room.x");
        fileConfiguration.createSection("waiting-room.y");
        fileConfiguration.createSection("waiting-room.z");
        fileConfiguration.createSection("waiting-room.yaw");
        fileConfiguration.createSection("waiting-room.pitch");

        fileConfiguration.createSection("spawner");
        fileConfiguration.createSection("spawner.wave");

        int wave = MonsterSlayer.getMonsterConfig().getInt("waves");

        for(int i = 1; i < wave; i++){
            fileConfiguration.createSection("spawner.wave." + i);
            fileConfiguration.createSection("spawner.wave." + i + ".limit");
            fileConfiguration.createSection("spawner.wave."+ i + ".start-room.x");
            fileConfiguration.createSection("spawner.wave."+ i + ".start-room.y");
            fileConfiguration.createSection("spawner.wave."+ i + ".start-room.z");
            fileConfiguration.createSection("spawner.wave."+ i + ".start-room.yaw");
            fileConfiguration.createSection("spawner.wave."+ i + ".start-room.pitch");
            fileConfiguration.set("spawner.wave." + i + ".start-room.x", 0.0);
            fileConfiguration.set("spawner.wave." + i + ".start-room.y", 60.0);
            fileConfiguration.set("spawner.wave." + i + ".start-room.z", 0.0);
            fileConfiguration.set("spawner.wave." + i + ".start-room.yaw", 0.0);
            fileConfiguration.set("spawner.wave." + i + ".start-room.pitch", 0.0);
            fileConfiguration.set("spawner.wave." + i + ".limit", 20);
            addWave(new Wave(i, this, 20));
        }

        fileConfiguration.set("instance", getGameInstance());
        fileConfiguration.set("world", getWorldName());
        fileConfiguration.set("waiting-room.x", 0.0);
        fileConfiguration.set("waiting-room.y", 60.0);
        fileConfiguration.set("waiting-room.z", 0.0);
        fileConfiguration.set("waiting-room.yaw", 0.0);
        fileConfiguration.set("waiting-room.pitch", 0.0);
        fileConfiguration.set("min-player", 1);
        fileConfiguration.set("max-player", 2);


        try {
            fileConfiguration.save(config.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        GameInstance.gameInstanceCache.put(getGameInstance(), this);
    }

    public boolean instanceIsFull(){
        if(this.getInstanceConfig().getConfig().getInt("max-player") == playerList.size()){
            return true;
        }
        return false;
    }

    public CustomConfig getInstanceConfig(){
        return MonsterSlayer.getCustomConfig("game-" + getGameInstance());
    }

    public Location getWaitingRoomLocation(){
        FileConfiguration config = getInstanceConfig().getConfig();
        World world = Bukkit.getWorld(config.getString("world"));
        double x =  config.getDouble("waiting-room.x");
        double y =  config.getDouble("waiting-room.y");
        double z =  config.getDouble("waiting-room.z");
        float yaw = (float) config.getDouble("waiting.room.yaw");
        float pitch = (float) config.getDouble("waiting.room.pitch");


        return new Location(world, x, y, z, yaw, pitch);

    }

    public void setWaitingRoomLocation(Location loc){
        FileConfiguration config = getInstanceConfig().getConfig();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        config.set("waiting-room.x", x);
        config.set("waiting-room.y", y);
        config.set("waiting-room.z", z);
        config.set("waiting-room.yaw", loc.getYaw());
        config.set("waiting-room.pitch", loc.getPitch());

        try {
            config.save( getInstanceConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setStartRoomLocation(Location loc, Wave wave){
        FileConfiguration config = getInstanceConfig().getConfig();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        config.set("spawner.wave." + wave.getWave() + ".start-room.x", x);
        config.set("spawner.wave." + wave.getWave() + ".start-room.y", y);
        config.set("spawner.wave." + wave.getWave() + ".start-room.z", z);
        config.set("spawner.wave." + wave.getWave() + ".start-room.yaw", loc.getYaw());
        config.set("spawner.wave." + wave.getWave() + ".start-room.pitch", loc.getPitch());

        try {
            config.save( getInstanceConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Location getStartRoomLocation(Wave wave){
        FileConfiguration config = getInstanceConfig().getConfig();
        World world = Bukkit.getWorld(config.getString("world"));
        double x = config.getDouble("spawner.wave." + wave.getWave() + ".start-room.x");
        double y = config.getDouble("spawner.wave." + wave.getWave() + ".start-room.y");
        double z = config.getDouble("spawner.wave." + wave.getWave() + ".start-room.z");
        float yaw = Float.parseFloat(config.getString("spawner.wave." + wave.getWave() + ".start-room.yaw"));
        float pitch = Float.parseFloat(config.getString("spawner.wave." + wave.getWave() + ".start-room.pitch"));

        return new Location(world, x, y, z, yaw, pitch);

    }

}

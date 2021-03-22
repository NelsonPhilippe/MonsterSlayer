package com.xilitra.monsterslayer.game;


import com.xilitra.monsterslayer.MonsterSlayer;
import com.xilitra.monsterslayer.config.CustomConfig;
import com.xilitra.monsterslayer.game.task.*;
import com.xilitra.monsterslayer.monster.Boss;
import com.xilitra.monsterslayer.monster.Mob;
import com.xilitra.monsterslayer.monster.Spawner;
import com.xilitra.monsterslayer.player.MonsterPlayer;
import com.xilitra.monsterslayer.player.team.MSScoreboard;
import com.xilitra.monsterslayer.waves.Wave;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameManager {

    public static HashMap<GameInstance, GameManager> gameManagerCache = new HashMap<>();
    private BukkitTask waitingTask;
    private BukkitTask startTask;
    private BukkitTask waveTask;
    private BukkitTask shoptask;
    private BukkitTask endTask;
    private BukkitTask endtaskloose;
    private int waveNumber = 1;
    private GameInstance instance;


    public GameManager(GameInstance instance){
       this.instance = instance;
    }

    public static void loadCache(){
        MonsterSlayer plugin = MonsterSlayer.getInstance();

        try(Stream<Path> walk = Files.walk(Paths.get(plugin.getDataFolder() + File.separator + "data"))){

            List<File> result = walk.map(Path::toFile).collect(Collectors.toList());

            for(File fileGame : result){

                if(!fileGame.getName().equals("data")){

                    String fileName = fileGame.getName().replace(".yml", "");

                    CustomConfig config = new CustomConfig(fileName, "data").loadConfig();
                    FileConfiguration fileConfiguration = config.getConfig();
                    MonsterSlayer.addCacheConfig(config);

                    GameInstance gInstance = new GameInstance(GameState.WAITING, fileConfiguration.getInt("instance"),
                            fileConfiguration.getString("world"));
                    GameInstance.gameInstanceCache.put(fileConfiguration.getInt("instance"), gInstance);
                    GameManager gameManager = new GameManager(gInstance);
                    GameManager.gameManagerCache.put(gInstance, gameManager);

                    WaitingTask.isStartedCache.put(gInstance, false);

                    for(String waveSection : fileConfiguration.
                            getConfigurationSection("spawner.wave").getKeys(false)) {

                        System.out.println("--------------- game-" + gInstance.getGameInstance() + "---------------");
                        int waveId = Integer.parseInt(waveSection);
                        int waveLimitMob = fileConfiguration.getInt("spawner.wave." + waveId + ".limit");
                        Wave wave = new Wave(waveId, gInstance, waveLimitMob);

                        for (String spawnerID : fileConfiguration.
                                getConfigurationSection("spawner.wave." + waveSection).getKeys(false)) {

                            if(!spawnerID.equalsIgnoreCase("limit")) {

                                if (!spawnerID.equalsIgnoreCase("start-room")) {


                                    int locX = fileConfiguration.getInt("spawner.wave." + waveSection + "." + spawnerID + ".loc.x");
                                    int locY = fileConfiguration.getInt("spawner.wave." + waveSection + "." + spawnerID + ".loc.y");
                                    int locZ = fileConfiguration.getInt("spawner.wave." + waveSection + "." + spawnerID + ".loc.z");


                                    World world = Bukkit.getWorld(fileConfiguration.getString("world"));
                                    Location location = new Location(world, locX, locY, locZ);
                                    int spawnerId = Integer.parseInt(spawnerID);
                                    Spawner spawner = new Spawner(location, spawnerId, gInstance, waveId);
                                    System.out.println("wave: " + waveSection +
                                            ": x: " + locX + " | y: " + locY + " | z: " + locZ);

                                    for (String mobSection : fileConfiguration.
                                            getConfigurationSection("spawner.wave." + waveSection + "." + spawnerID + ".moblist").getKeys(false)) {

                                        String mobPath = "spawner.wave." + waveSection + "." + spawnerID + ".moblist." + mobSection;
                                        int levelmin = fileConfiguration.getInt(mobPath + ".levelmin");
                                        int levelmax = fileConfiguration.getInt(mobPath + ".levelmax");
                                        int chance = fileConfiguration.getInt(mobPath + ".chance");
                                        int blood = fileConfiguration.getInt(mobPath + ".blood");


                                        Mob mob = new Mob(mobSection, levelmin, levelmax, chance, wave, blood);
                                        spawner.addMob(mob);
                                    }

                                    for (String bossSection : fileConfiguration.
                                            getConfigurationSection("spawner.wave." + waveSection + "." + spawnerID + ".bosslist").getKeys(false)) {

                                        String bossPath = "spawner.wave." + waveSection + "." + spawnerID + ".bosslist." + bossSection;

                                        int levelmin = fileConfiguration.getInt(bossPath + ".levelmin");
                                        int levelmax = fileConfiguration.getInt(bossPath + ".levelmax");
                                        int chance = fileConfiguration.getInt(bossPath + ".chance");
                                        int blood = fileConfiguration.getInt(bossPath + ".blood");


                                        Boss boss = new Boss(bossSection, levelmin, levelmax, chance, wave, blood);
                                        spawner.addBoss(boss);
                                    }

                                    wave.addSpawner(spawner);

                                }

                            }
                        }
                        gInstance.addWave(wave);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runWaitingTask(){
        instance.setGameState(GameState.WAITING);
        MSScoreboard.updateScoreboard(instance);
        waitingTask = new WaitingTask(instance).runTaskTimer(MonsterSlayer.getInstance(), 20, 20);
    }

    public void runStartTask(){
        for(MonsterPlayer msPlayer : instance.getPlayerList()){
            msPlayer.getPlayer().teleport(instance.getStartRoomLocation(instance.getWave(waveNumber)));
        }
        instance.setGameState(GameState.START);
        MSScoreboard.updateScoreboard(instance);
        startTask = new StartTask(instance).runTaskTimer(MonsterSlayer.getInstance(), 20, 20);
    }

    public void runWaveTask(){
        instance.setGameState(GameState.WAVE);
        MSScoreboard.updateScoreboard(instance);
        instance.getWave(waveNumber).setCounterMob(instance.getWave(waveNumber).getLimitMob());
        instance.getWave(waveNumber).setCounterBoss(instance.getPlayerList().size());
        waveTask = new WaveTask(instance, waveNumber).runTaskTimer(MonsterSlayer.getInstance(), 20, 20);
    }

    public void runShopTask(){
        instance.setGameState(GameState.SHOP);
        MSScoreboard.updateScoreboard(instance);
        shoptask = new ShopTask(instance).runTaskTimer(MonsterSlayer.getInstance(), 20, 20);
    }

    public void stopWaitingTask(){
        waitingTask.cancel();
    }

    public void stopStartTask(){
        startTask.cancel();
    }

    public void stopWaveTask(){
        waveTask.cancel();
    }

    public void stoShopTask(){
        shoptask.cancel();
    }

    public void stopEndTask(){
        endTask.cancel();
    }

    public void runEndTask(){
        instance.setGameState(GameState.FINISH);
        MSScoreboard.updateScoreboard(instance);
        endTask = new EndTaskWin(instance).runTaskTimer(MonsterSlayer.getInstance(), 20, 20);
    }

    public void runEndTaskLoose(){
        instance.setGameState(GameState.FINISH);
        MSScoreboard.updateScoreboard(instance);
        endtaskloose = new EndTaskLoose(instance).runTaskTimer(MonsterSlayer.getInstance(), 20, 20);
    }

    public void stopEndTaskLoose(){
        endtaskloose.cancel();
    }

    public int getWaveNumber() {
        return waveNumber;
    }

    public void setWaveNumber(int waveNumber) {
        this.waveNumber = waveNumber;
    }

    public GameInstance getGameInstance() {
        return instance;
    }

    public BukkitTask getWaitingTask(){
        return waitingTask;
    }

    public static GameManager getGameManager(GameInstance instance){
        return GameManager.gameManagerCache.get(instance);
    }

}

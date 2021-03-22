package com.xilitra.monsterslayer.game.task;

import com.xilitra.monsterslayer.MonsterSlayer;
import com.xilitra.monsterslayer.game.GameInstance;
import com.xilitra.monsterslayer.game.GameManager;
import com.xilitra.monsterslayer.game.GameState;
import com.xilitra.monsterslayer.gui.GuiBuilder;
import com.xilitra.monsterslayer.monster.Mob;
import com.xilitra.monsterslayer.monster.Spawner;
import com.xilitra.monsterslayer.player.MonsterPlayer;
import com.xilitra.monsterslayer.utils.MonsterUtils;
import com.xilitra.monsterslayer.waves.Wave;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MobManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class WaveTask extends BukkitRunnable {

    private final FileConfiguration config = MonsterSlayer.getMonsterConfig();
    private final FileConfiguration messageConfig = MonsterSlayer.getCustomConfig("message").getConfig();
    private final GameInstance instance;
    private final int waveNumber;
    private int mobspawned = 0;
    private int timer = 0;

    public WaveTask(GameInstance instance, int waveNumber){
        this.instance = instance;
        this.waveNumber = waveNumber;
    }

    @Override
    public void run() {
        GameManager manager = GameManager.getGameManager(instance);
        Wave wave = instance.getWave(waveNumber);

        if(timer == 0){
            for(MonsterPlayer msPlayer : instance.getPlayerList()){
                msPlayer.sendTitle(getMessage("wave-start").replace("%wave-number%", String.valueOf(waveNumber)),
                        null, 20, 20, 20);

                if(!msPlayer.isAlive()){
                    msPlayer.getPlayer().setGameMode(GameMode.ADVENTURE);
                    msPlayer.getPlayer().teleport(instance.getStartRoomLocation(wave));
                }
            }
        }

        int spawnerNumber = wave.getSpawners().size();

        if(spawnerNumber == 0){
            manager.runEndTask();
            this.cancel();
            return;
        }
        int spawnerRandom = new Random().nextInt(spawnerNumber);
        int chanceSpawnMob = (int) (Math.random() * (100 - 1)) + 1;
        Spawner spawner = wave.getSpawners().get(spawnerRandom);



        for (Mob mob : spawner.getMobList()) {

            if (chanceSpawnMob <= mob.getChance()) {

                if (mobspawned <= wave.getLimitMob()) {
                    int level = randomNumber(mob.getLevelMin(), mob.getLevelMax());

                    MobManager mm = MythicMobs.inst().getMobManager();


                    MonsterUtils.spawnMob(mob.getType(), spawner.getLocation(), level, instance);
                    mobspawned++;
                    break;

                }

            }
        }

        if(waveNumber == MonsterSlayer.getMonsterConfig().getInt("waves")){
            manager.runEndTask();
            this.cancel();
        }


        timer++;
    }

    private int randomNumber(int a, int b){

        Random random = new Random();
        int nb = a + random.nextInt(b - a) ;

        return nb;
    }


    private String getMessage(String path){
        return formatMessage(MonsterSlayer.config.getString(path));
    }

    public String formatMessage(String message){
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}

package com.xilitra.monsterslayer.game.task;

import com.xilitra.monsterslayer.MonsterSlayer;
import com.xilitra.monsterslayer.game.GameInstance;
import com.xilitra.monsterslayer.game.GameManager;
import com.xilitra.monsterslayer.player.MonsterPlayer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class StartTask extends BukkitRunnable {

    private GameInstance instance;
    private FileConfiguration configuration = MonsterSlayer.getMonsterConfig();
    private int timer = configuration.getInt("start-timer");
    private FileConfiguration messageConfig = MonsterSlayer.getCustomConfig("message").getConfig();

    public StartTask(GameInstance instance) {
        this.instance = instance;
    }

    @Override
    public void run() {

        for(MonsterPlayer msPlayer : instance.getPlayerList()){
            for(String timeSection : messageConfig.getConfigurationSection("title-start").getKeys(false)){
                int time = Integer.parseInt(timeSection);

                if(time == timer){
                    String message = messageConfig.getString("title-start." + timeSection);
                    msPlayer.sendTitle(message, null, 20, 20, 20);
                }
            }
        }

        if(timer == 0){
            GameManager.getGameManager(instance).runWaveTask();
            this.cancel();
        }

        timer--;
    }
}

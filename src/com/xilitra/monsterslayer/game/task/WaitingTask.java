package com.xilitra.monsterslayer.game.task;

import com.xilitra.monsterslayer.MonsterSlayer;
import com.xilitra.monsterslayer.game.GameInstance;
import com.xilitra.monsterslayer.game.GameManager;
import com.xilitra.monsterslayer.player.MonsterPlayer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class WaitingTask extends BukkitRunnable {

    public static HashMap<GameInstance, Boolean> isStartedCache = new HashMap<>();
    private FileConfiguration config = MonsterSlayer.getMonsterConfig();
    private FileConfiguration messageConfig = MonsterSlayer.getCustomConfig("message").getConfig();
    private int timer = config.getInt("waiting-timer");
    private GameInstance instance;

    public WaitingTask(GameInstance instance) {
        this.instance = instance;
    }

    @Override
    public void run() {

        for(MonsterPlayer msPlayer : instance.getPlayerList()){
            for(String timeSection : messageConfig.getConfigurationSection("title-waiting").getKeys(false)){
                int time = Integer.parseInt(timeSection);

                if(time == timer){
                    String message = messageConfig.getString("title-waiting." + timeSection);
                    msPlayer.sendTitle(message, null, 20, 20, 20);
                }
            }
        }

        if(timer == 0){
            GameManager.getGameManager(instance).runStartTask();
            this.cancel();
        }

        timer--;
    }

}

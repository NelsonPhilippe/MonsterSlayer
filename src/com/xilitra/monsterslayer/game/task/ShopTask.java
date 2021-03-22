package com.xilitra.monsterslayer.game.task;

import com.xilitra.monsterslayer.MonsterSlayer;
import com.xilitra.monsterslayer.game.GameInstance;
import com.xilitra.monsterslayer.game.GameManager;
import com.xilitra.monsterslayer.player.MonsterPlayer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class ShopTask extends BukkitRunnable {

    private GameInstance instance;
    private FileConfiguration config = MonsterSlayer.getMonsterConfig();
    private FileConfiguration messageConfig = MonsterSlayer.getCustomConfig("message").getConfig();
    private int timer = config.getInt("shop-time");

    public ShopTask(GameInstance instance) {
        this.instance = instance;
    }

    @Override
    public void run() {

        if(timer == config.getInt("shop-time")){
            for(MonsterPlayer msPlayer : instance.getPlayerList()){
                String messageShop = ChatColor.translateAlternateColorCodes('&', messageConfig.getString("kit-phase"));
                msPlayer.sendTitle(messageShop, null, 20, 20 , 20);
            }
        }

        if(timer == 0){
            GameManager manager = GameManager.getGameManager(instance);
            manager.setWaveNumber(manager.getWaveNumber() + 1);
            manager.runWaveTask();
            this.cancel();
        }
        timer--;
    }
}

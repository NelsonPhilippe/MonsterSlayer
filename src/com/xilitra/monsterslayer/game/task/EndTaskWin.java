package com.xilitra.monsterslayer.game.task;

import com.xilitra.monsterslayer.MonsterSlayer;
import com.xilitra.monsterslayer.game.GameInstance;
import com.xilitra.monsterslayer.player.MonsterPlayer;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class EndTaskWin extends BukkitRunnable {

    private GameInstance instance;
    private int timer = 5;

    public EndTaskWin(GameInstance instance){
        this.instance = instance;
    }
    @Override
    public void run() {

        if(timer == 5){
            for(MonsterPlayer player : instance.getPlayerList()){
                player.sendTitle(getMessage("end-message"), null, 40, 20, 40);
            }
        }

        if(timer == 0){

            int level = randomNumber(0, 1);
            String[] win = MonsterSlayer.getMonsterConfig().getString("coins.win").split(":");
            int coins = Integer.parseInt(win[level]);

            for(MonsterPlayer player : instance.getPlayerList()){

                //player.addCoinsInAccount(coins);
            }
            this.cancel();
        }

        timer--;
    }

    private String getMessage(String path){
        return formatMessage(MonsterSlayer.config.getString(path));
    }

    public String formatMessage(String message){
        return ChatColor.translateAlternateColorCodes('&', message);
    }


    private int randomNumber(int a, int b){

        Random random = new Random();
        int nb = a + random.nextInt(b - a) ;

        return nb;
    }
}

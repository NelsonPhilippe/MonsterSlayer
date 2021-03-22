package com.xilitra.monsterslayer.game.task;

import com.xilitra.monsterslayer.MonsterSlayer;
import com.xilitra.monsterslayer.game.GameInstance;
import com.xilitra.monsterslayer.player.MonsterPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class EndTaskLoose extends BukkitRunnable {

    private GameInstance instance;
    private int timer = 5;

    public EndTaskLoose(GameInstance instance){
        this.instance = instance;
    }

    @Override
    public void run() {

        if(timer == 0){
            for(MonsterPlayer msPlayer : instance.getPlayerList()){
                sendToServer(msPlayer.getPlayer(), MonsterSlayer.getMonsterConfig().getString("endtaskloose.serveur-name"));
            }
        }
        timer--;
    }

    public static void sendToServer(Player player, String serverName) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("Connect");
            out.writeUTF(serverName);
            player.sendPluginMessage(MonsterSlayer.getInstance(), "BungeeCord", b.toByteArray());
        } catch (Exception e) {
            player.kickPlayer("Server unreachable");
        }
    }
}

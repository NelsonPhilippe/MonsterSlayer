package com.xilitra.monsterslayer.player.team;

import com.xilitra.monsterslayer.MonsterSlayer;
import com.xilitra.monsterslayer.game.GameInstance;
import com.xilitra.monsterslayer.game.GameManager;
import com.xilitra.monsterslayer.game.GameState;
import com.xilitra.monsterslayer.player.MonsterPlayer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MSScoreboard {

    public static void updateScoreboard(GameInstance instance){
        GameManager manager = GameManager.getGameManager(instance);
        FileConfiguration instanceConfig = instance.getInstanceConfig().getConfig();


        for(MonsterPlayer msPlayer : instance.getPlayerList()){
            Player player = msPlayer.getPlayer();
            FastBoard fastBoard = new FastBoard(player.getPlayer());
            fastBoard.updateTitle(getMessage("scoreboard.title"));

            if(instance.getGameState() == GameState.WAITING){
                int playerMin = instanceConfig.getInt("min-player");
                int playerMax = instanceConfig.getInt("max-player");

                if(playerMin < instance.getPlayerList().size()){
                    List<String> formatMessage = new ArrayList<>();
                    for(String format : MonsterSlayer.config.getStringList("scoreboard.lines.waiting")){
                        formatMessage.add(ChatColor.translateAlternateColorCodes('&', format));
                    }


                    fastBoard.updateLines(formatMessage);
                }

                if(playerMax >= instance.getPlayerList().size()){
                    List<String> noFormatMessage = MonsterSlayer.config.getStringList("scoreboard.lines.waiting-start");
                    fastBoard.updateLines(noFormatMessage);
                }
            }

            if(instance.getGameState() == GameState.WAVE){
                List<String> formatmessage = new ArrayList<>();

                for(String noFormat :  MonsterSlayer.config.getStringList("scoreboard.lines.wave")){
                    String coloredMessage = ChatColor.translateAlternateColorCodes('&', noFormat);
                    formatmessage.add(coloredMessage
                            .replace("%wave%", String.valueOf(manager.getWaveNumber()))
                            .replace("%blood%", String.valueOf(msPlayer.getBlood())));
                }
                fastBoard.updateLines(formatmessage);
            }

            if(instance.getGameState() == GameState.SHOP){
                List<String> formatmessage = new ArrayList<>();

                for(String noFormat : MonsterSlayer.config.getStringList("scoreboard.lines.shop")){
                    String coloredMessage = ChatColor.translateAlternateColorCodes('&', noFormat);
                    formatmessage.add(coloredMessage
                            .replace("%blood%", String.valueOf(msPlayer.getBlood())));
                }
                fastBoard.updateLines(formatmessage);
            }

            if(instance.getGameState() == GameState.FINISH){
                List<String> noFormatMessage = MonsterSlayer.config.getStringList("scoreboard.lines.end");
                fastBoard.updateLines(noFormatMessage);
            }

        }
    }
    private static String getMessage(String path){
        return formatMessage(MonsterSlayer.config.getString(path));
    }

    private static String formatMessage(String message){
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private static List<String> formatMessageList(List<String> noformated){
        List<String> formated = new ArrayList<>();

        for(String message : noformated){
            formated.add(ChatColor.translateAlternateColorCodes('&', message));
        }

        return formated;
    }
}

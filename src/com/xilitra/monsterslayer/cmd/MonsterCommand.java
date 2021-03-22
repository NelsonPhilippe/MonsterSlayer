package com.xilitra.monsterslayer.cmd;

import com.xilitra.monsterslayer.MonsterSlayer;
import com.xilitra.monsterslayer.game.GameInstance;
import com.xilitra.monsterslayer.game.GameState;
import com.xilitra.monsterslayer.gui.ShopGui;
import com.xilitra.monsterslayer.monster.Boss;
import com.xilitra.monsterslayer.monster.Mob;
import com.xilitra.monsterslayer.monster.Spawner;
import com.xilitra.monsterslayer.player.MonsterPlayer;
import com.xilitra.monsterslayer.waves.Wave;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MobManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class MonsterCommand implements CommandExecutor {

    private FileConfiguration config = MonsterSlayer.getMonsterConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        if(!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        MonsterPlayer msPlayer = MonsterPlayer.getMonsterPlayer(player.getUniqueId());

        if(command.getName().equalsIgnoreCase("ms")){
            if(player.hasPermission("ms.admin")){
                createGame(player, args);
                createMobSpawner(player, args);
                addMob(player, args);
                removeMob(player, args);
                removeBoss(player, args);
                addboss(player, args);
                setPlayerMin(player, args);
                setPlayerMax(player, args);
                help(player, args);
                setStartRoom(player, args);
                setWaitingRoom(player, args);
            }

            shop(player, args);

            rules(player, args);

            return true;
        }

        return false;
    }

    public void shop(Player player, String[] args){

        if(args.length == 1){

            if(args[0].equalsIgnoreCase("shop")){

                if(MonsterSlayer.getMonsterConfig().getBoolean("config-mod")){
                    return;
                }
                MonsterPlayer msPlayer = MonsterPlayer.getMonsterPlayer(player.getUniqueId());

                if(msPlayer.getGameInstance().getGameState() != GameState.SHOP){
                    return;
                }

                player.openInventory(ShopGui.createShopGui(msPlayer).getInventory());

            }

        }

    }

    public void help(Player player, String[] args){
        if(args.length == 1){
            if(args[0].equalsIgnoreCase("help")){

                for(String message : MonsterSlayer.config.getStringList("commands.help")){

                    player.sendMessage(formatMessage(message));

                }

            }
        }
    }

    private void createGame(Player player, String[] args){

        if(args.length == 1){
            if(args[0].equalsIgnoreCase("creategame")){
                int instSize = GameInstance.gameInstanceCache.size();
                new GameInstance(GameState.WAITING, instSize, player.getWorld().getName()).loadGameInstance();
                player.sendMessage(getMessage("commands.creategame").replace("%instance%", String.valueOf(instSize)));
            }
        }
    }

    private void createMobSpawner(Player player, String[] args){

        if(args.length == 4){
            if(args[0].equalsIgnoreCase("createmobspawner")){

                Location loc = player.getLocation();

                if(!isNumeric(args[1]) && !isNumeric(args[2]) && !isNumeric(args[3])){
                    player.sendMessage(getMessage("commands.argument-available"));
                    return;
                }
                int instanceId = Integer.parseInt(args[1]);

                if(!GameInstance.gameInstanceCache.containsKey(instanceId)){
                    player.sendMessage(getMessage("commands.instancenoexist").replace("%instance%", args[1]));
                    return;
                }

                GameInstance instance = GameInstance.getGameInstance(instanceId);

                int waveId = Integer.parseInt(args[2]);

                if(instance.getWave(waveId) == null){
                    player.sendMessage(getMessage("commands.wavenoexist").replace("%wave%", args[2]));
                    return;
                }

                Wave wave = instance.getWave(waveId);

                int spawnerId = Integer.parseInt(args[3]);

                if(wave.spawnerExist(loc)){
                    player.sendMessage(getMessage("commands.spawnerexist"));
                    return;
                }


                Spawner spawner = new Spawner(loc, spawnerId, instance, waveId);
                spawner.writeConfig();
                wave.addSpawner(spawner);


                player.sendMessage(getMessage("commands.addspawner"));

            }
        }
    }

    private void addMob(Player player, String[] args){
        if(args.length == 9){
            if (args[0].equalsIgnoreCase("addmob")) {


                if(!isNumeric(args[2]) && !isNumeric(args[3]) && !isNumeric(args[4]) &&
                        !isNumeric(args[5]) && !isNumeric(args[6])){
                    player.sendMessage(getMessage("commands.argument-available"));
                    return;
                }

                MobManager mm = MythicMobs.inst().getMobManager();
                if(!mm.getMobNames().contains(args[1])){
                    player.sendMessage(getMessage("commands.mobnoexist"));
                    return;
                }

                String type = args[1];
                int levelmin = Integer.parseInt(args[2]);
                int levelmax = Integer.parseInt(args[3]);
                int chance = Integer.parseInt(args[4]);
                int instID = Integer.parseInt(args[5]);
                int waveID = Integer.parseInt(args[6]);
                int spawnerID = Integer.parseInt(args[7]);
                int blood = Integer.parseInt(args[8]);

                if(!GameInstance.gameInstanceCache.containsKey(instID)){
                    player.sendMessage(getMessage("commands.instancenoexist").replace("%instance%", args[5]));
                    return;
                }

                GameInstance instance = GameInstance.getGameInstance(instID);

                if(instance.getWaveList().size() == 0){
                    player.sendMessage(getMessage("commands.nowave"));
                    return;
                }

                if(instance.getWave(waveID) == null){
                    player.sendMessage(getMessage("commands.nowave").replace("%wave%", args[6]));
                    return;
                }

                Wave wave = instance.getWave(waveID);

                if(chance > 100){
                    player.sendMessage(getMessage("commands.chanceerror"));
                    return;
                }

                if(wave.getSpawner(spawnerID) == null){
                    player.sendMessage(getMessage("commands.spawnerexist"));
                }
                Spawner spawner = wave.getSpawner(spawnerID);
                Mob mob = new Mob(type, levelmin, levelmax, chance, wave, blood);
                spawner.addMob(mob);

                player.sendMessage(getMessage("commands.addmob")
                        .replace("%mob%", args[1])
                        .replace("%wave%", args[6])
                        .replace("%chance%", args[4]));

            }
        }
    }

    private void removeMob(Player player, String[] args){
        if(args.length == 5){
            if(args[0].equalsIgnoreCase("removemob")) {

                MobManager mm = MythicMobs.inst().getMobManager();
                if(!mm.getMobNames().contains(args[1])){
                    player.sendMessage(getMessage("commands.mobnoexist"));
                    return;
                }

                String type = args[1];
                int instID = Integer.parseInt(args[2]);
                int waveID = Integer.parseInt(args[3]);
                int spawnerID = Integer.parseInt(args[4]);

                if(!GameInstance.gameInstanceCache.containsKey(instID)){
                    player.sendMessage(getMessage("commands.instancenoexist").replace("%instance%", args[5]));
                    return;
                }

                GameInstance instance = GameInstance.getGameInstance(instID);

                if(instance.getWaveList().size() == 0){
                    player.sendMessage(getMessage("commands.nowave"));
                    return;
                }

                if(instance.getWave(waveID) == null){
                    player.sendMessage(getMessage("wavenoexist").replace("%wave%", args[6]));
                    return;
                }

                Wave wave = instance.getWave(waveID);

                if(wave.getSpawner(spawnerID) == null){
                    player.sendMessage(getMessage("commands.spawnernoexist"));
                }

                Spawner spawner = wave.getSpawner(spawnerID);

                if(spawner.getMob(type) == null){
                    player.sendMessage(getMessage("commands.mob-not-exist-in-spawner"));
                    return;
                }

                spawner.removeMob(spawner.getMob(type));
                player.sendMessage(getMessage("commands.deletemob").replace("%mob%", type));


            }

        }
    }

    private void addboss(Player player, String[] args){
        if(args.length == 9){
            if (args[0].equalsIgnoreCase("addboss")) {


                if(!isNumeric(args[2]) && !isNumeric(args[3]) && !isNumeric(args[4]) && !isNumeric(args[5]) &&
                        !isNumeric(args[8])){
                    player.sendMessage(getMessage("commands.argument-available"));
                    return;
                }

                MobManager mm = MythicMobs.inst().getMobManager();
                if(!mm.getMobNames().contains(args[1])){
                    player.sendMessage(getMessage("commands.bossnoexist"));
                    return;
                }

                String type = args[1];
                int levelmin = Integer.parseInt(args[2]);
                int levelmax = Integer.parseInt(args[3]);
                int chance = Integer.parseInt(args[4]);
                int instID = Integer.parseInt(args[5]);
                int waveID = Integer.parseInt(args[6]);
                int spawnerID = Integer.parseInt(args[7]);
                int blood = Integer.parseInt(args[8]);

                if(!GameInstance.gameInstanceCache.containsKey(instID)){
                    player.sendMessage(getMessage("commands.instancenoexist").replace("%instance%", args[5]));
                    return;
                }

                GameInstance instance = GameInstance.getGameInstance(instID);

                if(instance.getWaveList().size() == 0){
                    player.sendMessage(getMessage("commands.nowave"));
                    return;
                }

                if(instance.getWave(waveID) == null){
                    player.sendMessage(getMessage("commands.wavenoexist").replace("%wave%", args[6]));
                    return;
                }

                Wave wave = instance.getWave(waveID);

                if(chance > 100){
                    player.sendMessage(getMessage("commands.chanceerror"));
                    return;
                }

                if(wave.getSpawner(spawnerID) == null){
                    player.sendMessage(getMessage("commands.spawnernoexist"));
                }
                Spawner spawner = wave.getSpawner(spawnerID);
                Boss boss = new Boss(type, levelmin, levelmax, chance, wave, blood);
                spawner.addBoss(boss);

                player.sendMessage(getMessage("commands.addboss")
                        .replace("%mob%", args[1])
                        .replace("%wave%", args[6])
                        .replace("%chance%", args[4]));

            }
        }
    }

    private void removeBoss(Player player, String[] args){
        if(args.length == 5){
            if(args[0].equalsIgnoreCase("removeboss")) {

                MobManager mm = MythicMobs.inst().getMobManager();
                if(!mm.getMobNames().contains(args[1])){
                    player.sendMessage(getMessage("commands.bossnoexist"));
                    return;
                }

                String type = args[1];
                int instID = Integer.parseInt(args[2]);
                int waveID = Integer.parseInt(args[3]);
                int spawnerID = Integer.parseInt(args[4]);

                if(!GameInstance.gameInstanceCache.containsKey(instID)){
                    player.sendMessage(getMessage("commands.instancenoexist").replace("%instance%", args[5]));
                    return;
                }

                GameInstance instance = GameInstance.getGameInstance(instID);

                if(instance.getWaveList().size() == 0){
                    player.sendMessage(getMessage("nowave"));
                    return;
                }

                if(instance.getWave(waveID) == null){
                    player.sendMessage(getMessage("wavenoexist").replace("%wave%", args[6]));
                    return;
                }

                Wave wave = instance.getWave(waveID);

                if(wave.getSpawner(spawnerID) == null){
                    player.sendMessage(getMessage("spawnernoexist"));
                }

                Spawner spawner = wave.getSpawner(spawnerID);

                if(spawner.getMob(type) == null){
                    player.sendMessage(getMessage("commands.bossnoexist").replace("%boss%", type));
                    return;
                }

                spawner.removeBoss(spawner.getBoss(type));
                player.sendMessage(getMessage("commands.deleteboss").replace("%boss%", type));
            }

        }
    }

    public void setWaitingRoom(Player player, String[] args){
        if(args.length == 2) {

            if (args[0].equalsIgnoreCase("setwaitingroom")) {
                Location loc = player.getLocation();

                if (!isNumeric(args[1]) ) {
                    player.sendMessage(getMessage("argument-available"));
                    return;
                }

                int instanceId = Integer.parseInt(args[1]);

                if (!GameInstance.gameInstanceCache.containsKey(instanceId)) {
                    player.sendMessage(getMessage("commands.instancenoexist").replace("%instance%", args[1]));
                    return;
                }

                GameInstance instance = GameInstance.getGameInstance(instanceId);

                instance.setWaitingRoomLocation(loc);
                player.sendMessage(getMessage("commands.setwaitingroom"));
            }
        }
    }

    public void setStartRoom(Player player, String[] args){
        if(args.length == 3) {

            if (args[0].equalsIgnoreCase("setstartroom")) {
                Location loc = player.getLocation();

                if (!isNumeric(args[1]) && !isNumeric(args[2])) {
                    player.sendMessage(getMessage("argument-available"));
                    return;
                }

                int instanceId = Integer.parseInt(args[1]);
                int waveId = Integer.parseInt(args[2]);

                if (!GameInstance.gameInstanceCache.containsKey(instanceId)) {
                    player.sendMessage(getMessage("commands.instancenoexist").replace("%instance%", args[1]));
                    return;
                }

                GameInstance instance = GameInstance.getGameInstance(instanceId);

                if(instance.getWave(waveId) == null){
                    player.sendMessage(getMessage("wavenoexist").replace("%wave%", args[2]));
                    return;
                }

                Wave wave = instance.getWave(waveId);

                GameInstance.getGameInstance(instanceId).setStartRoomLocation(loc, wave);
                player.sendMessage(getMessage("commands.setstartroom"));

            }
        }
    }

    public void setPlayerMin(Player player, String[] args){
        if(args.length == 3){

            if(args[0].equalsIgnoreCase("setplayermin")){

                if(!isNumeric(args[1]) && !isNumeric(args[2])){
                    player.sendMessage(getMessage("argument-available"));
                    return;
                }

                int instanceId = Integer.parseInt(args[1]);

                if(!GameInstance.gameInstanceCache.containsKey(instanceId)){
                    player.sendMessage(getMessage("commands.instancenoexist").replace("%instance%", args[1]));
                    return;
                }

                GameInstance instance = GameInstance.getGameInstance(instanceId);

                player.sendMessage(getMessage("commands.setminplayer").replace("%value%", args[2]));
                instance.getInstanceConfig().getConfig().set("player-min", Integer.parseInt(args[2]));
                try {
                    instance.getInstanceConfig().getConfig().save(instance.getInstanceConfig().getFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    public void setPlayerMax(Player player, String[] args){
        if(args.length == 3){

            if(args[0].equalsIgnoreCase("setplayermax")){

                if(!isNumeric(args[1]) && !isNumeric(args[2])){
                    player.sendMessage(getMessage("argument-available"));
                    return;
                }

                int instanceId = Integer.parseInt(args[1]);

                if(!GameInstance.gameInstanceCache.containsKey(instanceId)){
                    player.sendMessage(getMessage("commands.instancenoexist").replace("%instance%", args[1]));
                    return;
                }

                GameInstance instance = GameInstance.getGameInstance(instanceId);

                player.sendMessage(getMessage("commands.setmaxplayer").replace("%value%", args[2]));
                instance.getInstanceConfig().getConfig().set("player-max", Integer.parseInt(args[2]));
                try {
                    instance.getInstanceConfig().getConfig().save(instance.getInstanceConfig().getFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    public void rules(Player player, String[] args){

        if(args.length == 1){
            if(args[0].equalsIgnoreCase("rules")){
                for(String message : MonsterSlayer.config.getStringList("commands.rules")){
                    player.sendMessage(formatMessage(message));
                }
            }
        }

    }

    private String getMessage(String path){
        return formatMessage(MonsterSlayer.config.getString(path));
    }

    public String formatMessage(String message){
        return ChatColor.translateAlternateColorCodes('&', message);
    }


    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}

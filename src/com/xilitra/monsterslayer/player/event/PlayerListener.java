package com.xilitra.monsterslayer.player.event;

import com.xilitra.monsterslayer.MonsterSlayer;
import com.xilitra.monsterslayer.game.GameInstance;
import com.xilitra.monsterslayer.game.GameManager;
import com.xilitra.monsterslayer.game.GameState;
import com.xilitra.monsterslayer.game.task.WaitingTask;
import com.xilitra.monsterslayer.monster.Boss;
import com.xilitra.monsterslayer.monster.Spawner;
import com.xilitra.monsterslayer.player.MonsterPlayer;
import com.xilitra.monsterslayer.player.team.MSScoreboard;
import com.xilitra.monsterslayer.utils.MonsterUtils;
import com.xilitra.monsterslayer.waves.Wave;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MobManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();

        if(MonsterSlayer.getMonsterConfig().getBoolean("config-mod")){
            p.sendMessage(ChatColor.RED + "Mode configuration activé");
            return;
        }

        MonsterPlayer msPlayer = new MonsterPlayer(p).load();

        for(String message : MonsterSlayer.config.getStringList("join-message")){
            msPlayer.getPlayer().sendMessage(message);
        }

        for(GameInstance instance : GameInstance.gameInstanceCache.values()){

            GameManager gameManager = GameManager.getGameManager(instance);
            if(!instance.instanceIsFull()){
                if(gameManager.getGameInstance().getGameState() == GameState.WAITING) {
                    instance.addMonsterPlayer(msPlayer);
                    msPlayer.setGameInstance(instance);
                    msPlayer.getPlayer().teleport(instance.getWaitingRoomLocation());
                    //msPlayer.getPlayer().setGameMode(GameMode.ADVENTURE);
                    MSScoreboard.updateScoreboard(instance);

                    int sizelist = instance.getPlayerList().size();
                    int minPlayer = instance.getInstanceConfig().getConfig().getInt("min-player");

                    if (sizelist < minPlayer) {
                        p.sendMessage(getMessage("waiting-queue").replace("%player%",
                                String.valueOf(sizelist - minPlayer)));
                    }


                    if (sizelist >= minPlayer) {


                        if (!WaitingTask.isStartedCache.get(instance)) {

                            WaitingTask.isStartedCache.put(instance, true);
                            GameManager.getGameManager(instance).runWaitingTask();
                        }
                    }

                    break;
                }
            }
        }
    }



    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e){
        Player p = e.getPlayer();

        if(MonsterSlayer.getMonsterConfig().getBoolean("config-mod")){
            return;
        }

        MonsterPlayer msPlayer = MonsterPlayer.getMonsterPlayer(p.getUniqueId()).unload();
        msPlayer.getPlayer().sendMessage(
                getMessage("leave-message").replace("%player%", msPlayer.getName()));
        GameInstance instance = msPlayer.getGameInstance();
        instance.removeMonsterPlayer(msPlayer);
        GameManager manager = GameManager.getGameManager(instance);

        if(instance.getPlayerList().size() == 0){

            //stop all scheduler if instance is empty
            switch(instance.getGameState()){
                case WAITING: manager.stopWaitingTask(); break;
                case START: manager.stopStartTask(); break;
                case WAVE: manager.stopWaveTask();
                    clearMythicMob(instance);
                    break;
                case SHOP: manager.stoShopTask(); break;
                case FINISH: manager.stopEndTask(); break;
            }


        }

    }

    @EventHandler
    public void onPlayerDamageByEnvironment(EntityDamageEvent e){

        if(!(e.getEntity() instanceof Player)) return;
        if(e.getCause() == EntityDamageEvent.DamageCause.FALL || EntityDamageEvent.DamageCause.SUFFOCATION == e.getCause()){
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e){

        if(e.getEntity() instanceof  Player){
            Player player = (Player) e.getEntity();
            MonsterPlayer ms = MonsterPlayer.getMonsterPlayer(player.getUniqueId());
            GameInstance instance = ms.getGameInstance();
            GameManager manager = GameManager.getGameManager(instance);
            Wave wave = instance.getWave(manager.getWaveNumber());

            if(e.getDamager() instanceof Player){
                e.setCancelled(true);
            }

            if(player.getHealth() <= e.getDamage()){

                ms.sendTitle(getMessage("kill-message"), null, 20, 35, 20);

                e.setCancelled(true);
                player.setGameMode(GameMode.SPECTATOR);
                ms.setAlive(false);


                instance.removeMonsterPlayer(ms);

                if(instance.getPlayerList().size() == 0){
                    ms.sendTitle(getMessage("end-message_lose"), null, 20 , 30, 20);
                    int level = randomNumber(0, 1);
                    String[] lose = MonsterSlayer.getMonsterConfig().getString("coins.lose").split(":");
                    int coins = Integer.parseInt(lose[level]);

                    //ms.addCoinsInAccount(coins);
                    clearMythicMob(instance);

                    if(MonsterSlayer.getMonsterConfig().getBoolean("endtaskloose.active")){
                        manager.runEndTaskLoose();
                    }
                }

            }
        }

        if(e.getDamager() instanceof  Player) {
            Player p = (Player) e.getDamager();
            MonsterPlayer msPlayer = MonsterPlayer.getMonsterPlayer(p.getUniqueId());
            Damageable victim = (Damageable) e.getEntity();
            MobManager mm = MythicMobs.inst().getMobManager();

            GameInstance instance = msPlayer.getGameInstance();
            GameManager manager = GameManager.getGameManager(instance);
            Wave wave = instance.getWave(manager.getWaveNumber());

            // Si un mob meurt
            if(victim.getHealth() <= e.getDamage()){

                victim.damage(10000);

                //  check mythicmob
                if(!mm.isActiveMob(victim.getUniqueId())){
                    return;
                }

                String type = mm.getMythicMobInstance(victim).getMobType();

                if(!mm.getMobNames().contains(type)){
                    return;
                }

                int mobkilled = wave.getMobkilled();
                int mobalive = wave.getCounterMob()  - mobkilled;
                int bosskilled = wave.getBosskilled();
                int bossalive = wave.getCounterBoss() - bosskilled;

                for(Spawner spawner : wave.getSpawners()){

                    // Break boucle pour eviter la dupli de money
                    if(spawner.getMob(type) != null){
                        int blood = spawner.getMob(type).getBlood();
                        msPlayer.addBlood(blood);
                        msPlayer.sendActionBar(getMessage("blood-notif").replace("%amount%", String.valueOf(blood)));
                        wave.setMobkilled(mobkilled + 1);
                        mobalive = mobalive - 1;
                        MSScoreboard.updateScoreboard(instance);
                        break;
                    }

                    if(spawner.getBoss(type) != null){
                        int blood = spawner.getBoss(type).getBlood();
                        msPlayer.addBlood(blood);
                        msPlayer.sendActionBar(getMessage("blood-notif").replace("%amount%", String.valueOf(blood)));
                        wave.setBosskilled(bosskilled + 1);
                        bossalive = bossalive - 1;
                        MSScoreboard.updateScoreboard(instance);
                        break;
                    }

                }

                // nombre de mob en vie a zero
                if(mobalive == 0){
                    if(!wave.bossIsSpawned()){

                        boolean spawned = spawnBoss(wave);

                        if(spawned){
                            wave.setBossIsSpawned(true);
                            return;
                        }
                        wave.setBossIsSpawned(true);
                    }
                }

                // nombre de boss en vie a zero
                if(bossalive == 0 && wave.bossIsSpawned()){
                    if(instance.getWaveList().size() <= manager.getWaveNumber()){
                        manager.runEndTask();
                        clearMythicMob(instance);
                        return;
                    }

                    for(MonsterPlayer msPlayers : instance.getPlayerList()){
                        msPlayers.sendTitle(getMessage("wave-stop"),
                                null, 20, 20, 20);
                    }
                    clearMythicMob(instance);
                    manager.stopWaveTask();
                    manager.runShopTask();
                }

            }

        }

    }

    private boolean spawnBoss(Wave wave){

        int counter = wave.getInstance().getPlayerList().size();

        for(int i = 0; i < counter ; i++){

            int spawnerNumber = wave.getSpawners().size();
            int spawnerRandom = new Random().nextInt(spawnerNumber);
            Spawner spawner = wave.getSpawners().get(spawnerRandom);


            int random = (int) (Math.random() * (100 - 1)) + 1;


            if(spawner.getBossList().size() == 0){
                wave.setCounterBoss(0);
                return true;
            }

            for(Boss boss : spawner.getBossList()){

                int level = randomNumber(boss.getLevelMin(), boss.getLevelMax());

                if(boss.getChance() >= random){


                    MonsterUtils.spawnMob(boss.getType(), spawner.getLocation(), level, wave.getInstance());
                    break;
                }
            }

        }

        for(MonsterPlayer msPlayer : wave.getInstance().getPlayerList()){
            msPlayer.sendTitle("Un boss vien de spawn", null, 20, 35, 20);
        }
        return true;
    }

    public void clearMythicMob(GameInstance instance){
        for(Entity entity : instance.mobspawned){

            entity.remove();

        }
    }

    public void giveStartKit(){
        for(String section : MonsterSlayer.getMonsterConfig().getConfigurationSection("kit").getKeys(false)){
            String path = "kit." + section;
            FileConfiguration config = MonsterSlayer.getMonsterConfig();
            ItemStack item = new ItemStack(Material.getMaterial(config.getString(path + ".material")),
                    config.getInt(path + ".size"));
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(config.getString(path + ".name"));

            if(!config.getStringList(path + ".enchantment").isEmpty()){
                for(String enchantment : config.getStringList(path + ".enchantment")){

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

    private int randomNumber(int a, int b){

        Random random = new Random();
        int nb = a + random.nextInt(b - a) ;

        return nb;
    }

}

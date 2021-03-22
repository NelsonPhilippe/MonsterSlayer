package com.xilitra.monsterslayer.monster;

import com.xilitra.monsterslayer.game.GameInstance;
import com.xilitra.monsterslayer.waves.Wave;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Spawner {

    private Location location;
    private int id;
    private GameInstance instance;
    private int wave;
    private List<Mob> mobList = new ArrayList<>();
    private List<Boss> bossList = new ArrayList<>();

    public Spawner(Location location, int id, GameInstance instance, int wave) {
        this.location = location;
        this.instance = instance;
        this.wave = wave;
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }


    public List<Mob> getMobList() {
        return mobList;
    }

    public void addMob(Mob mob){
        mobList.add(mob);
        saveMob(mob);
    }

    public Mob getMob(String type){
        for(Mob mob : mobList){
            if(mob.getType().equalsIgnoreCase(type)){
                return mob;
            }
        }
        return null;
    }

    public void removeMob(Mob mob){
        mobList.remove(mob);
        removeMobConfig(mob);
    }

    public List<Boss> getBossList() {
        return bossList;
    }

    public void addBoss(Boss boss){
        bossList.add(boss);
        saveBoss(boss);
    }

    public void removeBoss(Boss boss){
        bossList.add(boss);
        removeBossConfig(boss);
    }

    public Boss getBoss(String type){
        for(Boss boss : bossList){
            if(boss.getType().equalsIgnoreCase(type)){
                return boss;
            }
        }
        return null;
    }

    public GameInstance getInstance() {
        return instance;
    }

    public int getWave() {
        return wave;
    }

    public int getId() {
        return id;
    }

    public void writeConfig(){
        FileConfiguration config = instance.getInstanceConfig().getConfig();
        config.createSection("spawner.wave." + wave + "." + id + ".id");
        config.createSection("spawner.wave." + wave + "." + id + ".loc.x");
        config.createSection("spawner.wave." + wave + "." + id + ".loc.y");
        config.createSection("spawner.wave." + wave + "." + id + ".loc.z");
        config.createSection("spawner.wave." + wave + "." + id + ".moblist");
        config.createSection("spawner.wave." + wave + "." + id + ".bosslist");

        config.set("spawner.wave." + wave + "." + id + ".id", id);
        config.set("spawner.wave." + wave + "." + id + ".loc.x", location.getBlockX());
        config.set("spawner.wave." + wave + "." + id + ".loc.y", location.getBlockY());
        config.set("spawner.wave." + wave + "." + id + ".loc.z", location.getBlockZ());


        try {
            config.save(instance.getInstanceConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveMob(Mob mob){
        FileConfiguration config = instance.getInstanceConfig().getConfig();

        config.createSection("spawner.wave." + wave + "." + id + ".moblist." + mob.getType() + ".levelmin");
        config.createSection("spawner.wave." + wave + "." +  id + ".moblist." + mob.getType() + ".levelmax");
        config.createSection("spawner.wave." + wave + "." + id + ".moblist." + mob.getType() + ".chance");
        config.createSection("spawner.wave." + wave + "." + id + ".moblist." + mob.getType() + ".blood");


        config.set("spawner.wave." + wave + "." + id + ".moblist." + mob.getType() + ".levelmin", mob.getLevelMin());
        config.set("spawner.wave." + wave + "." + id + ".moblist." + mob.getType() + ".levelmax", mob.getLevelMax());
        config.set("spawner.wave." + wave + "." + id + ".moblist." + mob.getType() + ".chance", mob.getChance());
        config.set("spawner.wave." + wave + "." + id + ".moblist." + mob.getType() + ".blood", mob.getBlood());


        try {
            config.save(instance.getInstanceConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void saveBoss(Boss boss){
        FileConfiguration config = instance.getInstanceConfig().getConfig();
        config.createSection("spawner.wave." + wave + "." + id + ".bosslist." + boss.getType());
        config.createSection("spawner.wave." + wave + "." + id + ".bosslist." + boss.getType() +".levelmin");
        config.createSection("spawner.wave." + wave + "." + id + ".bosslist." + boss.getType() +".levelmax");
        config.createSection("spawner.wave." + wave + "." + id + ".bosslist." + boss.getType() +".chance");
        config.createSection("spawner.wave." + wave + "." + id + ".bosslist." + boss.getType() + ".blood");


        config.set("spawner.wave." + wave + "." + id + ".bosslist." + boss.getType() +".levelmin", boss.getLevelMin());
        config.set("spawner.wave." + wave + "." + id + ".bosslist." + boss.getType() +".levelmax", boss.getLevelMax());
        config.set("spawner.wave." + wave + "." + id + ".bosslist." + boss.getType() +".chance", boss.getChance());
        config.set("spawner.wave." + wave + "." + id + ".bosslist." + boss.getType() + ".blood", boss.getBlood());

        try {
            config.save(instance.getInstanceConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void removeMobConfig(Mob mob){
        FileConfiguration config = instance.getInstanceConfig().getConfig();
        String path = "spawner.wave." + wave + "." + id + ".moblist." + mob.getType();

        config.set(path, null);

        try {
            config.save(instance.getInstanceConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void removeBossConfig(Boss boss){
        FileConfiguration config = instance.getInstanceConfig().getConfig();
        String path = "spawner.wave." + wave + "." + id + ".bosslist." + boss.getType();

        config.set(path, null);

        try {
            config.save(instance.getInstanceConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

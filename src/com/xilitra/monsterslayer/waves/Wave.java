package com.xilitra.monsterslayer.waves;

import com.xilitra.monsterslayer.game.GameInstance;
import com.xilitra.monsterslayer.monster.Boss;
import com.xilitra.monsterslayer.monster.Mob;
import com.xilitra.monsterslayer.monster.Spawner;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Wave {

    private int wave;
    private GameInstance instance;
    private List<Spawner> spawners = new ArrayList<>();
    private int limitMob = 1;
    private int counterMob;
    private int counterBoss;
    private boolean bossIsSpawned = false;
    private int mobkilled = 0;
    private int bosskilled = 0;




    public Wave(int wave, GameInstance instance, int limiMob) {
        this.wave = wave;
        this.instance = instance;
        this.limitMob = limiMob;
    }

    public int getWave() {
        return wave;
    }

    public List<Spawner> getSpawners() {
        return spawners;
    }

    public void addSpawner(Spawner spawner){
        spawners.add(spawner);
    }

    public void removeSpawner(Spawner spawner){
        spawners.remove(spawner);
    }

    public Spawner getSpawner(int id){
        for(Spawner spawner : spawners){
            if(spawner.getId() == id){
                return spawner;
            }
        }
        return null;
    }

    public boolean spawnerExist(Location loc){
        for(Spawner spawner : spawners){
            if(spawner.getLocation() == loc){
                return true;
            }
        }
        return false;
    }

    public int getLimitMob(){
        return limitMob;
    }

    public int getCounterMob(){
        return counterMob;
    }

    public void setCounterMob(int counterMob){
        this.counterMob = counterMob;
    }

    public int getCounterBoss(){
        return counterBoss;
    }

    public void setCounterBoss(int counterBoss){
        this.counterBoss = counterBoss;
    }

    public GameInstance getInstance(){
        return instance;
    }

    public boolean bossIsSpawned(){
        return bossIsSpawned;
    }

    public void setBossIsSpawned(boolean value){
        this.bossIsSpawned = value;
    }

    public int getMobkilled(){return mobkilled;}

    public int getBosskilled(){return bosskilled;}

    public void setMobkilled(int amount){
        this.mobkilled = amount;
    }

    public void setBosskilled(int amount){
        this.bosskilled = amount;
    }


}

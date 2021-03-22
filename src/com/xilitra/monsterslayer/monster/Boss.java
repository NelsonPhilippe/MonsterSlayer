package com.xilitra.monsterslayer.monster;

import com.xilitra.monsterslayer.waves.Wave;

public class Boss {

    private String type;
    private int levelmin;
    private int levelmax;
    private int chance;
    private Wave wave;
    private int blood;

    public int getChance() {
        return chance;
    }

    public Boss(String type, int levelmin, int levelmax, int chance, Wave wave, int blood) {
        this.type = type;
        this.levelmin = levelmin;
        this.levelmax = levelmax;
        this.chance = chance;
        this.wave = wave;
        this.blood = blood;
    }

    public String getType() {
        return type;
    }

    public int getLevelMin() {
        return levelmin;
    }

    public int getLevelMax() {
        return levelmax;
    }

    public Wave getWave() {
        return wave;
    }

    public int getBlood(){
        return blood;
    }
}

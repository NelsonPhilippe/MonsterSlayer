package com.xilitra.monsterslayer.config;

import com.xilitra.monsterslayer.MonsterSlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class CustomConfig {

    private String fileName;
    private String folderName;
    private File file;
    private FileConfiguration config;

    public CustomConfig(String fileName, String folderName) {
        this.fileName = fileName;
        this.folderName = folderName;
    }

    public CustomConfig(String fileName) {
        this.fileName = fileName;
    }

    public CustomConfig loadConfig(){
        if(folderName == null){
            file = new File(MonsterSlayer.getInstance().getDataFolder(), fileName + ".yml");
        }else{
            file = new File(MonsterSlayer.getInstance().getDataFolder()
                    + File.separator + folderName, fileName + ".yml");
        }
        config = null;

        if(!file.exists()){
            try {
                file.createNewFile();
                config = YamlConfiguration.loadConfiguration(file);
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            config = YamlConfiguration.loadConfiguration(file);
        }

        return this;
    }

    public CustomConfig loadLangConfig(){
        if(folderName == null){
            file = new File(MonsterSlayer.getInstance().getDataFolder(), fileName + ".yml");
        }else{
            file = new File(MonsterSlayer.getInstance().getDataFolder()
                    + File.separator + folderName, fileName + ".yml");
        }
        config = null;

        if(!file.exists()){
            try {
                file.createNewFile();
                config = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(MonsterSlayer.getInstance().getResource(fileName + ".yml")));
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            config = YamlConfiguration.loadConfiguration(file);
        }

        return this;
    }

    public CustomConfig loadShopConfig(){
        if(folderName == null){
            file = new File(MonsterSlayer.getInstance().getDataFolder(), fileName + ".yml");
        }else{
            file = new File(MonsterSlayer.getInstance().getDataFolder()
                    + File.separator + folderName, fileName + ".yml");
        }
        config = null;

        if(!file.exists()){
            try {
                file.createNewFile();
                config = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(MonsterSlayer.getInstance().getResource(fileName + ".yml")));
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            config = YamlConfiguration.loadConfiguration(file);
        }

        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFolderName() {
        return folderName;
    }

    public File getFile() {
        return file;
    }

    public FileConfiguration getConfig() {
        return config;
    }
}

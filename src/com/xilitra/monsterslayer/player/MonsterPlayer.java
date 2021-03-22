package com.xilitra.monsterslayer.player;

import com.xilitra.monsterslayer.game.GameInstance;
import com.xilitra.monsterslayer.utils.ActionBar;
import com.xilitra.monsterslayer.utils.TitleUtils;
import io.zentae.accountapi.Account;
import io.zentae.accountapi.AccountAPI;
import org.bukkit.Material;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class MonsterPlayer {

    public static HashMap<UUID, MonsterPlayer> playerCache = new HashMap<>();
    private Player player;
    private String name;
    private UUID uuid;
    private GameInstance instance;
    private int blood;
    private boolean isAlive = true;

    public MonsterPlayer(Player player) {
        this.player = player;
        this.name = player.getName();
        this.uuid = player.getUniqueId();
    }

    public Player getPlayer() {
        return player;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public MonsterPlayer load(){
        MonsterPlayer.playerCache.put(getUuid(), this);
        return this;
    }

    public MonsterPlayer unload(){
        MonsterPlayer.playerCache.remove(getUuid());
        return this;
    }

    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut){
        TitleUtils.sendTitle(getPlayer(), title, subtitle, fadeIn, stay, fadeOut);
    }

    public static MonsterPlayer getMonsterPlayer(UUID uuid){
        return MonsterPlayer.playerCache.get(uuid);
    }

    public static void sendTitleToAllPlayerInstance(String title, String subtitle, int fadeIn, int stay, int fadeOut){
        for(MonsterPlayer msPlayer : MonsterPlayer.playerCache.values()){
            Player bukkitPlayer = msPlayer.getPlayer();

            TitleUtils.sendTitle(bukkitPlayer, title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    public void sendActionBar(String message){
        ActionBar.sendActionBar(getPlayer(), message);
    }

    public int getBlood(){
        return blood;
    }

    public void withDrawBlood(int amount){
        blood = blood - amount;
    }

    public void addBlood(int amount){
        blood = blood + amount;
    }

    public void setGameInstance(GameInstance instance) {
        this.instance = instance;
    }

    public GameInstance getGameInstance() {
        return instance;
    }

    public boolean isAlive(){
        return isAlive;
    }

    public void setAlive(boolean value){
        this.isAlive = value;
    }

    public void addCoinsInAccount(int amount){
        Account account = AccountAPI.getAccount(player);

        account.setCoins(account.getCoins() + amount);
        AccountAPI.setAccount(account);
    }
}

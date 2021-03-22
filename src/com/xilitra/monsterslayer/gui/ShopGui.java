package com.xilitra.monsterslayer.gui;

import com.xilitra.monsterslayer.MonsterSlayer;
import com.xilitra.monsterslayer.config.CustomConfig;
import com.xilitra.monsterslayer.player.MonsterPlayer;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.MMOItem;
import net.Indyuce.mmoitems.manager.ItemManager;
import net.Indyuce.mmoitems.manager.TypeManager;
import net.minecraft.server.v1_14_R1.ItemMapEmpty;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopGui implements Listener {

    private static CustomConfig shopConfig = MonsterSlayer.getCustomConfig("gui");
    private static FileConfiguration config = shopConfig.getConfig();
    public static GuiBuilder shopGuiBuilder = new GuiBuilder("shop",
            config.getString("shop.name"), config.getInt("shop.size"));

    public static GuiBuilder createShopGui(MonsterPlayer msPlayer){
        List<String> inseeList = config.getStringList("shop.insee.slot");

        for(String insee : inseeList){

            String[] inseeSplited = insee.split(":");
            int slot = Integer.parseInt(inseeSplited[0]);
            Material material = Material.getMaterial(inseeSplited[1]);

            shopGuiBuilder.setItem(slot, new ItemStack(material));

        }

        for(String itemName : config.getConfigurationSection("shop.item-sell.item").getKeys(false)){
            String path = "shop.item-sell.item." + itemName;
            int slot = config.getInt(path + ".slot");

            shopGuiBuilder.setItem(slot, formatItemWithLore(itemName));
        }

        int blood_slot = config.getInt("blood-item.slot");

        shopGuiBuilder.setItem(blood_slot, bloodItem(msPlayer));

        return shopGuiBuilder;
    }

    private static ItemStack bloodItem(MonsterPlayer msPlayer){
        Material material = Material.getMaterial(config.getString("shop.blood-item.material"));
        String name = ChatColor.translateAlternateColorCodes('&',
                config.getString("shop.blood-item.name").replace("%blood%", String.valueOf(msPlayer.getBlood())));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);

        item.setItemMeta(meta);

        return item;
    }

    private static ItemStack formatItemWithLore(String name){
        FileConfiguration config = shopConfig.getConfig();
        String path = "shop.item-sell.item." + name;
        String displayname = ChatColor.translateAlternateColorCodes('&', config.getString( path + ".display-name"));
        Material material = Material.getMaterial(config.getString(path + ".material"));
        Enchantment enchantment = Enchantment.getByName(config.getString(path + ".Echantment.type"));
        int level = config.getInt(path + ".enchantment.level");
        int amount = config.getInt(path + ".amount");
        int price = config.getInt(path + ".price");
        List<String> loreList = config.getStringList(path + ".lore");

        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',displayname));

        List<String> loreFormated = new ArrayList<>();
        for(String loreString : loreList){
            loreString.replace("%price%", String.valueOf(price));
            loreFormated.add(ChatColor.translateAlternateColorCodes('&', loreString));
        }

        if(enchantment != null){
            meta.addEnchant(enchantment, level, true);
        }

        item.setItemMeta(meta);

        return item;

    }

    private static ItemStack formatItem(String name){
        FileConfiguration config = shopConfig.getConfig();
        String path = "shop.item-sell.item." + name;
        String displayname = config.getString( path + ".display-name");
        Material material = Material.getMaterial(config.getString(path + ".material"));
        Enchantment enchantment = Enchantment.getByName(config.getString(path + ".Echantment.type"));
        int level = config.getInt(path + ".enchantment.level");
        int amount = config.getInt(path + ".amount");

        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',displayname));

        if(enchantment != null){
            meta.addEnchant(enchantment, level, true);
        }

        return item;

    }

    public List<ItemStack> getItemKit(String itemSelled){

        List<ItemStack> itemStacks = new ArrayList<>();
        List<String> kitList = config.getStringList("shop.item-sell.item." + itemSelled + ".give");

        for(String item : kitList){

            String[] split = item.split(":");
            String type = split[0];
            String name = split[1];

            ItemManager itemManager = MMOItems.plugin.getItems();
            ItemStack newItem = itemManager.getItem(MMOItems.plugin.getTypes().get(type), name);
            itemStacks.add(newItem);

        }

        return itemStacks;

    }


    @EventHandler
    public void onClickInventory(InventoryClickEvent e){
        FileConfiguration config = shopConfig.getConfig();
        String path = "shop.item-sell.item";
        Inventory inventory = e.getClickedInventory();
        Inventory shopInventory = shopGuiBuilder.getInventory();
        ItemStack itemClicked = e.getCurrentItem();
        Player p = (Player) e.getWhoClicked();
        MonsterPlayer msPlayer = MonsterPlayer.getMonsterPlayer(p.getUniqueId());

        if(inventory.getHolder() == shopInventory.getHolder()){
            e.setCancelled(true);

            for(String itemName : config.getConfigurationSection(path).getKeys(false)){

                if(itemClicked.getItemMeta().getDisplayName().contains(itemName)){

                    int itemPrice = config.getInt(path + "." + itemName + ".price");

                    if(itemPrice > msPlayer.getBlood()){
                        p.sendMessage(getMessage("blood-amount"));
                        return;
                    }

                    List<String> permissions = config.getStringList(path + "." + itemName + ".permission");

                    for(String permission : permissions){
                        if(!msPlayer.getPlayer().hasPermission(permission)){

                            msPlayer.getPlayer().sendMessage(getMessage("shop-grade"));
                            return;

                        }

                    }

                    for(ItemStack itemGived : getItemKit(itemName)){

                        msPlayer.getPlayer().getInventory().addItem(itemGived);

                    }

                    msPlayer.withDrawBlood(itemPrice);
                    p.closeInventory();
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

}

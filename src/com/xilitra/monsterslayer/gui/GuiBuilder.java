package com.xilitra.monsterslayer.gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class GuiBuilder implements InventoryHolder {

    private String name;
    private String displayName;
    private int size;
    private Inventory inv;

    public GuiBuilder(String name, String displayName, int size){
        this.name = name;
        this.displayName = displayName;
        this.size = size;
        inv = Bukkit.createInventory(this, size, displayName);
    }

    public void addItem(ItemStack...item){
        inv.addItem(item);
    }

    public void setItem(int slot, ItemStack item){
        inv.setItem(slot, item);
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}

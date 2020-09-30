package com.lss233.simplestgui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public abstract class Gui implements InventoryHolder {
    protected final Player player;
    private final Plugin plugin;
    private Inventory inventory;
    private final Listener inventoryListener;
    private final Map<Integer, Component> componentMap = new HashMap<>();
    private final  InventoryHolder holder;
    protected String title;
    protected int size;

    public Gui(Plugin plugin, Player player, String title, int size) {
        this.plugin = plugin;
        this.player = player;
        this.size = size;
        this.title = title;
        this.holder = this;
        this.inventoryListener = new Listener() {
            @EventHandler(ignoreCancelled = true)
            public void onInventoryClick(InventoryClickEvent event) {
                if (event.getInventory() == null || !event.getInventory().getHolder().equals(holder)) {
                    return;
                }
                //if(event.getClickedInventory() == null || !event.getClickedInventory().equals(getInventory())) return;
                event.setCancelled(true);
                if (componentMap.containsKey(event.getSlot()) && componentMap.get(event.getSlot()) != null) {
                    if (componentMap.get(event.getSlot()).getAction() != null) {
                        componentMap.get(event.getSlot()).getAction().accept((Player) event.getWhoClicked(), event.getClick());
                    }
                }
            }
            @EventHandler(ignoreCancelled = true)
            public void onInventoryClose(InventoryCloseEvent event) {
                if (!event.getInventory().getHolder().equals(holder)) {
                    return;
                }
                unregisterListeners();
                onClosed();
            }
        };
    }

    @Override
    public Inventory getInventory() {
        if(this.inventory == null){
            this.inventory = Bukkit.createInventory(this, size, title);
        } else if(!this.inventory.getTitle().equals(this.title) || this.inventory.getSize() != this.size){
            Inventory newInventory = Bukkit.createInventory(this, size, title);
            newInventory.setContents(this.inventory.getContents());
            this.inventory = newInventory;
        }
        return this.inventory;
    }

    protected abstract void draw();

    private void registerListeners() {
        plugin.getServer().getPluginManager().registerEvents(this.inventoryListener, plugin);
    }

    private void unregisterListeners() {
        InventoryClickEvent.getHandlerList().unregister(inventoryListener);
        InventoryCloseEvent.getHandlerList().unregister(inventoryListener);
    }

    protected abstract void onClosed();

    public void open() {
        unregisterListeners();
        registerListeners();
        player.openInventory(getInventory());
        draw();
    }

    public void setComponent(int slot, Component component) {
        if (component == null) {
            this.componentMap.remove(slot);
            getInventory().setItem(slot, null);
        } else {
            this.componentMap.put(slot, component);
            getInventory().setItem(slot, component.getItemStack());
        }

    }

    public Player getPlayer(){
        return player;
    }

    public int getSize(){
        return size;
    }

    public String getTitle(){
        return title;
    }



}

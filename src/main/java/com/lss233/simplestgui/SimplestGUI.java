package com.lss233.simplestgui;

import org.bukkit.plugin.java.JavaPlugin;

public final class SimplestGUI extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        GuiLayout.init();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

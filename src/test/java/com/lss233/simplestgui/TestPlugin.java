package com.lss233.simplestgui;

import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin {
    private static TestPlugin instance;
    @Override
    public void onEnable() {
        instance = this;
    }

    public static TestPlugin getInstance() {
        return instance;
    }
}

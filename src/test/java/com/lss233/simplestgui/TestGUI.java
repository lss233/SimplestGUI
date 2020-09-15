package com.lss233.simplestgui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;

public class TestGUI extends Gui{

    public TestGUI(Player player) {
        super(TestPlugin.getInstance(), player, "A test gui", 9);
    }

    @Override
    protected void draw() {
        setComponent(1, new Component.Builder()
                .icon(Material.CARROT_STICK)
                .title("Test button")
                .lore(Arrays.asList("A", "Test", "BUTTON"))
                .click((user, click) -> {
                    if(click.equals(ClickType.LEFT)){
                        user.sendMessage("You left clicked this");
                    } else if(click.equals(ClickType.RIGHT)) {
                        user.sendMessage("You right clicked this");
                    }
                })
                .build());
    }

    @Override
    protected void onClosed() {

    }
}

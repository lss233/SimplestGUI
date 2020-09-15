package com.lss233.simplestgui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class CustomGui extends Gui{
    private final GuiLayout layout;
    private final Map<String, BiConsumer<Player, ClickType>> registeredCallbacks = new HashMap<>();
    public CustomGui(Plugin plugin, Player player, GuiLayout layout) {
        super(plugin, player, layout.getTitle(), layout.getLayout().length * 9);
        this.layout = layout;
    }

    @Override
    protected void draw() {
        getLayout().getLayoutFlat((i, _el) -> {
            String el = _el.toString();
            setComponent(i, new Component.Builder().item(getLayout().getWidgets().get(el))
                    .click((player, click) -> {
                        if(!getLayout().getCallbacks().containsKey(el)) return;
                        if(this.registeredCallbacks.containsKey(getLayout().getCallbacks().get(el))){
                            this.registeredCallbacks.get(getLayout().getCallbacks().get(el)).accept(player, click);
                        }
                    })
                    .build());
        });
    }

    public GuiLayout getLayout() {
        return layout;
    }
    public CustomGui on(String callback, BiConsumer<Player, ClickType> consumer) {
        this.registeredCallbacks.put(callback, consumer);
        return this;
    }

}

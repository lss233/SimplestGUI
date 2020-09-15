package com.lss233.simplestgui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;

public abstract class PaginatedGui<T> extends Gui {
    protected int page;
    protected Gui previous;
    protected boolean hasNext;
    protected List<T> items;
    public PaginatedGui(Plugin plugin, Player player, String title, int size) {
        super(plugin, player, title, size);
    }
    public PaginatedGui(Plugin plugin, Player player, String title, int size, Gui previous) {
        this(plugin, player, title, size);
        this.previous = previous;
    }
    protected PaginatedGui(Plugin plugin, Player player, String title, int size, int page,Gui previous) {
        super(plugin, player, title, size);
        this.page = page;
        this.previous = previous;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    protected abstract Component drawItem(int slot, T item);

    @Override
    protected void draw() {
        getInventory().clear();
        for (int i = 0; i < getItems().size(); i++) {
            T item = getItems().get(i);
            setComponent(i, drawItem(i, item));
        }
    }

    public boolean hasNext() {
        return hasNext;
    }

    public void hasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    @Override
    public void open() {
        super.open();
    }

    public Gui getPrevious() {
        return previous;
    }
}

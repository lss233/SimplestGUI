package com.lss233.simplestgui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class Component {
    private ItemStack itemStack;
    private BiConsumer<Player, ClickType> action;
    private Component(){
        this.itemStack = new ItemStack(Material.STAINED_GLASS);
    }

    public BiConsumer<Player, ClickType> getAction() {
        return action;
    }

    ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Component component = (Component) o;
        return itemStack.equals(component.itemStack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemStack);
    }

    public static class Builder {
        private final Component component;
        public Builder() {
            this.component = new Component();
        }
        public Builder title(String title) {
            ItemMeta meta = component.itemStack.getItemMeta();
            meta.setDisplayName(title.replace('&', ChatColor.COLOR_CHAR));
            component.itemStack.setItemMeta(meta);
            return this;
        }
        public Builder lore(List<String> lore) {
            ItemMeta meta = component.itemStack.getItemMeta();
            meta.setLore(lore.stream().map(i -> i.replace('&', ChatColor.COLOR_CHAR)).collect(Collectors.toList()));
            component.itemStack.setItemMeta(meta);
            return this;
        }
        public Builder icon(Material material){
            ItemMeta meta = component.itemStack.getItemMeta();
            component.itemStack.setType(material);

            if(meta == null){
                component.itemStack.setItemMeta(Bukkit.getItemFactory().getItemMeta(material));
            } else {
                component.itemStack.setItemMeta(meta);
            }
            return this;
        }
        public Builder click(BiConsumer<Player, ClickType> action) {
            component.action = action;
            return this;
        }
        public Component build() {
            return component;
        }

        public Builder meta(ItemMeta meta) {
            component.itemStack.setItemMeta(meta);
            return this;
        }

        public Builder item(ItemStack itemStack) {
            component.itemStack = itemStack;
            return this;
        }
        public Builder enchant(Enchantment enchantment, int level) {
            component.itemStack.addUnsafeEnchantment(enchantment, level);
            return this;
        }
        public Builder growing(boolean growing) {
            if(growing) {
                enchant(Enchantment.LUCK, 1);
            }
            return this;
        }
    }
}

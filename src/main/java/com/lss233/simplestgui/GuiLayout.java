package com.lss233.simplestgui;

import com.google.common.collect.ImmutableMap;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SerializableAs("GuiLayout")
public class GuiLayout implements ConfigurationSerializable, Cloneable {
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{([\\w.]+)}");
    private Map<String, Object> deserializedMap;
    static {
        ConfigurationSerialization.registerClass(GuiLayout.class, "GuiLayout");
    }

    private String title;
    private Character[][] layout;
    private Map<String, ItemStack> widgets;
    private Map<String, String> callbacks;

    public Character[][] getLayout() {
        return layout;
    }

    public void setLayout(Character[][] layout) {
        this.layout = layout;
    }

    public Map<String, ItemStack> getWidgets() {
        return widgets;
    }

    public void setWidgets(Map<String, ItemStack> widgets) {
        this.widgets = widgets;
    }

    public Map<String, String> getCallbacks() {
        return callbacks;
    }

    public void setCallbacks(Map<String, String> callbacks) {
        this.callbacks = callbacks;
    }
    public static void init() {
        ConfigurationSerialization.registerClass(GuiLayout.class, "GuiLayout");
    }
    public static GuiLayout deserialize(Map<String, Object> map) {
        GuiLayout layout = new GuiLayout();
        layout.deserializedMap = ImmutableMap.copyOf(map);
        List<String> lines  = (List<String>) map.get("layout");
        Map<String, ItemStack> widgetsMap = new HashMap<>();
        ((Map<String, ItemStack>)map.get("widgets")).forEach((key, value) -> {
            ItemStack itemStack = new ItemStack(value.getType());
            itemStack.setAmount(value.getAmount());
            itemStack.setDurability(value.getDurability());
            value.getEnchantments().forEach(itemStack::addUnsafeEnchantment);
            if(value.hasItemMeta()){
                itemStack.setItemMeta(value.getItemMeta().clone());
            }
        });
        layout.setTitle(((String) map.get("title")).replace('&', ChatColor.COLOR_CHAR));
        layout.setLayout(lines.stream().map(GuiLayout::toCharacterArray).collect(Collectors.toList()).toArray(new Character[0][0]));
        layout.setWidgets((Map<String, ItemStack>) map.get("widgets"));
        layout.setCallbacks((Map<String, String>) map.get("callbacks"));
        return layout;
    }

    @Override
    public Map<String, Object> serialize() {
        return deserializedMap;
    }

    public String getTitle() {
        return title;
    }
    public GuiLayout applyVariables(Map<String, String> variables) {
        this.getWidgets().replaceAll((key, item) -> {
            ItemStack value = item;
            if (value.hasItemMeta()) {
                if (value.getItemMeta().hasLore()) {
                    ItemMeta meta = value.getItemMeta().clone();
                    List<String> lore = new ArrayList<>();
                    value.getItemMeta().getLore().stream().forEach(line -> {
                        Matcher matcher = VARIABLE_PATTERN.matcher(line);
                        StringBuilder builder = new StringBuilder();
                        int i = 0;
                        while (matcher.find()) {
                            String replacement = variables.getOrDefault(matcher.group(1), "{" + key + "}").toString();
                            builder.append(line, i, matcher.start());
                            builder.append(replacement);
                            i = matcher.end();
                        }
                        builder.append(line.substring(i));
                        lore.add(builder.toString().replaceAll("&", String.valueOf(ChatColor.COLOR_CHAR)));
                    });

                    final AtomicReference<String> name = new AtomicReference<>(meta.getDisplayName());
                    variables.forEach((k, v) -> name.set(name.get().replace("{" + k + "}", v)));
                    meta.setDisplayName(name.get());
                    meta.setLore(lore);

                    value.setItemMeta(meta);
                }
            }
            return value;
        });
        return this;
    }

    public GuiLayout applyPlaceholders(Player player) {
        if(Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.getWidgets().replaceAll((key, item) -> {
                ItemStack value = item.clone();
                if (value.hasItemMeta()) {
                    ItemMeta meta = value.getItemMeta().clone();
                    if(value.getItemMeta().hasDisplayName()) {
                        meta.setDisplayName(PlaceholderAPI.setPlaceholders(player, meta.getDisplayName().replace('&', ChatColor.COLOR_CHAR)));
                    }
                    if (value.getItemMeta().hasLore()) {
                        List<String> lore = new ArrayList<>();
                        meta.getLore().forEach(i -> lore.add(PlaceholderAPI.setPlaceholders(player, i.replace('&', ChatColor.COLOR_CHAR))));
                        meta.setLore(lore);
                    }
                    value.setItemMeta(meta);
                }
                return value;
            });
        }
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public GuiLayout addWidget(String identifier, ItemStack itemStack) {
        getWidgets().put(identifier, itemStack);
        return this;
    }

    public GuiLayout updateWidget(String identifier, BiConsumer<ItemStack, ItemMeta> consumer) {
        ItemStack item = getWidgets().getOrDefault(identifier, null);
        ItemMeta meta = item == null ? null : item.getItemMeta().clone();
        consumer.accept(item, meta);
        if(item != null) {
            item.setItemMeta(meta);
        }
        return this;
    }
    public GuiLayout updateWidget(String identifier, Map<String, String> variables) {
        ItemStack item = getWidgets().getOrDefault(identifier, null);
        ItemMeta meta = item.getItemMeta().clone();
        if(meta.hasLore()) {
            meta.setLore(meta.getLore().stream().map(line -> {
                Matcher matcher = VARIABLE_PATTERN.matcher(line);
                StringBuilder builder = new StringBuilder();
                int i = 0;
                while (matcher.find()) {
                    String replacement = variables.getOrDefault(matcher.group(1), matcher.group(1));
                    builder.append(line, i, matcher.start());
                    builder.append(replacement);
                    i = matcher.end();
                }
                builder.append(line.substring(i));
                return builder.toString().replaceAll("&", String.valueOf(ChatColor.COLOR_CHAR));
            }).collect(Collectors.toList()));
        }
        if(meta.hasDisplayName()) {
            final AtomicReference<String> name = new AtomicReference<>(meta.getDisplayName());
            if (variables != null) {
                variables.forEach((k, v) -> name.set(name.get().replace("{" + k + "}", v)));
            }
            meta.setDisplayName(name.get().replace("&", String.valueOf(ChatColor.COLOR_CHAR)));
        }
        item.setItemMeta(meta);
        return this;
    }

    public static Character[] toCharacterArray(String str){
        return ArrayUtils.toObject(str.toCharArray());
    }

    public void getLayoutFlat(BiConsumer<Integer, Character> action) {
        int index = 0;
        for (Character[] lines : getLayout()) {
            for(Character el: lines) {
                action.accept(index++, el);
            }
        }
    }

    public static GuiLayout from(ConfigurationSection section) {
        GuiLayout layout = new GuiLayout();
        Map<String, String> callbacks =  new HashMap<>();
        Map<String, ItemStack> widgets = new HashMap<>();
        section.getConfigurationSection("callbacks").getValues(false).forEach((k,v) -> {
            callbacks.put(k, (String) v);
        });
        section.getConfigurationSection("widgets").getKeys(false).forEach(k-> {
            ItemStack item = section.getConfigurationSection("widgets").getItemStack(k).clone();
            widgets.put(k, item);
        });
        layout.setLayout(section.getStringList("layout").stream().map(GuiLayout::toCharacterArray).collect(Collectors.toList()).toArray(new Character[0][0]));
        layout.setTitle(section.getString("title"));
        layout.setCallbacks(callbacks);
        layout.setWidgets(widgets);
        return layout;
    }
}

package org.antipk;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataStorage {

    private final AntiPlayerKill plugin;
    private File file;
    private FileConfiguration config;

    public DataStorage(AntiPlayerKill plugin) {
        this.plugin = plugin;
        init();
    }

    private void init() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        file = new File(plugin.getDataFolder(), "data.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create data.yml: " + e.getMessage());
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void saveRelapses(Map<UUID, Integer> relapseCache) {
        Map<UUID, Integer> snapshot = new HashMap<>(relapseCache);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            synchronized (this) {
                config.set("relapses", null);
                for (Map.Entry<UUID, Integer> entry : snapshot.entrySet()) {
                    config.set("relapses." + entry.getKey().toString(), entry.getValue());
                }
                try {
                    config.save(file);
                } catch (IOException e) {
                    plugin.getLogger().severe("Failed to save data.yml: " + e.getMessage());
                }
            }
        });
    }

    public synchronized void loadRelapses(Map<UUID, Integer> relapseCache) {
        relapseCache.clear();
        if (!file.exists()) return;

        config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("relapses");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                int offenses = config.getInt("relapses." + key);
                relapseCache.put(uuid, offenses);
            } catch (IllegalArgumentException ignored) {}
        }
    }
}
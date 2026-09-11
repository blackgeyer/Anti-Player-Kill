package org.antipk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.entity.Player;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DumpManager {
    private final AntiPlayerKill plugin;
    private final File dumpsFile;
    private final List<Dump> dumps = new ArrayList<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public DumpManager(AntiPlayerKill plugin) {
        this.plugin = plugin;
        this.dumpsFile = new File(plugin.getDataFolder(), "dumps.json");
        loadDumps();
    }

    public synchronized void createDump(Player victim, String killerName, String killType) {
        Dump dump = Dump.fromPlayer(idCounter.getAndIncrement(), victim, killerName, killType);
        dumps.add(dump);
        saveDumps();
    }

    public synchronized boolean restoreDump(Player target, Dump dump) {
        if (!dump.canFitInInventory(target)) {
            return false;
        }
        dump.applyToPlayer(target);
        dump.setExpired(true);
        saveDumps();
        return true;
    }

    public synchronized Dump getLatestActiveDump(String playerName) {
        for (int i = dumps.size() - 1; i >= 0; i--) {
            Dump d = dumps.get(i);
            if (d.getPlayerName().equalsIgnoreCase(playerName) && !isDumpExpired(d)) {
                return d;
            }
        }
        return null;
    }

    public synchronized Dump getDumpById(String playerName, int id) {
        for (Dump d : dumps) {
            if (d.getPlayerName().equalsIgnoreCase(playerName) && d.getId() == id) {
                return d;
            }
        }
        return null;
    }

    public synchronized List<Dump> getDumpsForPlayer(String playerName) {
        List<Dump> result = new ArrayList<>();
        for (Dump d : dumps) {
            if (d.getPlayerName().equalsIgnoreCase(playerName)) {
                result.add(d);
            }
        }
        return result;
    }

    public boolean isDumpExpired(Dump dump) {
        if (dump.isExpired()) return true;
        long clearTime = ConfigLoader.getClearTimeMillis();
        return (System.currentTimeMillis() - dump.getTimestamp()) > clearTime;
    }

    public synchronized void saveDumps() {
        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            File tempFile = new File(plugin.getDataFolder(), "dumps.json.tmp");
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8)) {
                gson.toJson(dumps, writer);
            }

            Files.move(tempFile.toPath(), dumpsFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to write dumps to dumps.json: " + e.getMessage());
        }
    }

    private synchronized void loadDumps() {
        dumps.clear();
        if (!dumpsFile.exists()) {
            return;
        }

        try (Reader reader = new InputStreamReader(new FileInputStream(dumpsFile), StandardCharsets.UTF_8)) {
            Type type = new TypeToken<List<Dump>>() {}.getType();
            List<Dump> loaded = gson.fromJson(reader, type);
            if (loaded != null) {
                dumps.addAll(loaded);
                int maxId = dumps.stream().mapToInt(Dump::getId).max().orElse(0);
                idCounter.set(maxId + 1);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("File dumps.json is corrupted: " + e.getMessage());
            backupCorruptedFile();
        }
    }

    private void backupCorruptedFile() {
        try {
            File backup = new File(plugin.getDataFolder(), "dumps.json.corrupted_" + System.currentTimeMillis());
            if (dumpsFile.exists()) {
                Files.move(dumpsFile.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
                plugin.getLogger().warning("Corrupted file has been saved with name: " + backup.getName());
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to rename the corrupted file: " + e.getMessage());
        }
    }
    public synchronized void cleanupExpiredDumps() {
    boolean removed = dumps.removeIf(this::isDumpExpired);
    if (removed) {
        saveDumps();
    }
}

    public synchronized void saveDumpsSync() {
        saveDumps();
    }
}
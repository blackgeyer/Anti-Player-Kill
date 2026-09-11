package org.antipk;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Dump {
    private final int id;
    private final String playerName;
    private final String killerName;
    private final String killType;
    private final long timestamp;
    private boolean expired;
    private final String itemsBase64;

    private transient List<ItemStack> cachedItems;

    public Dump(int id, String playerName, String killerName, String killType, long timestamp, boolean expired, String itemsBase64) {
        this.id = id;
        this.playerName = playerName;
        this.killerName = killerName;
        this.killType = killType;
        this.timestamp = timestamp;
        this.expired = expired;
        this.itemsBase64 = itemsBase64;
    }

    public static Dump fromPlayer(int id, Player victim, String killerName, String killType) {
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack item : victim.getInventory().getContents()) {
            if (item != null) {
                items.add(item.clone());
            }
        }
        String base64 = serializeItems(items);
        Dump dump = new Dump(id, victim.getName(), killerName, killType, System.currentTimeMillis(), false, base64);
        dump.cachedItems = items;
        return dump;
    }

    public List<ItemStack> getItems() {
        if (cachedItems == null) {
            cachedItems = deserializeItems(itemsBase64);
        }
        return cachedItems;
    }

    public boolean canFitInInventory(Player target) {
        List<ItemStack> items = getItems();
        int emptySlots = 0;
        for (ItemStack item : target.getInventory().getStorageContents()) {
            if (item == null) {
                emptySlots++;
            }
        }
        return emptySlots >= items.size();
    }

    public void applyToPlayer(Player target) {
        for (ItemStack item : getItems()) {
            target.getInventory().addItem(item.clone());
        }
    }

    private static String serializeItems(List<ItemStack> items) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
            dataOutput.writeInt(items.size());
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }
            dataOutput.flush();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static List<ItemStack> deserializeItems(String base64) {
        List<ItemStack> items = new ArrayList<>();
        if (base64 == null || base64.isEmpty()) return items;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(base64));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            int size = dataInput.readInt();
            for (int i = 0; i < size; i++) {
                items.add((ItemStack) dataInput.readObject());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public int getId() { return id; }
    public String getPlayerName() { return playerName; }
    public String getKillerName() { return killerName; }
    public String getKillType() { return killType; }
    public long getTimestamp() { return timestamp; }
    public boolean isExpired() { return expired; }
    public void setExpired(boolean expired) { this.expired = expired; }
}
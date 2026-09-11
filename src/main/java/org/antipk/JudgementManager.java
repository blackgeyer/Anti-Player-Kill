package org.antipk;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JudgementManager {

    private final Map<UUID, WarnContainer> warnCache = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> relapseCache = new ConcurrentHashMap<>();
    private final DataStorage dataStorage;

    private static final Pattern DURATION_PATTERN = Pattern.compile("^([0-9]+(?:\\.[0-9]+)?)\\s*([a-zA-Z]*)$");

    public JudgementManager(AntiPlayerKill plugin) {
        this.dataStorage = new DataStorage(plugin);
        this.dataStorage.loadRelapses(this.relapseCache);
    }

    public void reloadData() {
        this.warnCache.clear();
        this.dataStorage.loadRelapses(this.relapseCache);
    }

    public void processRdm(Player killer, Player victim) {
        UUID killerId = killer.getUniqueId();

        if (!ConfigLoader.warnsEnabled || ConfigLoader.warnLimit <= 0) {
            if (ConfigLoader.actionsEnabled) {
                executePunishment(killer);
            }
            if (ConfigLoader.warnMsgEnabled) {
                sendStaticWarnMessage(killer);
            }
            return;
        }

        WarnContainer container = warnCache.computeIfAbsent(killerId, k -> new WarnContainer());
        int currentWarns = container.addWarnAndGetCount(ConfigLoader.warnIsDynamic, ConfigLoader.warnDecayMillis);

        if (currentWarns > ConfigLoader.warnLimit) {
            warnCache.remove(killerId);
            if (ConfigLoader.actionsEnabled) {
                executePunishment(killer);
            }
            return;
        }

        if (currentWarns == ConfigLoader.warnLimit) {
            sendLastWarnMessage(killer, currentWarns);
        } else {
            sendWarnMessage(killer, currentWarns);
        }
    }

    private void executePunishment(Player killer) {
        String rawDuration = ConfigLoader.basePunishmentDuration;
        String durationStr = rawDuration;

        if (ConfigLoader.relapseDetect) {
            int offenses = relapseCache.merge(killer.getUniqueId(), 1, Integer::sum);
            dataStorage.saveRelapses(relapseCache);
            durationStr = calculateRelapseDuration(rawDuration, offenses);
        }

        String command = ConfigLoader.punishmentCommand
                .replace("%player%", killer.getName())
                .replace("%duration%", durationStr)
                .replace("%reason%", ConfigLoader.defaultReason);

        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    private String calculateRelapseDuration(String rawDuration, int offenses) {
        if (rawDuration == null || rawDuration.trim().isEmpty()) {
            return "1h";
        }

        Matcher baseMatcher = DURATION_PATTERN.matcher(rawDuration.trim());

        if (!baseMatcher.matches()) {
            return rawDuration;
        }

        double baseValue = Double.parseDouble(baseMatcher.group(1));
        String unit = baseMatcher.group(2);

        if (unit == null || unit.isEmpty()) {
            unit = "h";
        }

        double calculated = baseValue * Math.pow(ConfigLoader.repeatMultiplier, offenses - 1);

        if (ConfigLoader.maxDuration != null && !ConfigLoader.maxDuration.trim().isEmpty()) {
            Matcher maxMatcher = DURATION_PATTERN.matcher(ConfigLoader.maxDuration.trim());
            if (maxMatcher.matches()) {
                double maxValue = Double.parseDouble(maxMatcher.group(1));
                String maxUnit = maxMatcher.group(2);

                if (maxUnit == null || maxUnit.isEmpty()) {
                    maxUnit = "h";
                }

                if (unit.equalsIgnoreCase(maxUnit)) {
                    calculated = Math.min(calculated, maxValue);
                }
            }
        }

        long finalValue = Math.round(calculated);
        return finalValue + unit;
    }

    private void sendWarnMessage(Player killer, int currentWarns) {
        if (ConfigLoader.warnMsgEnabled && ConfigLoader.warnMsg != null && !ConfigLoader.warnMsg.isEmpty()) {
            String msg = ConfigLoader.warnMsg
                    .replace("%player%", killer.getName())
                    .replace("%warns%", String.valueOf(currentWarns))
                    .replace("%max_warns%", String.valueOf(ConfigLoader.warnLimit));
            MessageUtil.sendMessage(killer, msg);
        }
    }

    private void sendLastWarnMessage(Player killer, int currentWarns) {
        if (ConfigLoader.warnMsgEnabled && ConfigLoader.lastWarnMsg != null && !ConfigLoader.lastWarnMsg.isEmpty()) {
            String lastMsg = ConfigLoader.lastWarnMsg
                    .replace("%player%", killer.getName())
                    .replace("%warns%", String.valueOf(currentWarns))
                    .replace("%max_warns%", String.valueOf(ConfigLoader.warnLimit));
            MessageUtil.sendMessage(killer, lastMsg);
        }
    }

    private void sendStaticWarnMessage(Player killer) {
        if (ConfigLoader.warnMsgEnabled && ConfigLoader.staticWarnMsg != null && !ConfigLoader.staticWarnMsg.isEmpty()) {
            String staticMsg = ConfigLoader.staticWarnMsg
                    .replace("%player%", killer.getName());
            MessageUtil.sendMessage(killer, staticMsg);
        }
    }
}
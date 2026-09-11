package org.antipk;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class AntiPKCommand implements CommandExecutor, TabCompleter {

    private final AntiPlayerKill plugin;
    private final DumpManager dumpManager;
    private final JudgementManager judgementManager;

    public AntiPKCommand(AntiPlayerKill plugin, DumpManager dumpManager, JudgementManager judgementManager) {
        this.plugin = plugin;
        this.dumpManager = dumpManager;
        this.judgementManager = judgementManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            MessageUtil.sendMessage(sender, ConfigLoader.reloadUsage);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("antipk.reload")) {
                MessageUtil.sendMessage(sender, ConfigLoader.msgNoPermission);
                return true;
            }
            plugin.reloadConfig();
            ConfigLoader.load(plugin.getConfig(), plugin);
            if (judgementManager != null) {
                judgementManager.reloadData();
            }
            MessageUtil.sendMessage(sender, ConfigLoader.msgReloadSuccess);
            return true;
        }

        if (args[0].equalsIgnoreCase("dump")) {
            if (!sender.hasPermission("antipk.dumps")) {
                MessageUtil.sendMessage(sender, ConfigLoader.msgNoPermission);
                return true;
            }

            if (args.length < 3) {
                MessageUtil.sendMessage(sender, ConfigLoader.dumpUsage);
                return true;
            }

            String targetName = args[1];
            String subCmd = args[2].toLowerCase();

            switch (subCmd) {
                case "restore" -> {
                    Player target = Bukkit.getPlayer(targetName);
                    if (target == null) {
                        String notFound = ConfigLoader.dumpNotFound.replace("%player%", targetName);
                        MessageUtil.sendMessage(sender, notFound);
                        return true;
                    }

                    int dumpId = -1;
                    boolean confirm = false;

                    if (args.length >= 4) {
                        try {
                            dumpId = Integer.parseInt(args[3]);
                            if (args.length >= 5 && args[4].equalsIgnoreCase("confirm")) {
                                confirm = true;
                            }
                        } catch (NumberFormatException e) {
                            if (args[3].equalsIgnoreCase("confirm")) {
                                confirm = true;
                            }
                        }
                    }

                    Dump dumpToRestore = (dumpId != -1) 
                            ? dumpManager.getDumpById(targetName, dumpId) 
                            : dumpManager.getLatestActiveDump(targetName);

                    if (dumpToRestore == null || dumpManager.isDumpExpired(dumpToRestore)) {
                        String expired = ConfigLoader.dumpIsExpired.replace("%player%", targetName);
                        MessageUtil.sendMessage(sender, expired);
                        return true;
                    }

                    if (!confirm) {
                        String msg = ConfigLoader.areYouSure
                                .replace("%player%", targetName)
                                .replace("%id%", String.valueOf(dumpToRestore.getId()));
                        MessageUtil.sendMessage(sender, msg);
                        return true;
                    }

                    boolean restored = dumpManager.restoreDump(target, dumpToRestore);
                    if (restored) {
                        String success = ConfigLoader.dumpRestoreSuc.replace("%player%", targetName);
                        MessageUtil.sendMessage(sender, success);

                        plugin.getLogger().info(String.format(
                            "%s restored dump #%d for %s. (Killer: %s, KillType: %s)",
                            sender.getName(), dumpToRestore.getId(), target.getName(),
                            dumpToRestore.getKillerName(), dumpToRestore.getKillType()
                        ));
                    } else {
                        String spaceErr = ConfigLoader.spaceError.replace("%player%", targetName);
                        MessageUtil.sendMessage(sender, spaceErr);
                    }
                    return true;
                }

                case "dumps" -> {
                    List<Dump> dumps = dumpManager.getDumpsForPlayer(targetName);
                    if (dumps.isEmpty()) {
                        String empty = ConfigLoader.dumpListEmpty.replace("%player%", targetName);
                        MessageUtil.sendMessage(sender, empty);
                        return true;
                    }

                    int page = 1;
                    if (args.length >= 4) {
                        try {
                            page = Integer.parseInt(args[3]);
                        } catch (NumberFormatException ignored) {}
                    }

                    int pageSize = 5;
                    int maxPages = (int) Math.ceil((double) dumps.size() / pageSize);
                    if (page < 1) page = 1;
                    if (page > maxPages) page = maxPages;

                    String header = ConfigLoader.dumpListHeader
                            .replace("%player%", targetName)
                            .replace("%page%", String.valueOf(page))
                            .replace("%max_pages%", String.valueOf(maxPages));
                    MessageUtil.sendMessage(sender, header);

                    int startIndex = (page - 1) * pageSize;
                    int endIndex = Math.min(startIndex + pageSize, dumps.size());

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    for (int i = startIndex; i < endIndex; i++) {
                        Dump d = dumps.get(i);
                        boolean isExp = dumpManager.isDumpExpired(d);
                        String item = ConfigLoader.dumpListItem
                                .replace("%id%", String.valueOf(d.getId()))
                                .replace("%time%", sdf.format(new Date(d.getTimestamp())))
                                .replace("%expired%", isExp ? "Yes" : "No");
                        MessageUtil.sendMessage(sender, item);
                    }
                    return true;
                }

                case "info" -> {
                    if (args.length < 4) {
                        MessageUtil.sendMessage(sender, ConfigLoader.dumpUsage);
                        return true;
                    }

                    int id;
                    try {
                        id = Integer.parseInt(args[3]);
                    } catch (NumberFormatException e) {
                        MessageUtil.sendMessage(sender, ConfigLoader.dumpUsage);
                        return true;
                    }

                    Dump dump = dumpManager.getDumpById(targetName, id);
                    if (dump == null) {
                        String notFound = ConfigLoader.dumpNotFound.replace("%player%", targetName);
                        MessageUtil.sendMessage(sender, notFound);
                        return true;
                    }

                    boolean isExp = dumpManager.isDumpExpired(dump);
                    long remainingMillis = ConfigLoader.getClearTimeMillis() - (System.currentTimeMillis() - dump.getTimestamp());
                    String expireTimeStr = isExp ? "EXPIRED" : formatTime(remainingMillis);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    String infoMsg = ConfigLoader.dumpInfo
                            .replace("%time%", sdf.format(new Date(dump.getTimestamp())))
                            .replace("%id%", String.valueOf(dump.getId()))
                            .replace("%killtype%", dump.getKillType())
                            .replace("%killer%", dump.getKillerName())
                            .replace("%expireflag%", isExp ? "Yes" : "No")
                            .replace("%expiretime%", expireTimeStr);

                    MessageUtil.sendMessage(sender, infoMsg);
                    return true;
                }
            }
        }

        MessageUtil.sendMessage(sender, ConfigLoader.reloadUsage);
        return true;
    }

    private String formatTime(long millis) {
        if (millis <= 0) return "EXPIRED";
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) return days + "d " + (hours % 24) + "h";
        if (hours > 0) return hours + "h " + (minutes % 60) + "m";
        if (minutes > 0) return minutes + "m " + (seconds % 60) + "s";
        return seconds + "s";
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("antipk.reload")) completions.add("reload");
            if (sender.hasPermission("antipk.dumps")) completions.add("dump");
            return filter(completions, args[0]);
        }

        if (!args[0].equalsIgnoreCase("dump") || !sender.hasPermission("antipk.dumps")) {
            return Collections.emptyList();
        }

        if (args.length == 2) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
            return filter(completions, args[1]);
        }

        if (args.length == 3) {
            completions.addAll(List.of("restore", "dumps", "info"));
            return filter(completions, args[2]);
        }

        String targetName = args[1];
        String subCmd = args[2].toLowerCase();

        if (args.length == 4) {
            if (subCmd.equals("restore")) {
                completions.add("confirm");
                List<Dump> dumps = dumpManager.getDumpsForPlayer(targetName);
                for (Dump d : dumps) {
                    if (!dumpManager.isDumpExpired(d)) {
                        completions.add(String.valueOf(d.getId()));
                    }
                }
            } else if (subCmd.equals("info")) {
                List<Dump> dumps = dumpManager.getDumpsForPlayer(targetName);
                for (Dump d : dumps) {
                    completions.add(String.valueOf(d.getId()));
                }
            } else if (subCmd.equals("dumps")) {
                completions.addAll(List.of("1", "2", "3"));
            }
            return filter(completions, args[3]);
        }

        if (args.length == 5 && subCmd.equals("restore")) {
            completions.add("confirm");
            return filter(completions, args[4]);
        }

        return Collections.emptyList();
    }

    private List<String> filter(List<String> list, String input) {
        if (input == null || input.isEmpty()) return list;
        String lower = input.toLowerCase();
        List<String> result = new ArrayList<>();
        for (String item : list) {
            if (item.toLowerCase().startsWith(lower)) {
                result.add(item);
            }
        }
        return result;
    }
}
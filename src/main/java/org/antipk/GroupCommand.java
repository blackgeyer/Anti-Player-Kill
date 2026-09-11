package org.antipk;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;

public class GroupCommand implements CommandExecutor, TabCompleter {

    private final GroupManager groupManager;
    private final DuelManager duelManager;

    public GroupCommand(GroupManager groupManager, DuelManager duelManager) {
        this.groupManager = groupManager;
        this.duelManager = duelManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtil.sendMessage(sender, ConfigLoader.initErrorMsg);
            return true;
        }

        if (duelManager.isInDuel(player.getUniqueId())) {
            MessageUtil.sendMessage(player, ConfigLoader.msgGroupInDuelError);
            return true;
        }

        if (args.length == 1) {
            String sub = args[0].toLowerCase();

            if (sub.equals("create")) {
                groupManager.createGroup(player);
                MessageUtil.sendMessage(player, ConfigLoader.msgGroupCreated);
                return true;
            }

            if (sub.equals("accept")) {
                if (!groupManager.hasInvite(player.getUniqueId())) {
                    MessageUtil.sendMessage(player, ConfigLoader.msgGroupNoInvites);
                    return true;
                }
                groupManager.acceptInvite(player);
                MessageUtil.sendMessage(player, ConfigLoader.msgGroupAccepted);
                return true;
            }

            if (sub.equals("leave")) {
                groupManager.leaveGroupCommand(player);
                return true;
            }

            if (sub.equals("info")) {
                UUID groupId = groupManager.getGroupId(player.getUniqueId());
                if (groupId == null) {
                    MessageUtil.sendMessage(player, ConfigLoader.msgNoGroup);
                    return true;
                }

                UUID leaderId = groupManager.getGroupLeader(groupId);
                Set<UUID> members = groupManager.getGroupMembers(groupId);

                String leaderName;
                if (leaderId != null) {
                    Player leaderPlayer = Bukkit.getPlayer(leaderId);
                    leaderName = leaderPlayer != null
                            ? leaderPlayer.getName()
                            : Bukkit.getOfflinePlayer(leaderId).getName();
                    if (leaderName == null) leaderName = "Unknown";
                } else {
                    leaderName = "Unknown";
                }

                StringJoiner memberList = new StringJoiner(", ");
                int count = 0;
                if (members != null) {
                    count = members.size();
                    for (UUID memberId : members) {
                        Player online = Bukkit.getPlayer(memberId);
                        if (online != null) {
                            memberList.add(online.getName());
                        } else {
                            String offlineName = Bukkit.getOfflinePlayer(memberId).getName();
                            memberList.add((offlineName != null ? offlineName : "Unknown")
                                    + ConfigLoader.msgGroupInfoOfflineMarker);
                        }
                    }
                }

                MessageUtil.sendMessage(player, ConfigLoader.msgGroupInfoHeader);
                MessageUtil.sendMessage(player,
                        ConfigLoader.msgGroupInfoLeader.replace("%player%", leaderName));
                MessageUtil.sendMessage(player,
                        ConfigLoader.msgGroupInfoMembers
                                .replace("%count%", String.valueOf(count))
                                .replace("%list%", memberList.toString()));
                return true;
            }

            if (sub.equals("delete")) {
                groupManager.deleteGroup(player);
                return true;
            }
        }

        if (args.length == 2) {
            String sub = args[0].toLowerCase();

            if (sub.equals("invite")) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null || target.equals(player)) {
                    MessageUtil.sendMessage(player, ConfigLoader.msgGroupPlayerNotFound);
                    return true;
                }
                groupManager.sendInvite(player, target);

                if (groupManager.isLeader(player)) {
                    MessageUtil.sendMessage(player,
                            ConfigLoader.msgGroupInvitedSender.replace("%target%", target.getName()));
                    MessageUtil.sendMessage(target,
                            ConfigLoader.msgGroupInvitedTarget.replace("%player%", player.getName()));
                }
                return true;
            }

            if (sub.equals("exile")) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null || target.equals(player)) {
                    MessageUtil.sendMessage(player, ConfigLoader.msgGroupPlayerNotFound);
                    return true;
                }
                groupManager.exilePlayer(player, target);
                return true;
            }
        }

        MessageUtil.sendMessage(player, ConfigLoader.msgGroupUsage);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String input = args[0].toLowerCase();
            List<String> options = List.of("create", "invite", "exile", "accept", "leave", "delete", "info");

            for (String opt : options) {
                if (opt.startsWith(input)) completions.add(opt);
            }

        } else if (args.length == 2) {
            String sub = args[0].toLowerCase();

            if (sub.equals("invite") || sub.equals("exile")) {
                String input = args[1].toLowerCase();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (sender instanceof Player sp && p.equals(sp)) continue;
                    if (p.getName().toLowerCase().startsWith(input)) completions.add(p.getName());
                }
            }
        }

        return completions;
    }
}

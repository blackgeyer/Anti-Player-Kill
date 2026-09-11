package org.antipk;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PvPCommand implements CommandExecutor, TabCompleter {

    private final DuelManager duelManager;
    private final GroupManager groupManager;

    public PvPCommand(DuelManager duelManager, GroupManager groupManager) {
        this.duelManager = duelManager;
        this.groupManager = groupManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtil.sendMessage(sender, ConfigLoader.initErrorMsg);
            return true;
        }

        if (args.length == 1) {
            String sub = args[0].toLowerCase();

            if (sub.equals("end")) {
                duelManager.voteEnd(player);
                return true;
            }

            if (groupManager.isInGroup(player.getUniqueId()) && !groupManager.isLeader(player)) {
                MessageUtil.sendMessage(player, ConfigLoader.msgNotLeader);
                return true;
            }

            if (sub.equals("confirm") || sub.equals("accept")) {
                duelManager.acceptRequest(player);
                return true;
            }

            if (sub.equals("deny")) {
                duelManager.denyRequest(player);
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null || target.equals(player)) {
                MessageUtil.sendMessage(player, ConfigLoader.msgGroupPlayerNotFound);
                return true;
            }

            duelManager.sendRequest(player, target);
            return true;
        }

        MessageUtil.sendMessage(player, ConfigLoader.msgPvpUsage);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String input = args[0].toLowerCase();
            List<String> options = List.of("confirm", "deny", "end");

            for (String opt : options) {
                if (opt.startsWith(input)) completions.add(opt);
            }

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (sender instanceof Player sp && p.equals(sp)) continue;
                if (p.getName().toLowerCase().startsWith(input)) completions.add(p.getName());
            }
        }

        return completions;
    }
}
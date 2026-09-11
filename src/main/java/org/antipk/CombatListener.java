package org.antipk;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CombatListener implements Listener {

    private final CombatManager combatManager;
    private final JudgementManager judgementManager;
    private final DuelManager duelManager;
    private final DumpManager dumpManager;

    public CombatListener(CombatManager combatManager, JudgementManager judgementManager, DuelManager duelManager, DumpManager dumpManager) {
        this.combatManager = combatManager;
        this.judgementManager = judgementManager;
        this.duelManager = duelManager;
        this.dumpManager = dumpManager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (attacker.equals(victim)) return;

        if (attacker.hasPermission("antipk.bypass.aggression")) return;
        if (duelManager.isInSameDuel(attacker.getUniqueId(), victim.getUniqueId())) return;

        combatManager.registerHit(attacker, victim, event.getFinalDamage());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        UUID victimId = victim.getUniqueId();

        if (duelManager.isInDuel(victimId)) {
            DuelSession session = duelManager.getSession(victimId);
            Set<UUID> allParticipants = new HashSet<>(session.getParticipants());

            boolean isMatchOver = duelManager.handleDeath(victimId);
            combatManager.clear(victimId);

            if (isMatchOver) {
                Set<UUID> winners = session.getWinningTeamMembers();
                String winnerNames = winners.stream()
                        .map(uuid -> victim.getServer().getPlayer(uuid))
                        .filter(Objects::nonNull)
                        .map(Player::getName)
                        .collect(Collectors.joining(", "));

                String matchOverMsg = ConfigLoader.msgPvpMatchOverWinner;
                String message = matchOverMsg.replace("%winners%", winnerNames);

                for (UUID uuid : allParticipants) {
                    Player p = victim.getServer().getPlayer(uuid);
                    if (p != null) {
                        MessageUtil.sendMessage(p, message);
                    }
                }
            } else {
                String eliminatedMsg = ConfigLoader.msgPvpPlayerEliminated;
                String message = eliminatedMsg.replace("%player%", victim.getName());

                for (UUID uuid : session.getParticipants()) {
                    Player p = victim.getServer().getPlayer(uuid);
                    if (p != null) {
                        MessageUtil.sendMessage(p, message);
                    }
                }
            }
            return;
        }

        Set<UUID> attackers = combatManager.getActiveAttackers(victimId);
        boolean isRdm = false;
        String killerName = killer != null ? killer.getName() : "Unknown";

        if (!attackers.isEmpty()) {
            for (UUID attackerId : attackers) {
                Player attacker = victim.getServer().getPlayer(attackerId);
                if (attacker == null) continue;

                if (!combatManager.isSelfDefense(attacker, victim)) {
                    judgementManager.processRdm(attacker, victim);
                    isRdm = true;
                }
            }
        } else if (killer != null && !killer.equals(victim)) {
            if (!combatManager.isSelfDefense(killer, victim)) {
                judgementManager.processRdm(killer, victim);
                isRdm = true;
            }
        }

        if (isRdm) {
            if (ConfigLoader.dumpsCreating) {
                dumpManager.createDump(victim, killerName, "RDM");
            }
            if (ConfigLoader.invClearing) {
                event.getDrops().clear();
                event.setDroppedExp(0);
            }
        }

        combatManager.clear(victimId);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (duelManager.isInDuel(playerId)) {
            DuelSession session = duelManager.getSession(playerId);
            Set<UUID> allParticipants = new HashSet<>(session.getParticipants());

            boolean isMatchOver = duelManager.handleDeath(playerId);
            combatManager.clear(playerId);

            if (isMatchOver) {
                Set<UUID> winners = session.getWinningTeamMembers();
                String winnerNames = winners.stream()
                        .map(uuid -> player.getServer().getPlayer(uuid))
                        .filter(Objects::nonNull)
                        .map(Player::getName)
                        .collect(Collectors.joining(", "));

                String fledMsg = ConfigLoader.msgPvpPlayerFled;
                String message = fledMsg
                        .replace("%player%", player.getName())
                        .replace("%winners%", winnerNames);

                for (UUID uuid : allParticipants) {
                    Player p = player.getServer().getPlayer(uuid);
                    if (p != null) {
                        MessageUtil.sendMessage(p, message);
                    }
                }
            } else {
                String disqMsg = ConfigLoader.msgPvpPlayerFledDisqualified;
                String message = disqMsg.replace("%player%", player.getName());

                for (UUID uuid : session.getParticipants()) {
                    Player p = player.getServer().getPlayer(uuid);
                    if (p != null) {
                        MessageUtil.sendMessage(p, message);
                    }
                }
            }
        }
    }
}
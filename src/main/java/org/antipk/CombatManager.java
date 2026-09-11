package org.antipk;

import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CombatManager {

    private final Map<UUID, Map<UUID, AggressionEntry>> combatCache = new ConcurrentHashMap<>();

    public void registerHit(Player attacker, Player victim, double damage) {
        UUID victimId = victim.getUniqueId();
        UUID attackerId = attacker.getUniqueId();

        Map<UUID, AggressionEntry> victimAttackers = combatCache.computeIfAbsent(victimId, k -> new ConcurrentHashMap<>());
        AggressionEntry entry = victimAttackers.get(attackerId);

        long ttl = ConfigLoader.ttlMillis;
        if (entry == null || entry.isExpired(ttl)) {
            victimAttackers.put(attackerId, new AggressionEntry(attackerId, damage));
        } else {
            entry.addHit(damage);
        }
    }

    public boolean isSelfDefense(Player killer, Player victim) {
        UUID killerId = killer.getUniqueId();
        UUID victimId = victim.getUniqueId();

        Map<UUID, AggressionEntry> killerAttackers = combatCache.get(killerId);
        if (killerAttackers == null) {
            return false;
        }

        long ttl = ConfigLoader.ttlMillis;
        AggressionEntry entry = killerAttackers.get(victimId);
        if (entry == null || entry.isExpired(ttl)) {
            return false;
        }

        double minDamage = ConfigLoader.minDamageThreshold;
        int minHits = ConfigLoader.minHitsThreshold;

        boolean reachedDamageLimit = entry.getAccumulatedDamage() >= minDamage;
        boolean reachedHitsLimit = entry.getHitCount() >= minHits;

        return reachedDamageLimit || reachedHitsLimit;
    }

    public Set<UUID> getActiveAttackers(UUID victimId) {
        Map<UUID, AggressionEntry> victimAttackers = combatCache.get(victimId);
        if (victimAttackers == null) return Collections.emptySet();

        long ttl = ConfigLoader.ttlMillis;
        return victimAttackers.entrySet().stream()
                .filter(entry -> !entry.getValue().isExpired(ttl))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public double getTotalAccumulatedDamage(UUID victimId) {
        Map<UUID, AggressionEntry> victimAttackers = combatCache.get(victimId);
        if (victimAttackers == null) return 0.0;

        long ttl = ConfigLoader.ttlMillis;
        return victimAttackers.values().stream()
                .filter(entry -> !entry.isExpired(ttl))
                .mapToDouble(AggressionEntry::getAccumulatedDamage)
                .sum();
    }

    public void clear(UUID victimId) {
        combatCache.remove(victimId);
    }
}
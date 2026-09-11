package org.antipk;

import java.util.UUID;

public class AggressionEntry {
    private final UUID aggressorId;
    private long lastAttackTime;
    private double accumulatedDamage;
    private int hitCount;

    public AggressionEntry(UUID aggressorId, double initialDamage) {
        this.aggressorId = aggressorId;
        this.lastAttackTime = System.currentTimeMillis();
        this.accumulatedDamage = initialDamage;
        this.hitCount = 1;
    }

    public void addHit(double damage) {
        this.lastAttackTime = System.currentTimeMillis();
        this.accumulatedDamage += damage;
        this.hitCount++;
    }

    public boolean isExpired(long ttlMillis) {
        return (System.currentTimeMillis() - lastAttackTime) > ttlMillis;
    }

    public UUID getAggressorId() {
        return aggressorId;
    }

    public double getAccumulatedDamage() {
        return accumulatedDamage;
    }

    public int getHitCount() {
        return hitCount;
    }
}
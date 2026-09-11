package org.antipk;

import org.bukkit.plugin.java.JavaPlugin;

public final class AntiPlayerKill extends JavaPlugin {

    private static AntiPlayerKill instance;
    private DumpManager dumpManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        ConfigLoader.load(getConfig(), this);

        GroupManager groupManager = new GroupManager();
        DuelManager duelManager = new DuelManager(groupManager);
        CombatManager combatManager = new CombatManager();
        JudgementManager judgementManager = new JudgementManager(this);
        this.dumpManager = new DumpManager(this);

        getServer().getPluginManager().registerEvents(
                new CombatListener(combatManager, judgementManager, duelManager, dumpManager), this
        );

        GroupCommand groupCommand = new GroupCommand(groupManager, duelManager);
        getCommand("group").setExecutor(groupCommand);
        getCommand("group").setTabCompleter(groupCommand);

        PvPCommand pvpCommand = new PvPCommand(duelManager, groupManager);
        getCommand("pvp").setExecutor(pvpCommand);
        getCommand("pvp").setTabCompleter(pvpCommand);

        AntiPKCommand antiPKCommand = new AntiPKCommand(this, dumpManager, judgementManager);
        getCommand("antipk").setExecutor(antiPKCommand);
        getCommand("antipk").setTabCompleter(antiPKCommand);

        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            dumpManager.cleanupExpiredDumps();
        }, 36000L, 36000L);

        getLogger().info("AntiPlayerKill successfully enabled.");
    }

    @Override
    public void onDisable() {
        if (dumpManager != null) {
            dumpManager.saveDumpsSync(); 
        }
        getLogger().info("AntiPlayerKill disabled.");
    }

    public static AntiPlayerKill getInstance() {
        return instance;
    }

    public DumpManager getDumpManager() {
        return dumpManager;
    }
}
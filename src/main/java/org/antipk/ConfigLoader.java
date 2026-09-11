package org.antipk;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigLoader {

    // Warns section
    public static boolean warnsEnabled;
    public static boolean warnIsDynamic;
    public static long warnDecayMillis;
    public static String warnMsg;
    public static String lastWarnMsg;
    public static boolean warnMsgEnabled;
    public static int warnLimit;
    public static String staticWarnMsg;

    // Action section
    public static boolean actionsEnabled;
    public static String punishmentCommand;
    public static String defaultReason;
    public static boolean relapseDetect;
    public static String basePunishmentDuration;
    public static double repeatMultiplier;
    public static String maxDuration;

    // Combat section
    public static long ttlMillis;
    public static double minDamageThreshold;
    public static int minHitsThreshold;

    // Dumps section
    public static boolean dumpsCreating;
    public static boolean invClearing;
    public static String clearTime;

    // Group messages
    public static String msgGroupCreated;
    public static String msgGroupDeleted;
    public static String msgGroupInvitedSender;
    public static String msgGroupInvitedTarget;
    public static String msgGroupAccepted;
    public static String msgGroupNoInvites;
    public static String msgGroupPlayerNotFound;
    public static String msgGroupInDuelError;
    public static String msgGroupUsage;
    public static String msgGroupDisbanded;
    public static String msgNoGroup;
    public static String msgNotLeader;

    public static String msgGroupLeft;          
    public static String msgGroupPlayerLeft;   
    public static String msgGroupNewLeader;    

    public static String msgGroupExiledSender;  
    public static String msgGroupExiledTarget;  
    public static String msgGroupNotMember;    
    public static String msgGroupCannotExileSelf;

    public static String msgGroupInfoHeader;     
    public static String msgGroupInfoLeader;    
    public static String msgGroupInfoMembers;    
    public static String msgGroupInfoOfflineMarker;


    // PvP / Duel messages
    public static String msgPvpRequestSent;
    public static String msgPvpRequestReceivedSolo;
    public static String msgPvpRequestReceivedGroup;
    public static String msgPvpSoloVsGroupError;
    public static String msgPvpAlreadyInDuel;
    public static String msgPvpNoActiveRequest;
    public static String msgPvpRequestDenied;
    public static String msgPvpRequestDeniedSender;
    public static String msgPvpEndVoteAdded;
    public static String msgPvpEndedPeacefully;
    public static String msgPvpPlayerEliminated;
    public static String msgPvpMatchOverWinner;
    public static String msgPvpPlayerFled;
    public static String msgPvpPlayerFledDisqualified;
    public static String msgPvpUsage;



    // System messages
    public static String msgReloadSuccess;
    public static String msgNoPermission;
    public static String reloadUsage;
    public static String initErrorMsg;

    public static String dumpNotFound;
    public static String dumpIsExpired;
    public static String spaceError;
    public static String dumpInfo;

    public static String dumpRestoreSuc;
    public static String dumpRestoreDeny;
    public static String areYouSure;

    public static String dumpListHeader;
    public static String dumpListItem;
    public static String dumpListEmpty;
    public static String dumpUsage;



    public static void load(FileConfiguration config, AntiPlayerKill plugin) {

        // Warns section uploading
        warnsEnabled = config.getBoolean("warns.warns-is-enabled");
        warnIsDynamic = config.getBoolean("warns.warn-is-dynamic");
        warnDecayMillis = config.getLong("warns.warn-decay-minutes") * 60L * 1000L;
        warnMsg = config.getString("warns.warn-msg");
        lastWarnMsg = config.getString("warns.last-warn-msg");
        warnMsgEnabled = config.getBoolean("warns.warns-msg-is-enabled");
        warnLimit = config.getInt("warns.warn-limit");
        staticWarnMsg = config.getString("warns.static-warn-msg");

        // Actions section uploading
        actionsEnabled = config.getBoolean("actions.actions-is-enabled");
        punishmentCommand = config.getString("actions.punishment-command");
        defaultReason = config.getString("actions.default-reason");
        relapseDetect = config.getBoolean("actions.relapse-detect");
        basePunishmentDuration = config.getString("actions.base-punishment-duration");
        repeatMultiplier = config.getDouble("actions.repeat-multiplier");
        maxDuration = config.getString("actions.max-duration");

        // Combat section uploading
        ttlMillis = config.getLong("combat.ttl-seconds") * 1000L;
        minDamageThreshold = config.getDouble("combat.min-damage-threshold");
        minHitsThreshold = config.getInt("combat.min-hits-threshold");

        // Dumps section uploading
        dumpsCreating = config.getBoolean("dumps.dump-creating");
        invClearing = config.getBoolean("dumps.inv-clear-when-die");
        clearTime = config.getString("dumps.clear-time");
        

        // Group messages uploading
        msgGroupCreated = config.getString("messages.group.created");
        msgGroupDeleted = config.getString("messages.group.deleted");
        msgGroupInvitedSender = config.getString("messages.group.invited-sender");
        msgGroupInvitedTarget = config.getString("messages.group.invited-target");
        msgGroupAccepted = config.getString("messages.group.accepted");
        msgGroupNoInvites = config.getString("messages.group.no-invites");
        msgGroupPlayerNotFound = config.getString("messages.group.player-not-found");
        msgGroupInDuelError = config.getString("messages.group.in-duel-error");
        msgGroupUsage = config.getString("messages.group.usage");
        msgGroupDisbanded = config.getString("messsages.group.group-disbanded");
        msgNoGroup = config.getString("messages.group.no-group");
        msgNotLeader = config.getString("messages.group-not-leader");

        msgGroupLeft = config.getString("messages.group.left");
        msgGroupPlayerLeft = config.getString("messages.group.player-left");
        msgGroupNewLeader = config.getString("messages.group.new-leader");
 
        msgGroupExiledSender = config.getString("messages.group.exiled-sender");
        msgGroupExiledTarget = config.getString("messages.group.exiled-target");
        msgGroupNotMember = config.getString("messages.group.not-member");
        msgGroupCannotExileSelf = config.getString("messages.group.cannot-exile-self");

        msgGroupInfoHeader = config.getString("messages.group.info-header");
        msgGroupInfoLeader = config.getString("messages.group.info-leader");
        msgGroupInfoMembers = config.getString("messages.group.info-members");
        msgGroupInfoOfflineMarker = config.getString("messages.group.info-offline-marker");



        // PvP messages uploading
        msgPvpRequestSent = config.getString("messages.pvp.request-sent");
        msgPvpRequestReceivedSolo = config.getString("messages.pvp.request-received-solo");
        msgPvpRequestReceivedGroup = config.getString("messages.pvp.request-received-group");
        msgPvpSoloVsGroupError = config.getString("messages.pvp.solo-vs-group-error");
        msgPvpAlreadyInDuel = config.getString("messages.pvp.already-in-duel");
        msgPvpNoActiveRequest = config.getString("messages.pvp.no-active-request");
        msgPvpRequestDenied = config.getString("messages.pvp.request-denied");
        msgPvpRequestDeniedSender = config.getString("messages.pvp.request-denied-sender");
        msgPvpEndVoteAdded = config.getString("messages.pvp.end-vote-added");
        msgPvpEndedPeacefully = config.getString("messages.pvp.ended-peacefully");
        msgPvpPlayerEliminated = config.getString("messages.pvp.player-eliminated");
        msgPvpMatchOverWinner = config.getString("messages.pvp.match-over-winner");
        msgPvpPlayerFled = config.getString("messages.pvp.player-fled");
        msgPvpPlayerFledDisqualified = config.getString("messages.pvp.player-fled-disqualified");
        msgPvpUsage = config.getString("messages.pvp.usage");

        // System messages uploading
        msgReloadSuccess = config.getString("messages.system.reload-success");
        msgNoPermission = config.getString("messages.system.no-permission");
        reloadUsage = config.getString("messages.system.reload-usage");
        initErrorMsg = config.getString("messages.system.initiator-error-msg");

        // Dump messages uploading
        dumpNotFound = config.getString("messages.system.dump-not-found");
        dumpIsExpired = config.getString("messages.system.dump-expired");
        spaceError = config.getString("messages.system.space-error");
        dumpInfo = config.getString("messages.system.dump-info");

        dumpRestoreSuc = config.getString("messages.system.dump-restore-suc");
        dumpRestoreDeny = config.getString("messages.system.dump-restore-deny");
        areYouSure = config.getString("messages.system.are-you-sure");

        dumpListHeader = config.getString("messages.system.dump-list-header");
        dumpListItem = config.getString("messages.system.dump-list-item");
        dumpListEmpty = config.getString("messages.system.dump-list-empty");
        dumpUsage = config.getString("messages.system.dump-usage");



        if (warnsEnabled && !actionsEnabled) {
            plugin.getLogger().warning("Configuration error, caused by:");
            plugin.getLogger().warning("Warns is enabled but actions isn't.");
            plugin.getLogger().warning("Plugin will continue to work however, proper plugin operation is not guaranteed. Use at your own risk.");
        }

        if (dumpsCreating && !invClearing) {
            plugin.getLogger().warning("Configuration error, caused by:");
            plugin.getLogger().warning("Dump generation is enabled, but victim resource cleanup on unauthorized death is disabled.");
            plugin.getLogger().warning("This may cause resource duplication, as the victim's inventory will be duplicated and remain accessible to both the aggressor and the generated dump.");
            plugin.getLogger().warning("Plugin will continue to work however, proper plugin operation is not guaranteed. Use at your own risk.");
        }
    }
    
    public static long getClearTimeMillis() {
        String val = clearTime;
        if (val == null || val.isEmpty()) return 7L * 86400000L;
        try {
            char unit = val.charAt(val.length() - 1);
            long num = Long.parseLong(val.substring(0, val.length() - 1));
            return switch (Character.toLowerCase(unit)) {
                case 'd' -> num * 86400000L;
                case 'h' -> num * 3600000L;
                case 'm' -> num * 60000L;
                case 's' -> num * 1000L;
                default -> Long.parseLong(val) * 1000L;
            };
        } catch (Exception e) {
            return 7L * 86400000L;
        }
    }
}

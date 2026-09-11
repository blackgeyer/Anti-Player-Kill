package org.antipk;

import org.bukkit.entity.Player;
import java.util.*;

public class DuelManager {

    private final GroupManager groupManager;
    private final Map<UUID, DuelSession> activeSessions = new HashMap<>();
    private final Map<UUID, DuelRequest> pendingRequests = new HashMap<>();

    public DuelManager(GroupManager groupManager) {
        this.groupManager = groupManager;
    }

    public void sendRequest(Player sender, Player target) {
        boolean senderInDuel = isInDuel(sender.getUniqueId());
        boolean targetInDuel = isInDuel(target.getUniqueId());
        
        if (senderInDuel || targetInDuel) {
            String msgAlready = ConfigLoader.msgPvpAlreadyInDuel;
            MessageUtil.sendMessage(sender, msgAlready);
            return;
        }

        UUID senderGroupId = groupManager.getGroupId(sender.getUniqueId());
        int groupSize = senderGroupId != null ? groupManager.getGroupSize(senderGroupId) : 1;

        pendingRequests.put(target.getUniqueId(), new DuelRequest(sender.getUniqueId(), target.getUniqueId(), senderGroupId));

        String requestSent = ConfigLoader.msgPvpRequestSent.replace("%target%", target.getName());
        MessageUtil.sendMessage(sender, requestSent);

        if (groupSize > 1) {
            String requestReceivedGroup = ConfigLoader.msgPvpRequestReceivedGroup
                    .replace("%player%", sender.getName())
                    .replace("%size%", String.valueOf(groupSize));
            MessageUtil.sendMessage(target, requestReceivedGroup);
        } else {
            String requestReceivedSolo = ConfigLoader.msgPvpRequestReceivedSolo.replace("%player%", sender.getName());
            MessageUtil.sendMessage(target, requestReceivedSolo);
        }
    }

    public void acceptRequest(Player target) {
    UUID targetId = target.getUniqueId();
    DuelRequest request = pendingRequests.remove(targetId);

    if (request == null) {
        String noRequest = ConfigLoader.msgPvpNoActiveRequest;
        MessageUtil.sendMessage(target, noRequest);
        return;
    }

    Player sender = target.getServer().getPlayer(request.getSenderId());
    if (sender == null || isInDuel(sender.getUniqueId()) || isInDuel(targetId)) {
        String msgAlready = ConfigLoader.msgPvpAlreadyInDuel;
        MessageUtil.sendMessage(target, msgAlready);
        return;
    }

    UUID senderGroupId = request.getSenderGroupId();
    UUID targetGroupId = groupManager.getGroupId(targetId);

    if (senderGroupId != null && targetGroupId == null) {
        String soloVsGroup = ConfigLoader.msgPvpSoloVsGroupError;
        MessageUtil.sendMessage(target, soloVsGroup);
        return;
    }

    DuelSession session = new DuelSession(sender.getUniqueId(), senderGroupId, targetId, targetGroupId);
    for (UUID uuid : session.getParticipants()) {
        activeSessions.put(uuid, session);
    }
}

    public void denyRequest(Player target) {
        DuelRequest request = pendingRequests.remove(target.getUniqueId());
        if (request == null) {
            String noRequest = ConfigLoader.msgPvpNoActiveRequest;
            MessageUtil.sendMessage(target, noRequest);
            return;
        }

        String requestDenied = ConfigLoader.msgPvpRequestDenied;
        MessageUtil.sendMessage(target, requestDenied);

        Player sender = target.getServer().getPlayer(request.getSenderId());
        if (sender != null) {
            String deniedSender = ConfigLoader.msgPvpRequestDeniedSender.replace("%player%", target.getName());
            MessageUtil.sendMessage(sender, deniedSender);
        }
    }

    public void voteEnd(Player player) {
        UUID playerId = player.getUniqueId();
        DuelSession session = activeSessions.get(playerId);

        if (session == null) {
            String noRequest = ConfigLoader.msgPvpNoActiveRequest;
            MessageUtil.sendMessage(player, noRequest);
            return;
        }

        boolean ended = session.voteEnd(playerId);
        String voteMsg = ConfigLoader.msgPvpEndVoteAdded
                .replace("%player%", player.getName())
                .replace("%votes%", String.valueOf(session.getVotesCount()))
                .replace("%total%", String.valueOf(session.getParticipants().size()));

        for (UUID uuid : session.getParticipants()) {
            Player p = player.getServer().getPlayer(uuid);
            if (p != null) {
                MessageUtil.sendMessage(p, voteMsg);
            }
        }

        if (ended) {
            String peaceEnd = ConfigLoader.msgPvpEndedPeacefully;
            for (UUID uuid : session.getParticipants()) {
                activeSessions.remove(uuid);
                Player p = player.getServer().getPlayer(uuid);
                if (p != null) {
                    MessageUtil.sendMessage(p, peaceEnd);
                }
            }
        }
    }

    public boolean isInDuel(UUID playerId) {
        return activeSessions.containsKey(playerId);
    }

    public boolean isInSameDuel(UUID player1, UUID player2) {
        DuelSession s1 = activeSessions.get(player1);
        DuelSession s2 = activeSessions.get(player2);
        return s1 != null && s1.equals(s2);
    }

    public DuelSession getSession(UUID playerId) {
        return activeSessions.get(playerId);
    }

    public boolean handleDeath(UUID playerId) {
        DuelSession session = activeSessions.remove(playerId);
        if (session == null) return false;

        session.removePlayer(playerId);
        return session.getRemainingTeamsCount() <= 1;
    }
}
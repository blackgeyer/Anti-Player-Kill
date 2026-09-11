package org.antipk;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DuelSession {

    private final Map<UUID, UUID> playerTeamMap = new ConcurrentHashMap<>();
    private final Set<UUID> votesForEnd = ConcurrentHashMap.newKeySet();

    public DuelSession(UUID senderId, UUID senderGroupId, UUID targetId, UUID targetGroupId) {
        UUID team1 = (senderGroupId != null) ? senderGroupId : senderId;
        UUID team2 = (targetGroupId != null) ? targetGroupId : targetId;

        playerTeamMap.put(senderId, team1);
        playerTeamMap.put(targetId, team2);
    }

    public Set<UUID> getParticipants() {
        return playerTeamMap.keySet();
    }

    public boolean voteEnd(UUID playerId) {
        votesForEnd.add(playerId);
        return votesForEnd.size() >= playerTeamMap.size();
    }

    public int getVotesCount() {
        return votesForEnd.size();
    }

    public void removePlayer(UUID playerId) {
        playerTeamMap.remove(playerId);
        votesForEnd.remove(playerId);
    }

    public int getRemainingTeamsCount() {
        return (int) playerTeamMap.values().stream().distinct().count();
    }

    public Set<UUID> getWinningTeamMembers() {
        if (getRemainingTeamsCount() != 1) return Collections.emptySet();

        UUID winningTeamId = playerTeamMap.values().iterator().next();
        Set<UUID> winners = new HashSet<>();

        for (Map.Entry<UUID, UUID> entry : playerTeamMap.entrySet()) {
            if (entry.getValue().equals(winningTeamId)) {
                winners.add(entry.getKey());
            }
        }

        return winners;
    }
}
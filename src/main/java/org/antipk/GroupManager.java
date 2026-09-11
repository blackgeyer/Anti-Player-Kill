package org.antipk;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GroupManager {

    private final Map<UUID, UUID> playerGroupMap = new ConcurrentHashMap<>();
    private final Map<UUID, Set<UUID>> groups = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> pendingGroupInvites = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> groupLeaders = new ConcurrentHashMap<>();

    private void removeFromGroup(UUID playerId) {
        UUID groupId = playerGroupMap.remove(playerId);
        if (groupId == null) return;

        Set<UUID> members = groups.get(groupId);
        if (members == null) return;

        members.remove(playerId);

        if (members.isEmpty()) {
            groups.remove(groupId);
            groupLeaders.remove(groupId);
            return;
        }

        if (playerId.equals(groupLeaders.get(groupId))) {
            UUID newLeaderId = members.iterator().next();
            groupLeaders.put(groupId, newLeaderId);
        }
    }

    public boolean isInGroup(UUID playerId) {
        return playerGroupMap.containsKey(playerId);
    }

    public UUID getGroupId(UUID playerId) {
        return playerGroupMap.get(playerId);
    }

    public int getGroupSize(UUID groupId) {
        Set<UUID> members = groups.get(groupId);
        return members != null ? members.size() : 0;
    }

    public Set<UUID> getGroupMembers(UUID groupId) {
        return groups.get(groupId);
    }

    public UUID getGroupLeader(UUID groupId) {
        return groupLeaders.get(groupId);
    }

    public boolean isLeader(Player player) {
        UUID playerId = player.getUniqueId();
        UUID groupId = playerGroupMap.get(playerId);
        if (groupId == null) return false;
        return playerId.equals(groupLeaders.get(groupId));
    }


    public UUID createGroup(Player leader) {
        UUID playerId = leader.getUniqueId();

        removeFromGroup(playerId);

        UUID groupId = UUID.randomUUID();
        Set<UUID> members = ConcurrentHashMap.newKeySet();
        members.add(playerId);

        groups.put(groupId, members);
        playerGroupMap.put(playerId, groupId);
        groupLeaders.put(groupId, playerId);
        return groupId;
    }


    public void deleteGroup(Player player) {
        UUID playerId = player.getUniqueId();
        UUID groupId = playerGroupMap.get(playerId);

        if (groupId == null) {
            MessageUtil.sendMessage(player, ConfigLoader.msgNoGroup);
            return;
        }
        if (!isLeader(player)) {
            MessageUtil.sendMessage(player, ConfigLoader.msgNotLeader);
            return;
        }

        Set<UUID> members = groups.remove(groupId);
        groupLeaders.remove(groupId);

        if (members != null) {
            for (UUID memberId : members) {
                playerGroupMap.remove(memberId);
                Player member = Bukkit.getPlayer(memberId);
                if (member == null) continue;

                if (memberId.equals(playerId)) {
                    MessageUtil.sendMessage(member, ConfigLoader.msgGroupDeleted);
                } else {
                    MessageUtil.sendMessage(member, ConfigLoader.msgGroupDisbanded);
                }
            }
        }
    }


    public void sendInvite(Player sender, Player target) {
        UUID senderId = sender.getUniqueId();
        UUID groupId = playerGroupMap.get(senderId);

        if (groupId == null) {
            MessageUtil.sendMessage(sender, ConfigLoader.msgNoGroup);
            return;
        }
        if (!isLeader(sender)) {
            MessageUtil.sendMessage(sender, ConfigLoader.msgNotLeader);
            return;
        }

        pendingGroupInvites.put(target.getUniqueId(), groupId);
    }

    public boolean hasInvite(UUID targetId) {
        return pendingGroupInvites.containsKey(targetId);
    }

    public UUID acceptInvite(Player target) {
        UUID targetId = target.getUniqueId();
        UUID groupId = pendingGroupInvites.remove(targetId);

        removeFromGroup(targetId);

        Set<UUID> members = groups.get(groupId);
        if (members != null) {
            members.add(targetId);
            playerGroupMap.put(targetId, groupId);
        }
        return groupId;
    }

    public void leaveGroupCommand(Player player) {
        UUID playerId = player.getUniqueId();
        UUID groupId = playerGroupMap.get(playerId);

        if (groupId == null) {
            MessageUtil.sendMessage(player, ConfigLoader.msgNoGroup);
            return;
        }

        boolean wasLeader = isLeader(player);

        removeFromGroup(playerId);

        MessageUtil.sendMessage(player, ConfigLoader.msgGroupLeft);

        Set<UUID> remaining = groups.get(groupId);
        if (remaining == null || remaining.isEmpty()) return;

        if (wasLeader) {
            UUID newLeaderId = groupLeaders.get(groupId);
            Player newLeader = newLeaderId != null ? Bukkit.getPlayer(newLeaderId) : null;
            String newLeaderName = newLeader != null ? newLeader.getName() : "Unknown";
            String msg = ConfigLoader.msgGroupNewLeader.replace("%player%", newLeaderName);

            for (UUID memberId : remaining) {
                Player member = Bukkit.getPlayer(memberId);
                if (member != null) MessageUtil.sendMessage(member, msg);
            }
        } else {
            String msg = ConfigLoader.msgGroupPlayerLeft.replace("%player%", player.getName());
            for (UUID memberId : remaining) {
                Player member = Bukkit.getPlayer(memberId);
                if (member != null) MessageUtil.sendMessage(member, msg);
            }
        }
    }

    public void exilePlayer(Player leader, Player target) {
        UUID leaderId = leader.getUniqueId();
        UUID targetId = target.getUniqueId();

        if (!isLeader(leader)) {
            MessageUtil.sendMessage(leader, ConfigLoader.msgNotLeader);
            return;
        }

        UUID groupId = playerGroupMap.get(leaderId);
        if (groupId == null) {
            MessageUtil.sendMessage(leader, ConfigLoader.msgNoGroup);
            return;
        }

        if (targetId.equals(leaderId)) {
            MessageUtil.sendMessage(leader, ConfigLoader.msgGroupCannotExileSelf);
            return;
        }

        if (!groupId.equals(playerGroupMap.get(targetId))) {
            MessageUtil.sendMessage(leader, ConfigLoader.msgGroupNotMember);
            return;
        }

        removeFromGroup(targetId);

        MessageUtil.sendMessage(target, ConfigLoader.msgGroupExiledTarget);

        String broadcastMsg = ConfigLoader.msgGroupExiledSender.replace("%player%", target.getName());

        Set<UUID> remaining = groups.get(groupId);
        if (remaining != null) {
            for (UUID memberId : remaining) {
                Player member = Bukkit.getPlayer(memberId);
                if (member != null) MessageUtil.sendMessage(member, broadcastMsg);
            }
        }
    }
}
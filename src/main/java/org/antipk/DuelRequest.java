package org.antipk;

import java.util.UUID;

public class DuelRequest {
    private final UUID senderId;
    private final UUID targetId;
    private final UUID senderGroupId;

    public DuelRequest(UUID senderId, UUID targetId, UUID senderGroupId) {
        this.senderId = senderId;
        this.targetId = targetId;
        this.senderGroupId = senderGroupId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public UUID getTargetId() {
        return targetId;
    }

    public UUID getSenderGroupId() {
        return senderGroupId;
    }
}

package org.antipk;

import java.util.ArrayDeque;
import java.util.Deque;

public class WarnContainer {
    private final Deque<Long> warnTimestamps = new ArrayDeque<>();

    public synchronized int addWarnAndGetCount(boolean isDynamic, long decayMillis) {
        long now = System.currentTimeMillis();

        if (isDynamic) {
            warnTimestamps.removeIf(timestamp -> (now - timestamp) > decayMillis);
        }

        warnTimestamps.addLast(now);
        return warnTimestamps.size();
    }
}

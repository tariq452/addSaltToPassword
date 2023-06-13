package org.example;

import java.util.HashMap;
import java.util.Map;

public class RateLimiter {
    private Map<String, Map<String, Long>> userPathTimestamps;
    private int limit;
    private long interval;

    public RateLimiter(int limit, long interval) {
        this.userPathTimestamps = new HashMap<>();
        this.limit = limit;
        this.interval = interval;
    }

    public synchronized boolean allowRequest(String user, String path) {
        long currentTime = System.currentTimeMillis();
        Map<String, Long> pathTimestamps = userPathTimestamps.get(user);
        if (pathTimestamps == null) {
            pathTimestamps = new HashMap<>();
            userPathTimestamps.put(user, pathTimestamps);
        }
        Long lastRequestTime = pathTimestamps.get(path);
        if (lastRequestTime == null || currentTime - lastRequestTime > interval) {
            System.out.println("currentTime: "+currentTime+ " lastRequestTime: "+lastRequestTime+ " interval:"+ interval);
            // Allow the request.
            pathTimestamps.put(path, currentTime);
            return true;
        } else {
            // Reject the request.
            return false;
        }
    }
}

package com.loginFeature.login.utility;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimiter {
    
    private final ConcurrentHashMap<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();
    
    // Rate limit configurations
    private static final int MAX_POSTS_PER_DAY = 5;
    private static final int MAX_COMMENTS_PER_HOUR = 20;
    private static final int MAX_LOGIN_ATTEMPTS_PER_HOUR = 5;
    private static final int MAX_VOTES_PER_HOUR = 50;
    
    private static final long HOUR_IN_MILLIS = 60 * 60 * 1000;
    private static final long DAY_IN_MILLIS = 24 * 60 * 60 * 1000;

    public boolean isPostCreationAllowed(String userId) {
        String key = "posts_" + userId;
        return isAllowed(key, MAX_POSTS_PER_DAY, DAY_IN_MILLIS);
    }

    public boolean isCommentCreationAllowed(String userId) {
        String key = "comments_" + userId;
        return isAllowed(key, MAX_COMMENTS_PER_HOUR, HOUR_IN_MILLIS);
    }

    public boolean isLoginAttemptAllowed(String email) {
        String key = "login_" + email;
        return isAllowed(key, MAX_LOGIN_ATTEMPTS_PER_HOUR, HOUR_IN_MILLIS);
    }

    public boolean isVotingAllowed(String userId) {
        String key = "votes_" + userId;
        return isAllowed(key, MAX_VOTES_PER_HOUR, HOUR_IN_MILLIS);
    }

    private boolean isAllowed(String key, int maxRequests, long timeWindow) {
        long currentTime = System.currentTimeMillis();
        RateLimitInfo info = rateLimitMap.computeIfAbsent(key, k -> new RateLimitInfo());
        
        // Clean up old entries
        if (currentTime - info.getFirstRequestTime() > timeWindow) {
            info.reset(currentTime);
        }
        
        // Check if limit exceeded
        if (info.getCount().get() >= maxRequests) {
            return false;
        }
        
        // Increment counter
        info.getCount().incrementAndGet();
        return true;
    }

    public void recordPostCreation(String userId) {
        String key = "posts_" + userId;
        recordActivity(key);
    }

    public void recordCommentCreation(String userId) {
        String key = "comments_" + userId;
        recordActivity(key);
    }

    public void recordLoginAttempt(String email) {
        String key = "login_" + email;
        recordActivity(key);
    }

    public void recordVote(String userId) {
        String key = "votes_" + userId;
        recordActivity(key);
    }

    private void recordActivity(String key) {
        long currentTime = System.currentTimeMillis();
        RateLimitInfo info = rateLimitMap.computeIfAbsent(key, k -> new RateLimitInfo());
        
        if (currentTime - info.getFirstRequestTime() > HOUR_IN_MILLIS) {
            info.reset(currentTime);
        }
        
        info.getCount().incrementAndGet();
    }

    public void clearRateLimit(String key) {
        rateLimitMap.remove(key);
    }

    public void clearAllRateLimits() {
        rateLimitMap.clear();
    }

    private static class RateLimitInfo {
        private final AtomicInteger count = new AtomicInteger(0);
        private long firstRequestTime = System.currentTimeMillis();

        public AtomicInteger getCount() {
            return count;
        }

        public long getFirstRequestTime() {
            return firstRequestTime;
        }

        public void reset(long currentTime) {
            count.set(0);
            firstRequestTime = currentTime;
        }
    }
}

package se.lexicon.todo_app.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBlacklistStorage {
    // replace in-memory storage with a more persistent solution such as redis or a database for production use if needed.

    private final ConcurrentHashMap<String, Instant> blacklistedTokens = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> userTokenVersions = new ConcurrentHashMap<>();

    @Value("${token.cleanup.interval}")
    private long cleanupInterval;


    public void blacklistToken(String token, String username, Instant expiryDate) {
        blacklistedTokens.put(token, expiryDate);
        // Increment user's token version when token is blacklisted
        userTokenVersions.compute(username, (key, oldVersion) ->
                oldVersion == null ? 1 : oldVersion + 1
        );
        removeExpiredTokens();
    }

    public boolean isBlacklisted(String token) {
        removeExpiredTokens();
        return blacklistedTokens.containsKey(token);
    }

    public int getUserTokenVersion(String username) {
        return userTokenVersions.getOrDefault(username, 0);
    }

    private void removeExpiredTokens() {
        Instant now = Instant.now();
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
    }

    @Scheduled(fixedDelayString = "${token.cleanup.interval}")
    public void cleanupExpiredTokens() {
        System.out.println("Starting scheduled cleanup of expired tokens...");
        int beforeSize = blacklistedTokens.size();

        removeExpiredTokens();

        int removedCount = beforeSize - blacklistedTokens.size();
        System.out.println("Cleanup completed. Removed " + removedCount + " expired tokens. Remaining tokens: " + blacklistedTokens.size());
    }


}
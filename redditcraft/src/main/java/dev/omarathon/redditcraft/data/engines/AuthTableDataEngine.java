package dev.omarathon.redditcraft.data.engines;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AuthTableDataEngine extends TableDataEngine {
    @Nullable
    LocalDateTime getExpiryTime(@NotNull UUID uuid);

    boolean existsToken(@NotNull UUID uuid);

    void addToken(@NotNull UUID uuid, @NotNull LocalDateTime expiry);
    void updateExpiryTime(@NotNull UUID uuid, @NotNull LocalDateTime newExpiry);
    void removeAllExpiredTokens();
    void removeToken(@NotNull UUID uuid);
    void removeAllTokens();
}

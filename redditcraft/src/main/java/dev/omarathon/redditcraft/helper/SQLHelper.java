package dev.omarathon.redditcraft.helper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

public class SQLHelper {
    public static Timestamp getNowTimestamp() {
        return Timestamp.valueOf(LocalDateTime.now());
    }

    public static String parseUuid(UUID uuid) {
        return uuid.toString();
    }

    public static long compareTwoTimeStamps(java.sql.Timestamp currentTime, java.sql.Timestamp oldTime)
    {
        long milliseconds1 = oldTime.getTime();
        long milliseconds2 = currentTime.getTime();

        long diff = milliseconds2 - milliseconds1;
        long diffSeconds = diff / 1000;
        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);
        long diffDays = diff / (24 * 60 * 60 * 1000);

        return diffMinutes;
    }
}

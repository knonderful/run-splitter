package runsplitter.speedrun;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A point in time.
 */
public class Instant {

    private static final int SECONDS_IN_MINUTE = 60;
    private static final int MINUTES_IN_HOUR = 60;
    private static final int HOURS_IN_DAY = 24;
    private static final int SECONDS_FACTOR = 1000;
    private static final int MINUTES_FACTOR = SECONDS_FACTOR * SECONDS_IN_MINUTE;
    private static final int HOURS_FACTOR = MINUTES_FACTOR * MINUTES_IN_HOUR;
    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile("^(\\d\\d?):(\\d\\d?):(\\d\\d?)\\.(\\d\\d\\d)$");
    private final long milliseconds;

    /**
     * Creates a new instance.
     *
     * @param milliseconds The time in milliseconds.
     */
    public Instant(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (int) (this.milliseconds ^ (this.milliseconds >>> 32));
        return hash;
    }

    private static String formatTimestamp(long millis) {
        long second = (millis / SECONDS_FACTOR) % SECONDS_IN_MINUTE;
        long minute = (millis / MINUTES_FACTOR) % MINUTES_IN_HOUR;
        long hour = (millis / HOURS_FACTOR) % HOURS_IN_DAY;
        return String.format("%02d:%02d:%02d.%03d", hour, minute, second, millis % SECONDS_FACTOR);
    }

    @Override
    public String toString() {
        return toTimestamp();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Instant other = (Instant) obj;
        return this.milliseconds == other.milliseconds;
    }

    /**
     * Retrieves the time in milliseconds.
     *
     * @return The time in milliseconds.
     */
    public long inMs() {
        return milliseconds;
    }

    /**
     * Creates a time stamp string.
     *
     * @return The time stamp.
     */
    public String toTimestamp() {
        return formatTimestamp(milliseconds);
    }

    /**
     * Creates an {@link Instant} from the provided time stamp string.
     *
     * @param timestamp The time stamp string
     * @return The {@link Instant}.
     */
    public static Instant fromTimestamp(String timestamp) {
        Matcher matcher = TIMESTAMP_PATTERN.matcher(timestamp);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format("Invalid time stamp: '%s'.", timestamp));
        }
        int hours = Integer.parseInt(matcher.group(1));
        int minutes = Integer.parseInt(matcher.group(2));
        int seconds = Integer.parseInt(matcher.group(3));
        int milliseconds = Integer.parseInt(matcher.group(4));

        return new Instant(
                ((long) milliseconds)
                + SECONDS_FACTOR * seconds
                + MINUTES_FACTOR * minutes
                + HOURS_FACTOR * hours);
    }
}

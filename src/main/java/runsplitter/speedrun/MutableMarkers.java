package runsplitter.speedrun;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * A mutable {@link RunMarkers} implementation.
 */
public class MutableMarkers implements RunMarkers {

    private final LinkedList<Instant> splits = new LinkedList<>();

    /**
     * Adds a split time.
     *
     * @param instant The (absolute) time.
     */
    public void addSplit(Instant instant) {
        splits.add(instant);
    }

    /**
     * Removes the last split time.
     *
     * @return The split time that was removed or {@code null} if no split time was found.
     */
    public Instant removeLastSplit() {
        if (splits.isEmpty()) {
            return null;
        }
        return splits.removeLast();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.splits);
        return hash;
    }

    @Override
    public String toString() {
        return "MutableSpeedrun{" + "splits=" + splits + '}';
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
        final MutableMarkers other = (MutableMarkers) obj;
        return Objects.equals(this.splits, other.splits);
    }

    @Override
    public List<Instant> getSplits() {
        return Collections.unmodifiableList(splits);
    }

    @Override
    public Instant getFinalSplit() {
        if (splits.isEmpty()) {
            return null;
        }
        return splits.peekLast();
    }

}

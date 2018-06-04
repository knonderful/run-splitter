package runsplitter.speedrun;

import java.util.Objects;

/**
 * A mutable {@link Speedrun} implementation.
 */
public class MutableSpeedrun implements Speedrun {

    private final String sourceName;
    private final MutableMarkers markers;
    private Instant start;

    /**
     * Creates a new instance.
     *
     * @param sourceName The name of the source.
     */
    public MutableSpeedrun(String sourceName) {
        this(sourceName, new Instant(0), new MutableMarkers());
    }

    /**
     * Creates a new instance.
     *
     * @param sourceName The name of the source.
     * @param start      The time in the source file where the speed run starts.
     * @param markers    The {@link MutableMarkers}.
     */
    public MutableSpeedrun(String sourceName, Instant start, MutableMarkers markers) {
        this.sourceName = sourceName;
        this.start = start;
        this.markers = markers;
    }

    @Override
    public String getSourceName() {
        return sourceName;
    }

    @Override
    public String toString() {
        return "MutableAnalysis{" + "sourceName=" + sourceName + ", markers=" + markers + ", start=" + start + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.sourceName);
        hash = 83 * hash + Objects.hashCode(this.markers);
        hash = 83 * hash + Objects.hashCode(this.start);
        return hash;
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
        final MutableSpeedrun other = (MutableSpeedrun) obj;
        if (!Objects.equals(this.sourceName, other.sourceName)) {
            return false;
        }
        if (!Objects.equals(this.markers, other.markers)) {
            return false;
        }
        return Objects.equals(this.start, other.start);
    }

    /**
     * Sets the time in the source file where the speed run starts.
     *
     * @param start The time in the source file where the speed run starts.
     */
    public void setStart(Instant start) {
        this.start = start;
    }

    @Override
    public Instant getStart() {
        return start;
    }

    @Override
    public MutableMarkers getMarkers() {
        return markers;
    }
}

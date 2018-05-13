package runsplitter.speedrun;

import java.util.Objects;

/**
 * A mutable {@link Analysis} implementation.
 */
public class MutableAnalysis implements Analysis {

    private final String sourceName;
    private final MutableSpeedrun speedrun;
    private Instant start;

    /**
     * Creates a new instance.
     *
     * @param sourceName The name of the source.
     */
    public MutableAnalysis(String sourceName) {
        this(sourceName, new Instant(0), new MutableSpeedrun());
    }

    /**
     * Creates a new instance.
     *
     * @param sourceName The name of the source.
     * @param start      The time in the source file where the speed run starts.
     * @param speedrun   The {@link Speedrun}.
     */
    public MutableAnalysis(String sourceName, Instant start, MutableSpeedrun speedrun) {
        this.sourceName = sourceName;
        this.start = start;
        this.speedrun = speedrun;
    }

    @Override
    public String getSourceName() {
        return sourceName;
    }

    @Override
    public String toString() {
        return "MutableAnalysis{" + "sourceName=" + sourceName + ", speedrun=" + speedrun + ", start=" + start + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.sourceName);
        hash = 83 * hash + Objects.hashCode(this.speedrun);
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
        final MutableAnalysis other = (MutableAnalysis) obj;
        if (!Objects.equals(this.sourceName, other.sourceName)) {
            return false;
        }
        if (!Objects.equals(this.speedrun, other.speedrun)) {
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
    public MutableSpeedrun getSpeedrun() {
        return speedrun;
    }
}

package runsplitter.application;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import runsplitter.VideoAnalyzer;
import runsplitter.speedrun.MutableSpeedrun;
import runsplitter.speedrun.Speedrun;

/**
 * A speed run category.
 */
public class Category {

    private String name;
    private VideoAnalyzer videoAnalyzer;
    private final List<SplitDescriptor> splitDescriptors = new LinkedList<>();
    private final List<MutableSpeedrun> speedruns = new LinkedList<>();

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public VideoAnalyzer getVideoAnalyzer() {
        return videoAnalyzer;
    }

    public void setVideoAnalyzer(VideoAnalyzer videoAnalyzer) {
        this.videoAnalyzer = videoAnalyzer;
    }

    public List<SplitDescriptor> getSplitDescriptors() {
        return Collections.unmodifiableList(splitDescriptors);
    }

    public List<SplitDescriptor> getSplitDescriptorsModifiable() {
        return splitDescriptors;
    }

    public List<Speedrun> getSpeedruns() {
        return Collections.unmodifiableList(speedruns);
    }

    public List<MutableSpeedrun> getSpeedrunsModifiable() {
        return speedruns;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.name);
        hash = 79 * hash + Objects.hashCode(this.videoAnalyzer);
        hash = 79 * hash + Objects.hashCode(this.splitDescriptors);
        hash = 79 * hash + Objects.hashCode(this.speedruns);
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
        final Category other = (Category) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.videoAnalyzer, other.videoAnalyzer)) {
            return false;
        }
        if (!Objects.equals(this.splitDescriptors, other.splitDescriptors)) {
            return false;
        }
        return Objects.equals(this.speedruns, other.speedruns);
    }
}

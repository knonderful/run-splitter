package runsplitter.application;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import runsplitter.VideoAnalyzer;

/**
 * A speed run category.
 */
public class Category {

    private String name;
    private VideoAnalyzer videoAnalyzer;
    private final List<SplitDescriptor> splitDescriptors = new LinkedList<>();

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
}

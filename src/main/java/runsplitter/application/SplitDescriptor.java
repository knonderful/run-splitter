package runsplitter.application;

/**
 * A descriptor for a single split point in a run.
 */
public class SplitDescriptor {

    private String name;
    private String description;

    public SplitDescriptor() {
    }

    public SplitDescriptor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

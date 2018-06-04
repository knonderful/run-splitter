package runsplitter.application;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import runsplitter.VideoAnalyzer;

/**
 * A game.
 */
public class Game {

    private String name;
    private VideoAnalyzer defaultVideoAnalyzer;
    private final List<Category> categories = new LinkedList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Category> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    public List<Category> getCategoriesModifiable() {
        return categories;
    }

    public VideoAnalyzer getDefaultVideoAnalyzer() {
        return defaultVideoAnalyzer;
    }

    public void setDefaultVideoAnalyzer(VideoAnalyzer defaultVideoAnalyzer) {
        this.defaultVideoAnalyzer = defaultVideoAnalyzer;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + Objects.hashCode(this.defaultVideoAnalyzer);
        hash = 29 * hash + Objects.hashCode(this.categories);
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
        final Game other = (Game) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.defaultVideoAnalyzer, other.defaultVideoAnalyzer)) {
            return false;
        }
        return Objects.equals(this.categories, other.categories);
    }

}

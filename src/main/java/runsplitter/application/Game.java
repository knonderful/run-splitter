package runsplitter.application;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A game.
 */
public class Game {

    private String name;
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
}

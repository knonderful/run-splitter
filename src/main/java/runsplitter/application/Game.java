package runsplitter.application;

import java.util.LinkedList;
import java.util.List;
import runsplitter.common.DuplicateElementException;
import runsplitter.common.UnknownElementException;

/**
 *
 */
public class Game {

    private String name;
    private final MovableList<Category> categories = new MovableList<Category>(new LinkedList<>(), Category::getName) {
        @Override
        protected void throwUnknownElementException(Category category) throws UnknownElementException {
            throw new UnknownElementException(String.format("Game %s does not contain category %s.", name, category.getName()));
        }

        @Override
        protected void throwDuplicateElementException(Category category) throws DuplicateElementException {
            throw new DuplicateElementException(String.format("Game %s already contains category %s.", name, category.getName()));
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Category> getCategories() {
        return categories.getList();
    }

    public void add(Category element) throws DuplicateElementException {
        categories.add(element);
    }

    public void remove(Category element) throws UnknownElementException {
        categories.remove(element);
    }

    public void clear() {
        categories.clear();
    }

    public void moveUp(Category element) throws UnknownElementException {
        categories.moveUp(element);
    }

    public void moveDown(Category category) throws UnknownElementException {
        categories.moveDown(category);
    }
}

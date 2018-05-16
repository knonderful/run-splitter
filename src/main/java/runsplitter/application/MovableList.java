package runsplitter.application;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import runsplitter.common.DuplicateElementException;
import runsplitter.common.UnknownElementException;

/**
 *
 * @param <T>
 */
public abstract class MovableList<T> {

    private final List<T> elements;
    private final Function<T, ?> keyGetter;

    protected abstract void throwUnknownElementException(T category) throws UnknownElementException;

    protected abstract void throwDuplicateElementException(T category) throws DuplicateElementException;

    public MovableList(List<T> categories, Function<T, ?> keyGetter) {
        this.elements = categories;
        this.keyGetter = keyGetter;
    }

    public List<T> getList() {
        return Collections.unmodifiableList(elements);
    }

    public void add(T element) throws DuplicateElementException {
        if (elements.contains(element)) {
            throwDuplicateElementException(element);
        }

        Object keyCandidate = keyGetter.apply(element);
        boolean hasElementWithSameKey = elements.stream()
                .map(keyGetter)
                .anyMatch(keyExisting -> Objects.equals(keyCandidate, keyExisting));

        if (hasElementWithSameKey) {
            throwDuplicateElementException(element);
        }

        elements.add(element);
    }

    public void remove(T element) throws UnknownElementException {
        if (!elements.contains(element)) {
            throwUnknownElementException(element);
        }

        elements.remove(element);
    }

    public void clear() {
        elements.clear();
    }

    public void moveUp(T element) throws UnknownElementException {
        int index = elements.indexOf(element);
        if (index < 0) {
            throwUnknownElementException(element);
        }
        if (index < 1) {
            return;
        }
        elements.remove(index);
        elements.add(index - 1, element);
    }

    public void moveDown(T category) throws UnknownElementException {
        int index = elements.indexOf(category);
        // -1 means that the element is not in the list
        if (index < 0) {
            throwUnknownElementException(category);
        }
        if (index >= elements.size()) {
            return;
        }
        elements.remove(index);
        elements.add(index + 1, category);
    }
}

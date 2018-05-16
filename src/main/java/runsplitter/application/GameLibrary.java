package runsplitter.application;

import java.util.LinkedList;
import java.util.List;
import runsplitter.common.DuplicateElementException;
import runsplitter.common.UnknownElementException;

/**
 *
 */
public class GameLibrary {

    private final MovableList<Game> games = new MovableList<Game>(new LinkedList<>(), Game::getName) {
        @Override
        protected void throwUnknownElementException(Game game) throws UnknownElementException {
            throw new UnknownElementException(String.format("Library does not contain %s.", game.getName()));
        }

        @Override
        protected void throwDuplicateElementException(Game game) throws DuplicateElementException {
            throw new DuplicateElementException(String.format("Library already contains game %s.", game.getName()));
        }
    };

    public List<Game> getGames() {
        return games.getList();
    }

    public void add(Game element) throws DuplicateElementException {
        games.add(element);
    }

    public void remove(Game element) throws UnknownElementException {
        games.remove(element);
    }

    public void clear() {
        games.clear();
    }

    public void moveUp(Game element) throws UnknownElementException {
        games.moveUp(element);
    }

    public void moveDown(Game category) throws UnknownElementException {
        games.moveDown(category);
    }

}

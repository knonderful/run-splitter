package runsplitter.application;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class GameLibrary {

    private final List<Game> games = new LinkedList<>();

    public List<Game> getGames() {
        return Collections.unmodifiableList(games);
    }

    public List<Game> getGamesModifiable() {
        return games;
    }
}

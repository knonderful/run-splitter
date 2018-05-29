package runsplitter.application;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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

    @Override
    public int hashCode() {
        return 79 * 5 + Objects.hashCode(this.games);
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
        final GameLibrary other = (GameLibrary) obj;
        return Objects.equals(this.games, other.games);
    }
}

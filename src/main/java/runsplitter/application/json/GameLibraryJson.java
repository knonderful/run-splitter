package runsplitter.application.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Reader;
import runsplitter.application.GameLibrary;

/**
 * JSON serialization for {@link GameLibrary}.
 */
public class GameLibraryJson {

    public static void toJson(GameLibrary settings, Appendable appendable) {
        createGson().toJson(settings, appendable);
    }

    public static GameLibrary fromJson(Reader reader) {
        return createGson().fromJson(reader, GameLibrary.class);
    }

    private static Gson createGson() {
        GsonBuilder builder = new GsonBuilder()
                .setPrettyPrinting();
        Gson gson = builder.create();
        return gson;
    }
}

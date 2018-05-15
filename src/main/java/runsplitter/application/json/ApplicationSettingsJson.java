package runsplitter.application.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import runsplitter.application.ApplicationSettings;

/**
 * JSON serialization for {@link ApplicationSettings}.
 */
public class ApplicationSettingsJson {

    public static void toJson(ApplicationSettings settings, Appendable appendable) {
        createGson().toJson(settings, appendable);
    }

    public static ApplicationSettings fromJson(Reader reader) {
        return createGson().fromJson(reader, ApplicationSettings.class);
    }

    private static Gson createGson() {
        GsonBuilder builder = new GsonBuilder()
                .registerTypeAdapter(Path.class, new PathAdapter())
                .setPrettyPrinting();
        Gson gson = builder.create();
        return gson;
    }

    private static class PathAdapter extends TypeAdapter<Path> {

        @Override
        public void write(JsonWriter writer, Path value) throws IOException {
            writer.value(value.toString());
        }

        @Override
        public Path read(JsonReader reader) throws IOException {
            return Paths.get(reader.nextString());
        }
    }
}

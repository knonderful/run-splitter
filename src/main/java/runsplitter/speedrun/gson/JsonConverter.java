package runsplitter.speedrun.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import runsplitter.speedrun.Instant;
import runsplitter.speedrun.Analysis;

/**
 *
 */
public class JsonConverter {

    public static void toJson(Collection<Analysis> analyses, Appendable appendable) {
        createGson().toJson(AnalysesGson.toGson(analyses), appendable);
    }

    public static Collection<Analysis> fromJson(Reader reader) {
        AnalysesGson set = createGson().fromJson(reader, AnalysesGson.class);
        return set.fromGson();
    }

    private static Gson createGson() {
        GsonBuilder builder = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .setPrettyPrinting();
        Gson gson = builder.create();
        return gson;
    }

    private static class InstantAdapter extends TypeAdapter<Instant> {

        @Override
        public void write(JsonWriter writer, Instant t) throws IOException {
            writer.value(t.toTimestamp());
        }

        @Override
        public Instant read(JsonReader reader) throws IOException {
            return Instant.fromTimestamp(reader.nextString());
        }
    }
}

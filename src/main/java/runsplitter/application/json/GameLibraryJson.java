package runsplitter.application.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import runsplitter.VideoAnalyzer;
import runsplitter.application.GameLibrary;

/**
 * JSON serialization for {@link GameLibrary}.
 */
public class GameLibraryJson {

    private static final Logger LOG = Logger.getLogger(GameLibraryJson.class.getName());

    private final Gson gson;

    public GameLibraryJson(Supplier<Collection<VideoAnalyzer>> videoAnalyzersSupplier) {
        GsonBuilder builder = new GsonBuilder()
                .registerTypeAdapter(VideoAnalyzer.class, new VideoAnalyzerAdapter(videoAnalyzersSupplier))
                .setPrettyPrinting();
        this.gson = builder.create();
    }
    
    public void toJson(GameLibrary library, Appendable appendable) {
        gson.toJson(library, appendable);
    }

    public GameLibrary fromJson(Reader reader) {
        return gson.fromJson(reader, GameLibrary.class);
    }

    private static class VideoAnalyzerAdapter extends TypeAdapter<VideoAnalyzer> {

        private final Supplier<Collection<VideoAnalyzer>> videoAnalyzersSupplier;

        VideoAnalyzerAdapter(Supplier<Collection<VideoAnalyzer>> videoAnalyzersSupplier) {
            this.videoAnalyzersSupplier = videoAnalyzersSupplier;
        }
        
        @Override
        public void write(JsonWriter writer, VideoAnalyzer analyzer) throws IOException {
            if (analyzer == null) {
                writer.nullValue();
                return;
            }
            writer.value(analyzer.getIdentifier());
        }

        @Override
        public VideoAnalyzer read(JsonReader in) throws IOException {
            String identifier = in.nextString();
            return videoAnalyzersSupplier.get().stream()
                    .filter(analyzer -> analyzer.getIdentifier().equals(identifier))
                    .findFirst()
                    .orElseGet(() -> {
                        LOG.log(Level.WARNING, String.format("Could not find %s with identifier '%s'.", VideoAnalyzer.class.getSimpleName(), identifier));
                        return null;
                    });
        }
    }
}

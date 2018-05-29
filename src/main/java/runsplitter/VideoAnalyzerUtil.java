package runsplitter;

import java.util.Collection;
import java.util.Objects;

/**
 *
 */
public class VideoAnalyzerUtil {

    private VideoAnalyzerUtil() {
    }

    public static boolean areEqual(VideoAnalyzer lhs, VideoAnalyzer rhs) {
        if (lhs == null) {
            return rhs == null;
        }

        if (rhs == null) {
            return false;
        }

        return Objects.equals(lhs.getIdentifier(), rhs.getIdentifier());
    }
    
    public static int createHashCode(VideoAnalyzer analyzer) {
        return 47 * 3 + Objects.hashCode(analyzer.getName());
    }
    
    public VideoAnalyzer find(Collection<VideoAnalyzer> analyzers, String identifier) {
        return analyzers.stream()
                .filter(analyzer -> analyzer.getIdentifier().equals(analyzer))
                .findFirst()
                .orElse(null);
    }
}

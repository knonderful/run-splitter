package runsplitter.yi;

import runsplitter.VideoFeed;
import runsplitter.VideoFeedHandler;

/**
 * The {@link VideoFeedHandler} for the Yoshi's Island clean run.
 */
public class YoshisIslandCleanFeedHandler implements VideoFeedHandler {

    /**
     * The display area where the game is rendered.
     */
    private static final Rectangle DISPLAY_RECTANGLE = new Rectangle(155, 0, 1129, 719);

    private final WhiteScreenPredicate whiteScreenPredicate = new WhiteScreenPredicate(DISPLAY_RECTANGLE);

    @Override
    public boolean handle(VideoFeed feed) {
        // Scan blocks until we find the start of the run
        return !feed.stream()
                // Take only the first block from the feed, since we have already scanned the older ones
                .findFirst()
                .flatMap(block -> {
                    // Find the first frame from the block that matches the predicate
                    return block.stream()
                            .filter(whiteScreenPredicate)
                            .findFirst();
                })
                .isPresent();
    }

}

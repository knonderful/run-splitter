package runsplitter.application;

/**
 * The GUI theme.
 */
public enum GuiTheme {
    DEFAULT("Default"),
    COMPACT("Compact");

    private final String internalName;

    private GuiTheme(String internalName) {
        this.internalName = internalName;
    }

    /**
     * Retrieves the name.
     *
     * @return The name.
     */
    public String getName() {
        return internalName;
    }

    /**
     * Retrieves the {@link GuiTheme} for the provided internal name.
     *
     * @param internalName The internal name.
     * @return The {@link GuiTheme}.
     */
    public static GuiTheme fromName(String internalName) {
        for (GuiTheme theme : values()) {
            if (theme.getName().equals(internalName)) {
                return theme;
            }
        }

        throw new IllegalArgumentException(String.format("Invalid internal name: %s.", internalName));
    }
}

package faang.school.projectservice.utils.image;

public record ResizeOptions(boolean requiresResizing, Integer maxWidth, Integer maxHeight) {
    public ResizeOptions {
        if (requiresResizing && (maxWidth == null || maxHeight == null)) {
            throw new IllegalArgumentException("When flag is true, both integers must be non-null");
        }
    }
}

package de.agiehl.bgg.model.common;

/**
 * Wishlist priority levels supported by the BGG collection endpoint.
 *
 * <p>The BGG API accepts a numeric value from 1 (highest priority) to 5
 * (lowest priority).
 */
public enum WishlistPriority {

    /** Must have. */
    MUST_HAVE(1),
    /** Love to have. */
    LOVE_TO_HAVE(2),
    /** Like to have. */
    LIKE_TO_HAVE(3),
    /** Thinking about it. */
    THINKING_ABOUT_IT(4),
    /** Don't buy this. */
    DONT_BUY(5);

    private final int apiValue;

    WishlistPriority(int apiValue) {
        this.apiValue = apiValue;
    }

    /**
     * @return the numeric value accepted by the BGG XML API
     */
    public int getApiValue() {
        return apiValue;
    }

    @Override
    public String toString() {
        return String.valueOf(apiValue);
    }
}

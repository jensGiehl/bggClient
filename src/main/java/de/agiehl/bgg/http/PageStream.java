package de.agiehl.bgg.http;

import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Builds a sequential, lazy {@link Stream} over a paginated BGG endpoint.
 *
 * <p>The factory is endpoint-agnostic: callers supply a function that fetches
 * page {@code N} and a predicate that decides whether the returned page still
 * contains data. The stream pulls pages on demand — partial consumers
 * ({@code limit}, {@code findFirst}, {@code takeWhile}) only trigger as many
 * page fetches as required.
 *
 * <p>Pagination terminates as soon as {@code hasContent} returns {@code false}
 * for a fetched page; that page is not emitted.
 *
 * <p>Each page fetch goes through {@link HttpExecutor}, which already handles
 * BGG's rate-limit retries (429/503) and the {@code 202 Accepted} async path.
 * Streams are not parallel-safe — running them in parallel would multiply rate
 * limit hits and is therefore discouraged.
 */
public final class PageStream {

    private static final Logger LOGGER = Logger.getLogger(PageStream.class.getName());

    private PageStream() {
    }

    /**
     * Creates a lazy {@code Stream<TResponse>} that yields one element per
     * fetched page, starting at {@code firstPage}.
     *
     * @param firstPage  page index of the first fetch (1-based for all BGG endpoints)
     * @param fetch      callback that fetches the given page
     * @param hasContent predicate deciding whether the fetched page still
     *                   contains data; when it returns {@code false} the stream
     *                   terminates and the page is dropped
     * @param <TResponse> the response type returned by the endpoint
     * @return a sequential, ordered stream of pages
     */
    public static <TResponse> Stream<TResponse> ofPages(
            int firstPage,
            IntFunction<TResponse> fetch,
            Predicate<TResponse> hasContent) {

        Objects.requireNonNull(fetch, "fetch");
        Objects.requireNonNull(hasContent, "hasContent");

        Spliterator<TResponse> spliterator = new PageSpliterator<>(firstPage, fetch, hasContent);
        return StreamSupport.stream(spliterator, false);
    }

    private static final class PageSpliterator<TResponse>
            extends Spliterators.AbstractSpliterator<TResponse> {

        private final IntFunction<TResponse> fetch;
        private final Predicate<TResponse> hasContent;
        private int nextPage;
        private boolean exhausted;

        private PageSpliterator(int firstPage,
                                IntFunction<TResponse> fetch,
                                Predicate<TResponse> hasContent) {
            super(Long.MAX_VALUE, Spliterator.ORDERED | Spliterator.NONNULL);
            this.nextPage = firstPage;
            this.fetch = fetch;
            this.hasContent = hasContent;
        }

        @Override
        public boolean tryAdvance(Consumer<? super TResponse> action) {
            if (exhausted) {
                return false;
            }
            int page = nextPage;
            LOGGER.log(Level.FINE, "PageStream fetching page {0}", page);
            TResponse response = fetch.apply(page);
            if (response == null || !hasContent.test(response)) {
                LOGGER.log(Level.FINE, "PageStream terminated at page {0} (no more content)", page);
                exhausted = true;
                return false;
            }
            action.accept(response);
            nextPage = page + 1;
            return true;
        }
    }
}

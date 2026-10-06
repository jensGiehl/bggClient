package de.agiehl.bgg.http;

import com.sun.net.httpserver.HttpServer;
import de.agiehl.bgg.config.BggClientConfig;
import de.agiehl.bgg.exception.BggParseException;
import de.agiehl.bgg.model.thing.ThingResponse;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HttpExecutorTest {

    @Test
    void wrapsMalformedXmlInBggParseException() throws Exception {
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/thing", exchange -> {
            byte[] body = "<items><item></items>".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/xml; charset=UTF-8");
            exchange.sendResponseHeaders(200, body.length);
            try (var output = exchange.getResponseBody()) {
                output.write(body);
            }
        });
        server.start();
        try {
            var config = BggClientConfig.builder()
                    .apiKey("test-token")
                    .baseUri(URI.create("http://127.0.0.1:" + server.getAddress().getPort()))
                    .requestTimeout(Duration.ofSeconds(5))
                    .build();
            var executor = new HttpExecutor(config);

            var exception = assertThrows(BggParseException.class,
                    () -> executor.get("/thing", QueryParameters.create(), ThingResponse.class));

            assertInstanceOf(JacksonException.class, exception.getCause());
        } finally {
            server.stop(0);
        }
    }
}

package io.helidon.benchmark.services;

import java.nio.charset.StandardCharsets;

import io.helidon.common.http.Http;
import io.helidon.common.http.MediaType;
import io.helidon.webserver.Routing;
import io.helidon.webserver.Service;

public class PlainTextService implements Service {

    private static final byte[] MESSAGE = "Hello, World!".getBytes(StandardCharsets.UTF_8);
    private static final String MEDIA_TYPE = MediaType.TEXT_PLAIN.toString();

    @Override
    public void update(Routing.Rules rules) {
        rules.get("/plaintext", (req, res) -> res.addHeader(Http.Header.CONTENT_TYPE, MEDIA_TYPE).send(MESSAGE));
    }
}

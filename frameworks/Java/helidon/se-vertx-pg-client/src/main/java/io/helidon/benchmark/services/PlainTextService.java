package io.helidon.benchmark.services;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;

import java.nio.charset.StandardCharsets;

import io.helidon.webserver.Routing;
import io.helidon.webserver.Service;

public class PlainTextService implements Service {

    private static final byte[] MSG = "Hello, World!".getBytes(StandardCharsets.UTF_8);

    @Override
    public void update(Routing.Rules rules) {
        rules.get("/plaintext",
                (req, res) -> {
                    res.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
                    res.send(MSG);
                });
    }
}

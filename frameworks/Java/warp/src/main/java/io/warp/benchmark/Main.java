package io.warp.benchmark;

import io.warp.http.HttpMethod;
import io.warp.http.headers.HeaderName;
import io.warp.http.headers.HttpHeader;
import io.warp.server.Routing;
import io.warp.server.Server;
import io.warp.server.ServerRequest;
import io.warp.server.ServerResponse;

import java.nio.charset.StandardCharsets;

public class Main {

    private static final byte[] MSG = "Hello, World!".getBytes(StandardCharsets.UTF_8);
    private static final HttpHeader SERVER_HEADER = HttpHeader.create(HeaderName.create("server"), "Warp");
    private static final HttpHeader CONTENT_TYPE_HEADER = HttpHeader.create(HttpHeader.HeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

    public static void main(String[] args) {
        Server.builder()
                .port(8080)
                .host("tfb-server")
                .receiveBufferSize(4096)
                .routing(Routing.builder()
                        .route(HttpMethod.Method.GET, "/plaintext", Main::plainText)
                        .route(HttpMethod.Method.GET, "/json", Main::notImplemented)
                        .route(HttpMethod.Method.GET, "/fortunes", Main::notImplemented)
                        .route(HttpMethod.Method.GET, "/db", Main::notImplemented)
                        .route(HttpMethod.Method.GET, "/queries", Main::notImplemented)
                        .route(HttpMethod.Method.GET, "/updates", Main::notImplemented)
                        .build()
                )
                .build()
                .start();
    }

    public static void plainText(ServerRequest req, ServerResponse res) {
        res.header(SERVER_HEADER)
                .header(CONTENT_TYPE_HEADER)
                .send(MSG);
    }

    public static void notImplemented(ServerRequest req, ServerResponse res) {
        res.header(SERVER_HEADER)
                .header(CONTENT_TYPE_HEADER)
                .send("NOT IMPLEMENTED");
    }
}

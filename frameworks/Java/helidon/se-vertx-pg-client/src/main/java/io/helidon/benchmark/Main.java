/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.helidon.benchmark;

import java.io.IOException;
import java.util.logging.LogManager;

import io.helidon.benchmark.models.DbRepository;
import io.helidon.benchmark.models.JdbcRepository;
import io.helidon.benchmark.services.DbService;
import io.helidon.benchmark.services.FortuneService;
import io.helidon.benchmark.services.JsonService;
import io.helidon.benchmark.services.PlainTextService;
import io.helidon.media.jsonp.JsonpSupport;
import io.helidon.webserver.BareResponse;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.AsciiString;
import io.opentracing.noop.NoopTracerFactory;

/**
 * Simple Hello World rest application.
 */
public final class Main {

    /**
     * Cannot be instantiated.
     */
    private Main() {
    }

    private static DbRepository getRepository() {
        return new JdbcRepository();
    }

    private static Mustache getTemplate() {
        MustacheFactory mf = new DefaultMustacheFactory();
        return mf.compile("fortunes.mustache");
    }

    private static final AsciiString SERVER = new AsciiString("Server");
    private static final AsciiString SERVER_VAL = new AsciiString("Helidon");

    private static Routing createRouting() {
        DbRepository repository = getRepository();
        return Routing.builder()
                .any((req, res) -> {
                    res.headers().add(SERVER, SERVER_VAL);
                    req.next();
                })
                .register(new JsonService())
                .register(new PlainTextService())
                .register(new DbService(repository))
                .register(new FortuneService(repository, getTemplate()))
                .build();
    }

    /**
     * Application main entry point.
     *
     * @param args command line arguments.
     * @throws IOException if there are problems reading logging properties
     */
    public static void main(final String[] args) throws IOException {
        startServer();
    }

    /**
     * Start the server.
     *
     * @return the created {@link WebServer} instance
     * @throws IOException if there are problems reading logging properties
     */
    static WebServer startServer() throws IOException {

        // load logging configuration
        LogManager.getLogManager().readConfiguration(
                Main.class.getResourceAsStream("/logging.properties"));

        // Build server with JSONP support
        WebServer server = WebServer.builder(createRouting())
                .port(8080)
//                .addMediaSupport(JsonpSupport.create())
//                .transport(new io.helidon.webserver.transport.netty.epoll.EPollTransport())
                .build();

        // Start the server and print some info.
        server.start().thenAccept(ws -> {
            System.out.println("WEB server is up! http://localhost:" + ws.port());
        });

        // Server threads are not demon. NO need to block. Just react.
        server.whenShutdown().thenRun(()
                -> System.out.println("WEB server is DOWN. Good bye!"));

        return server;
    }
}

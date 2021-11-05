package io.helidon.benchmark.services;

import java.util.Collections;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonBuilderFactory;

import io.helidon.webserver.Routing;
import io.helidon.webserver.Service;

public class JsonService implements Service {

    private static final String ATTR_NAME = "message";
    private static final String ATTR_VALUE = "Hello, World!";

    private final JsonBuilderFactory jsonBuilderFactory;

     public JsonService() {
         this.jsonBuilderFactory = Json.createBuilderFactory(Collections.emptyMap());
     }

    @Override
    public void update(Routing.Rules rules) {
        rules.get("/json", (req, res) -> res.send(jsonBuilderFactory.createObjectBuilder(Map.of(ATTR_NAME, ATTR_VALUE)).build()));
    }
}

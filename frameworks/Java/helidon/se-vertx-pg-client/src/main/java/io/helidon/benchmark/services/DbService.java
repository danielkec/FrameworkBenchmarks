package io.helidon.benchmark.services;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import io.helidon.benchmark.models.DbRepository;
import io.helidon.benchmark.models.World;
import io.helidon.common.http.Parameters;
import io.helidon.common.reactive.Multi;
import io.helidon.common.reactive.Single;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;

public class DbService implements Service {

    private final DbRepository repository;
    private JsonBuilderFactory jsonBuilderFactory;

    public DbService(DbRepository repository) {
        this.repository = repository;

        this.jsonBuilderFactory = Json.createBuilderFactory(Collections.emptyMap());
    }

    @Override
    public void update(Routing.Rules rules) {
        rules.get("/db", this::db);
        rules.get("/queries", this::queries);
        rules.get("/updates", this::updates);
    }

    private void db(final ServerRequest request,
                    final ServerResponse response) {
        repository.getWorld(randomWorldNumber())
                .forSingle(world -> response.send(world.toJson()));
    }

    private void queries(final ServerRequest request,
                         final ServerResponse response) {
        parseQueryCount(request.queryParams());
        response.send(Multi.range(0, parseQueryCount(request.queryParams()))
                .flatMap(i -> repository.getWorld(randomWorldNumber()))
                .map(World::toJson), JsonObject.class);

//        Single<JsonObject>[] worlds = new Single[parseQueryCount(request.queryParams())];
//        Arrays.setAll(worlds, i -> repository.getWorld(randomWorldNumber()).thenApply(World::toJson));
//        marshall(worlds).subscribe(response::send);

    }

    private void updates(final ServerRequest request,
                         final ServerResponse response) {
        response.send(Multi.range(0, parseQueryCount(request.queryParams()))
                .flatMap(i -> repository.getWorld(randomWorldNumber()))
                .flatMap(world -> {
                    world.randomNumber = randomWorldNumber();
                    return repository.updateWorld(world);
                })
                .map(World::toJson), JsonObject.class);
    }


    private int randomWorldNumber() {
        return 1 + ThreadLocalRandom.current().nextInt(10000);
    }

    private int parseQueryCount(Parameters parameters) {
        Optional<String> textValue = parameters.first("queries");

        if (textValue.isEmpty()) {
            return 1;
        }
        int parsedValue;
        try {
            parsedValue = Integer.parseInt(textValue.get());
        } catch (NumberFormatException e) {
            return 1;
        }
        return Math.min(500, Math.max(1, parsedValue));
    }

    private Single<JsonArray> marshall(Single<JsonObject>[] worlds) {
        return Multi.concatArray(worlds)
                .reduce(jsonBuilderFactory::createArrayBuilder, JsonArrayBuilder::add)
                .map(JsonArrayBuilder::build)
                .onError(Throwable::printStackTrace);
    }
}

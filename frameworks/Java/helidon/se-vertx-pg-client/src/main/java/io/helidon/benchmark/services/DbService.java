package io.helidon.benchmark.services;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.util.AsciiString;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;

import io.helidon.benchmark.models.DbRepository;
import io.helidon.benchmark.models.JdbcRepository;
import io.helidon.benchmark.models.World;
import io.helidon.common.http.DataChunk;
import io.helidon.common.http.Headers;
import io.helidon.common.http.Http;
import io.helidon.common.http.Parameters;
import io.helidon.common.reactive.IoMulti;
import io.helidon.common.reactive.Multi;
import io.helidon.common.reactive.OutputStreamMulti;
import io.helidon.common.reactive.Single;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;

public class DbService implements Service {

    private final JdbcRepository repository;
    private final JsonWriterFactory jsonBuilderFactory;

    public DbService(DbRepository repository) {
        this.repository = (JdbcRepository) repository;
        this.jsonBuilderFactory = Json.createWriterFactory(Map.of());
    }

    @Override
    public void update(Routing.Rules rules) {
        rules.get("/db", this::db);
        rules.get("/queries", this::queries);
        rules.get("/updates", this::updates);
    }

//    private void db(final ServerRequest request,
//                    final ServerResponse response) {
//        response.headers().add(Http.Header.CONTENT_TYPE, "application/json");
//        repository.getWorld(randomWorldNumber())
//                .forSingle(world -> {
//                    var os = new ByteBuffOutputStream();
//                    JsonWriter writer = jsonBuilderFactory.createWriter(os);
//                    writer.write(world.toJson());
//                    writer.close();
//                    response.send(Single.just(os.toDataChunk()));
//                });
//    }

    private void db(final ServerRequest request,
                    final ServerResponse response) {
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        repository.client.get().preparedQuery("SELECT id, randomnumber FROM world WHERE id = $1")
                .execute(Tuple.of(randomWorldNumber()), rowSetAsyncResult -> {
                    Row r = rowSetAsyncResult.result().iterator().next();
                    var os = new ByteBuffOutputStream();
                    JsonWriter writer = jsonBuilderFactory.createWriter(os);
                    writer.write(new World(r.getInteger(0), r.getInteger(1)).toJson());
                    writer.close();
                    response.send(Single.just(os.toDataChunk()));
                });
    }

    static class ByteBuffOutputStream extends ByteArrayOutputStream{

        public ByteBuffOutputStream() {
            super(32);
        }

        public DataChunk toDataChunk() {
            return DataChunk.create(ByteBuffer.wrap(super.buf, 0, super.count));
        }
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
}

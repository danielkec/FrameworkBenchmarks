package io.helidon.benchmark.models;

import java.util.ArrayList;
import java.util.List;

import io.helidon.common.reactive.Single;
import io.helidon.config.Config;

import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

public class JdbcRepository implements DbRepository {
    private PgConnectOptions connectOptions = null;
    private  PoolOptions poolOptions = null;

    public ThreadLocal<SqlClient> client = ThreadLocal.withInitial(() -> PgPool.client(connectOptions, poolOptions));

    public JdbcRepository() {
        connectOptions = new PgConnectOptions()
                .setPort(5432)
                .setCachePreparedStatements(true)
                .setHost("tfb-database")
//                .setHost("localhost")
                .setDatabase("hello_world")
                .setUser("benchmarkdbuser")
                .setPassword("benchmarkdbpass");

        poolOptions = new PoolOptions()
                .setMaxSize(1);
//        client = (SqlClientInternal) PgPool.client(connectOptions, poolOptions);
    }

    @Override
    public Single<World> getWorld(int id) {
        return Single.create(client.get().preparedQuery("SELECT id, randomnumber FROM world WHERE id = $1")
                .execute(Tuple.of(id))
                .map(rows -> {
                    Row r = rows.iterator().next();
                    return new World(r.getInteger(0), r.getInteger(1));
                })
                .toCompletionStage());
    }

    @Override
    public Single<World> updateWorld(World world) {
        return Single.create(client.get().preparedQuery("UPDATE world SET randomnumber = $1 WHERE id = $2")
                .execute(Tuple.of(world.id, world.randomNumber))
                .toCompletionStage()
                .thenApply(rows -> world));
    }

    @Override
    public Single<List<Fortune>> getFortunes() {
        return Single.create(client.get().preparedQuery("SELECT id, message FROM fortune")
                .execute()
                .map(rows -> {
                    List<Fortune> fortunes = new ArrayList<>(rows.size() + 1);
                    for (Row r : rows) {
                        fortunes.add(new Fortune(r.getInteger(0), r.getString(1)));
                    }
                    return fortunes;
                })
                .toCompletionStage());
    }
}

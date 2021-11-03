package io.helidon.benchmark.models;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import io.helidon.common.configurable.ThreadPoolSupplier;
import io.helidon.common.reactive.Multi;
import io.helidon.common.reactive.Single;
import io.helidon.config.Config;
import io.helidon.dbclient.DbClient;
import io.helidon.dbclient.jdbc.JdbcDbClientProviderBuilder;

public class JdbcRepository implements DbRepository {
    private final ExecutorService exec;
    private final DbClient dbClient;

    public JdbcRepository(Config config) {
        this.exec = Executors.newCachedThreadPool();
        dbClient = JdbcDbClientProviderBuilder.create()
                .config(config.get("db"))
                .executorService(() -> exec)
                .build();
    }

    @Override
    public Single<World> getWorld(int id) {
        return dbClient.execute(ex ->
                                ex.createGet("SELECT id, randomnumber FROM world WHERE id = ?")
                                        .params(id)
                                        .execute()
                                        .flatMapOptional(dbRow -> dbRow.map(r -> new World(r.column(1).as(Integer.class), r.column(2).as(Integer.class))))
//                                        .flatMap(r -> Single.just(new World(r.column(1).as(Integer.class), r.column(2).as(Integer.class))),
//                                                2, false, 2)
                        );
    }

    @Override
    public Single<World> updateWorld(World world) {
        return dbClient.execute(ex -> ex
                        .createUpdate("UPDATE world SET randomnumber = ? WHERE id = ?")
                        .params(world.randomNumber, world.id)
                        .execute())
                .map(r -> world);
    }

    @Override
    public Multi<Fortune> getFortunes() {
        return dbClient.execute(ex -> ex.query("SELECT id, message FROM fortune ORDER BY message"))
                .map(r -> new Fortune(r.column(1).as(Integer.class), r.column(2).as(String.class)));
    }
}

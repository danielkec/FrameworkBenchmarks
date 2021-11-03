package io.helidon.benchmark.models;


import java.util.List;
import java.util.concurrent.CompletionStage;

import io.helidon.common.reactive.Single;

public interface DbRepository {

    Single<World> getWorld(int id);

    Single<World> updateWorld(World world);

    Single<List<Fortune>> getFortunes();
}
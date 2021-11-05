package io.helidon.benchmark.services;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;

import io.helidon.benchmark.models.DbRepository;
import io.helidon.benchmark.models.Fortune;
import io.helidon.benchmark.views.fortunes;
import io.helidon.common.http.MediaType;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;

import com.fizzed.rocker.runtime.ArrayOfByteArraysOutput;

public class FortuneService implements Service {

    private final DbRepository repository;

    public FortuneService(DbRepository repository) {
        this.repository = repository;
    }

    @Override
    public void update(Routing.Rules rules) {
        rules.get("/fortunes", this::fortunes);
    }

    private void fortunes(ServerRequest request, ServerResponse response) {
        response.headers().contentType(MediaType.TEXT_HTML.withCharset(StandardCharsets.UTF_8.name()));
        repository.getFortunes()
                .forSingle(fortuneList -> {
                    fortuneList.add(new Fortune(0, "Additional fortune added at request time."));
                    fortuneList.sort(Comparator.comparing(Fortune::getMessage));
                    response.headers().contentType(MediaType.TEXT_HTML.withCharset(StandardCharsets.UTF_8.name()));
                    response.send(fortunes.template(fortuneList)
                            .render(ArrayOfByteArraysOutput.FACTORY)
                            .toByteArray());
                });
    }
}

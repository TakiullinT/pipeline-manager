package org.dealership.pipelinemanager.infrastructure.impl;

import org.dealership.pipelinemanager.domain.Pipeline;
import org.dealership.pipelinemanager.infrastructure.PipelineRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPipelineRepository implements PipelineRepository {
    private final Map<UUID, Pipeline> store = new ConcurrentHashMap<>();

    @Override
    public Pipeline save(Pipeline pipeline) {
        store.put(pipeline.getId(), pipeline);
        return pipeline;
    }

    @Override
    public Optional<Pipeline> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }
}

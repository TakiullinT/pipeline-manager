package org.dealership.pipelinemanager.infrastructure;

import org.dealership.pipelinemanager.domain.Pipeline;

import java.util.Optional;
import java.util.UUID;

public interface PipelineRepository {
    Pipeline save(Pipeline pipeline);
    Optional<Pipeline> findById(UUID id);
}

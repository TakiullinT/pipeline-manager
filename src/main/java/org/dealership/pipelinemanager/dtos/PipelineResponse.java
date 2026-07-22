package org.dealership.pipelinemanager.dtos;

import org.dealership.pipelinemanager.domain.Pipeline;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record PipelineResponse(
        UUID id, Set<String> nodes,
        Map<String, Set<String>> edges
) {
    public static PipelineResponse fromDomain(Pipeline pipeline) {
        return new PipelineResponse(
                pipeline.getId(),
                pipeline.getNodes(),
                pipeline.getEdges()
        );
    }
}

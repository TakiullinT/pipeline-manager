package org.dealership.pipelinemanager.services;

import lombok.RequiredArgsConstructor;
import org.dealership.pipelinemanager.domain.Pipeline;
import org.dealership.pipelinemanager.exceptions.PipelineNotFoundException;
import org.dealership.pipelinemanager.infrastructure.PipelineRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PipelineService {
    private final PipelineRepository pipelineRepository;

    public Pipeline createPipeline() {
        Pipeline pipeline = new Pipeline();
        return pipelineRepository.save(pipeline);
    }

    public void addNode(UUID pipelineId, String nodeId) {
        Pipeline pipeline = getPipelineById(pipelineId);
        pipeline.addNode(nodeId);
        pipelineRepository.save(pipeline);
    }

    public void addEdge(UUID pipelineId, String from, String to) {
        Pipeline pipeline = getPipelineById(pipelineId);
        pipeline.addEdge(from, to);
        pipelineRepository.save(pipeline);
    }

    public Pipeline getPipeline(UUID pipelineId) {
        return getPipelineById(pipelineId);
    }

    public List<String> getExecutionOrder(UUID pipelineId) {
        Pipeline pipeline = getPipelineById(pipelineId);
        return pipeline.getExecutionOrder();
    }

    private Pipeline getPipelineById(UUID pipelineId) {
        return pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new PipelineNotFoundException("Pipeline with id " + pipelineId + " not found."));
    }
}

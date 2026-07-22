package org.dealership.pipelinemanager.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dealership.pipelinemanager.domain.Pipeline;
import org.dealership.pipelinemanager.dtos.AddEdgeRequest;
import org.dealership.pipelinemanager.dtos.AddNodeRequest;
import org.dealership.pipelinemanager.dtos.PipelineResponse;
import org.dealership.pipelinemanager.services.PipelineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pipelines")
@RequiredArgsConstructor
public class PipelineController {
    private final PipelineService pipelineService;

    @PostMapping
    public ResponseEntity<PipelineResponse> createPipeline() {
        Pipeline pipeline = pipelineService.createPipeline();
        return ResponseEntity.status(HttpStatus.CREATED).body(PipelineResponse.fromDomain(pipeline));
    }

    @PostMapping("/{pipelineId}/nodes")
    public ResponseEntity<Void> addNode(@PathVariable UUID pipelineId, @Valid @RequestBody AddNodeRequest request) {
        pipelineService.addNode(pipelineId, request.getNodeId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{pipelineId}/edges")
    public ResponseEntity<Void> addEdge(@PathVariable UUID pipelineId, @Valid @RequestBody AddEdgeRequest request) {
        pipelineService.addEdge(pipelineId, request.getFrom(), request.getTo());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{pipelineId}")
    public ResponseEntity<PipelineResponse> getPipeline(@PathVariable UUID pipelineId) {
        Pipeline pipeline = pipelineService.getPipeline(pipelineId);
        return ResponseEntity.ok(PipelineResponse.fromDomain(pipeline));
    }

    @GetMapping("/{pipelineId}/execution-order")
    public ResponseEntity<List<String>> getExecutionOrder(@PathVariable UUID pipelineId) {
        List<String> order = pipelineService.getExecutionOrder(pipelineId);
        return ResponseEntity.ok(order);
    }
}

package org.dealership.pipelinemanager;

import org.dealership.pipelinemanager.domain.Pipeline;
import org.dealership.pipelinemanager.infrastructure.impl.InMemoryPipelineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PipelineRepositoryTest {

    private InMemoryPipelineRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPipelineRepository();
    }

    @Test
    void shouldSaveAndFindPipelineById() {
        Pipeline pipeline = new Pipeline();

        Pipeline savedPipeline = repository.save(pipeline);
        Optional<Pipeline> foundPipeline = repository.findById(pipeline.getId());

        assertNotNull(savedPipeline);
        assertTrue(foundPipeline.isPresent(), "Pipeline should be found in the repository");
        assertEquals(pipeline.getId(), foundPipeline.get().getId(), "Found pipeline should have the same ID");
    }

    @Test
    void shouldReturnEmptyOptional_whenPipelineDoesNotExist() {
        Optional<Pipeline> foundPipeline = repository.findById(UUID.randomUUID());
        assertTrue(foundPipeline.isEmpty(), "Repository should return empty Optional for unknown ID");
    }

    @Test
    void shouldUpdateExistingPipeline_whenSavedMultipleTimes() {
        Pipeline pipeline = new Pipeline();
        repository.save(pipeline);
        pipeline.addNode("A");

        repository.save(pipeline);
        Optional<Pipeline> foundPipeline = repository.findById(pipeline.getId());

        assertTrue(foundPipeline.isPresent());
        assertEquals(1, foundPipeline.get().getNodes().size(), "Pipeline should reflect added nodes");
        assertTrue(foundPipeline.get().getNodes().contains("A"));
    }
}
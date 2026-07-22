package org.dealership.pipelinemanager;

import org.dealership.pipelinemanager.domain.Pipeline;
import org.dealership.pipelinemanager.exceptions.CycleDetectedException;
import org.dealership.pipelinemanager.exceptions.NodeNotFoundException;
import org.dealership.pipelinemanager.exceptions.SelfDependencyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PipelineTest {

    private Pipeline pipeline;

    @BeforeEach
    void setUp() {
        pipeline = new Pipeline();
    }

    @Test
    void shouldReturnCorrectExecutionOrder_whenGraphIsValid() {
        pipeline.addNode("input");
        pipeline.addNode("filter");
        pipeline.addNode("enrich");
        pipeline.addNode("output");

        pipeline.addEdge("input", "filter");
        pipeline.addEdge("input", "enrich");
        pipeline.addEdge("filter", "output");
        pipeline.addEdge("enrich", "output");

        List<String> order = pipeline.getExecutionOrder();

        assertEquals(4, order.size());

        assertEquals("input", order.get(0));
        assertEquals("output", order.get(3));

        assertTrue(order.containsAll(List.of("filter", "enrich")));
    }

    @Test
    void shouldThrowException_whenAddingEdgeWithNonExistentNode() {
        pipeline.addNode("A");
        assertThrows(NodeNotFoundException.class, () -> pipeline.addEdge("A", "B"));
    }

    @Test
    void shouldThrowException_whenAddingSelfDependency() {
        pipeline.addNode("A");
        assertThrows(SelfDependencyException.class, () -> pipeline.addEdge("A", "A"));
    }

    @Test
    void shouldThrowException_whenDirectCycleIsDetected() {
        pipeline.addNode("A");
        pipeline.addNode("B");

        pipeline.addEdge("A", "B");
        assertThrows(CycleDetectedException.class, () -> pipeline.addEdge("B", "A"));
    }

    @Test
    void shouldThrowException_whenTransitiveCycleIsDetected() {
        pipeline.addNode("A");
        pipeline.addNode("B");
        pipeline.addNode("C");

        pipeline.addEdge("A", "B");
        pipeline.addEdge("B", "C");
        assertThrows(CycleDetectedException.class, () -> pipeline.addEdge("C", "A"));
    }

    @Test
    void shouldHandleDisconnectedNodesProperly() {
        pipeline.addNode("A");
        pipeline.addNode("B");
        pipeline.addNode("C");

        pipeline.addEdge("A", "B");

        List<String> order = pipeline.getExecutionOrder();
        assertEquals(3, order.size());
        assertTrue(order.indexOf("A") < order.indexOf("B"));
        assertTrue(order.contains("C"));
    }

    @Test
    void shouldReturnEmptyList_whenPipelineIsEmpty() {
        List<String> order = pipeline.getExecutionOrder();
        assertTrue(order.isEmpty(), "Execution order of an empty pipeline should be empty");
    }

    @Test
    void shouldNotEraseEdges_whenAddingExistingNode() {
        pipeline.addNode("A");
        pipeline.addNode("B");
        pipeline.addEdge("A", "B");
        pipeline.addNode("A");

        Map<String, Set<String>> edges = pipeline.getEdges();
        assertEquals(1, edges.get("A").size());
        assertTrue(edges.get("A").contains("B"));
    }

    @Test
    void shouldNotDuplicateEdges_whenAddingExistingEdge() {
        pipeline.addNode("A");
        pipeline.addNode("B");

        pipeline.addEdge("A", "B");
        pipeline.addEdge("A", "B");
        assertEquals(1, pipeline.getEdges().get("A").size());
    }

    @Test
    void shouldProtectNodesFromExternalModification() {
        pipeline.addNode("A");

        Set<String> returnedNodes = pipeline.getNodes();

        assertThrows(UnsupportedOperationException.class, () -> returnedNodes.add("B"),
                "Expected UnsupportedOperationException when trying to modify returned nodes set");
    }

    @Test
    void shouldProtectEdgesFromExternalModification() {
        pipeline.addNode("A");
        pipeline.addNode("B");
        pipeline.addEdge("A", "B");

        Map<String, Set<String>> returnedEdges = pipeline.getEdges();
        assertThrows(UnsupportedOperationException.class, () -> returnedEdges.get("A").add("C"),
                "Expected UnsupportedOperationException when trying to modify internal edges set");
    }
}
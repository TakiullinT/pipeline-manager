package org.dealership.pipelinemanager.domain;

import lombok.Getter;
import org.dealership.pipelinemanager.exceptions.CycleDetectedException;
import org.dealership.pipelinemanager.exceptions.NodeNotFoundException;
import org.dealership.pipelinemanager.exceptions.SelfDependencyException;

import java.util.*;
import java.util.stream.Collectors;

public class Pipeline {
    @Getter
    private final UUID id;
    private final Set<String> nodes;
    private final Map<String, Set<String>> edges;

    public Pipeline () {
        this.id = UUID.randomUUID();
        this.nodes = new TreeSet<>();
        this.edges = new TreeMap<>();
    }

    public synchronized Set<String> getNodes() {
        return Set.copyOf(nodes);
    }

    public synchronized Map<String, Set<String>> getEdges() {
        Map<String, Set<String>> protectedEdges =  edges.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> Set.copyOf(entry.getValue())
                ));
        return Map.copyOf(protectedEdges);
    }

    public synchronized void addNode(String nodeId) {
        nodes.add(nodeId);
        edges.putIfAbsent(nodeId, new TreeSet<>());
    }

    public synchronized void addEdge(String from, String to) {
        if (!nodes.contains(from) || !nodes.contains(to)) {
            throw new NodeNotFoundException("Both nodes must exist in the pipeline.");
        }

        if (from.equals(to)) {
            throw new SelfDependencyException("A node cannot depend on itself.");
        }

        if (createCycle(from, to)) {
            throw new CycleDetectedException("Adding edge from '" + from + "' to '" + to + "' creates a cycle.");
        }

        edges.get(from).add(to);
    }

    public synchronized List<String> getExecutionOrder() {
        Map<String, Integer> inDegree = new HashMap<>();
        for (String node : nodes) {
            inDegree.putIfAbsent(node, 0);
        }

        for (Set<String> dependentNodes : edges.values()) {
            for (String dependentNode : dependentNodes) {
                inDegree.put(dependentNode, inDegree.get(dependentNode) + 1);
            }
        }

        Queue<String> queue = new ArrayDeque<>();
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        List<String> executionOrder = new ArrayList<>();
        while (!queue.isEmpty()) {
            String cur = queue.poll();
            executionOrder.add(cur);

            for (String neighbor : edges.getOrDefault(cur, Collections.emptySet())) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        if (executionOrder.size() != nodes.size()) {
            throw new IllegalStateException("Graph contains a cycle, execution order cannot be determined.");
        }

        return executionOrder;
    }

    private boolean createCycle(String from, String to) {
        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();

        queue.add(to);
        visited.add(to);

        while (!queue.isEmpty()) {
            String cur = queue.poll();

            if (cur.equals(from)) {
                return true;
            }

            for (String neighbor : edges.getOrDefault(cur, Collections.emptySet())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        return false;
    }
}

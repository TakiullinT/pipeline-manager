package org.dealership.pipelinemanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dealership.pipelinemanager.controllers.PipelineController;
import org.dealership.pipelinemanager.domain.Pipeline;
import org.dealership.pipelinemanager.dtos.AddEdgeRequest;
import org.dealership.pipelinemanager.dtos.AddNodeRequest;
import org.dealership.pipelinemanager.exceptions.CycleDetectedException;
import org.dealership.pipelinemanager.exceptions.PipelineNotFoundException;
import org.dealership.pipelinemanager.services.PipelineService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PipelineController.class)
class PipelineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PipelineService pipelineService;

    @Test
    void shouldCreatePipelineAndReturn201() throws Exception {
        Pipeline mockPipeline = new Pipeline();
        Mockito.when(pipelineService.createPipeline()).thenReturn(mockPipeline);

        mockMvc.perform(post("/pipelines")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(mockPipeline.getId().toString()))
                .andExpect(jsonPath("$.nodes").isArray())
                .andExpect(jsonPath("$.edges").isMap());
    }

    @Test
    void shouldReturn400_whenAddingNodeWithBlankId() throws Exception {
        UUID pipelineId = UUID.randomUUID();
        AddNodeRequest request = new AddNodeRequest();
        request.setNodeId("   ");

        mockMvc.perform(post("/pipelines/{pipelineId}/nodes", pipelineId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nodeId").value("Node ID cannot be blank"));
    }

    @Test
    void shouldReturn400_whenCycleIsDetected() throws Exception {
        UUID pipelineId = UUID.randomUUID();
        AddEdgeRequest request = new AddEdgeRequest();
        request.setFrom("B");
        request.setTo("A");

        Mockito.doThrow(new CycleDetectedException("Adding edge creates a cycle."))
                .when(pipelineService).addEdge(eq(pipelineId), eq("B"), eq("A"));

        mockMvc.perform(post("/pipelines/{pipelineId}/edges", pipelineId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Adding edge creates a cycle.")); // Проверяем GlobalExceptionHandler
    }

    @Test
    void shouldReturn404_whenPipelineNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();

        Mockito.when(pipelineService.getExecutionOrder(any(UUID.class)))
                .thenThrow(new PipelineNotFoundException("Pipeline not found"));

        mockMvc.perform(get("/pipelines/{pipelineId}/execution-order", randomId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Pipeline not found"));
    }

    @Test
    void shouldReturnExecutionOrderAnd200() throws Exception {
        UUID pipelineId = UUID.randomUUID();
        List<String> expectedOrder = List.of("input", "filter", "enrich", "output");

        Mockito.when(pipelineService.getExecutionOrder(pipelineId)).thenReturn(expectedOrder);

        mockMvc.perform(get("/pipelines/{pipelineId}/execution-order", pipelineId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("input"))
                .andExpect(jsonPath("$[3]").value("output"));
    }
}
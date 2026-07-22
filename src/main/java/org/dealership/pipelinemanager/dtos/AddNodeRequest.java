package org.dealership.pipelinemanager.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddNodeRequest {
    @NotBlank(message = "Node ID cannot be blank")
    private String nodeId;
}

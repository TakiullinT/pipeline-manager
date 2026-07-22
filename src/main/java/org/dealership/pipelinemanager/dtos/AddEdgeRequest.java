package org.dealership.pipelinemanager.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddEdgeRequest {
    @NotBlank(message = "Field 'from' cannot be blank")
    private String from;

    @NotBlank(message = "Field 'to' cannot be blank")
    private String to;
}

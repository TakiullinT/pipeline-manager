package org.dealership.pipelinemanager.exceptions;

public class PipelineNotFoundException extends RuntimeException {
    public PipelineNotFoundException(String message) {
        super(message);
    }
}

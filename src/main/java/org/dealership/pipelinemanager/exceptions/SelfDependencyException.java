package org.dealership.pipelinemanager.exceptions;

public class SelfDependencyException extends RuntimeException {
    public SelfDependencyException(String message) {
        super(message);
    }
}

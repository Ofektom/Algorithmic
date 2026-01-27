package com.algorithm.web.controller;

import com.algorithm.web.service.AlgorithmExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/execute")
public class AlgorithmController {
    
    @Autowired
    private AlgorithmExecutionService executionService;
    
    @PostMapping
    public Map<String, Object> execute(@RequestBody ExecutionRequest request) {
        return executionService.execute(
            request.getClassName(),
            request.getMethodName(),
            request.getInputs()
        );
    }
    
    public static class ExecutionRequest {
        private String className;
        private String methodName;
        private Object[] inputs;
        
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        
        public String getMethodName() { return methodName; }
        public void setMethodName(String methodName) { this.methodName = methodName; }
        
        public Object[] getInputs() { return inputs; }
        public void setInputs(Object[] inputs) { this.inputs = inputs; }
    }
}

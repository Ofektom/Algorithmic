package com.algorithm.web.model;

import java.util.List;

public class ProblemInfo {
    private String name;
    private List<String> aliases;
    private String description;
    private String shortDescription;
    private List<String> keywords;
    private String category;
    private String className;
    private List<String> methods;
    private List<String> paramNames;
    private List<String> paramTypes;
    private Object[] exampleInputs;
    
    public ProblemInfo() {}
    
    public ProblemInfo(String name, List<String> aliases, String description,
                     String shortDescription, List<String> keywords, String category,
                     String className, List<String> methods,
                     List<String> paramNames, List<String> paramTypes, Object[] exampleInputs) {
        this.name = name;
        this.aliases = aliases != null ? aliases : List.of();
        this.description = description != null ? description : "";
        this.shortDescription = shortDescription != null ? shortDescription : "";
        this.keywords = keywords != null ? keywords : List.of();
        this.category = category != null ? category : "";
        this.className = className;
        this.methods = methods;
        this.paramNames = paramNames;
        this.paramTypes = paramTypes;
        this.exampleInputs = exampleInputs;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public List<String> getAliases() { return aliases; }
    public void setAliases(List<String> aliases) { this.aliases = aliases; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
    
    public List<String> getKeywords() { return keywords; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    
    public List<String> getMethods() { return methods; }
    public void setMethods(List<String> methods) { this.methods = methods; }
    
    public List<String> getParamNames() { return paramNames; }
    public void setParamNames(List<String> paramNames) { this.paramNames = paramNames; }
    
    public List<String> getParamTypes() { return paramTypes; }
    public void setParamTypes(List<String> paramTypes) { this.paramTypes = paramTypes; }
    
    public Object[] getExampleInputs() { return exampleInputs; }
    public void setExampleInputs(Object[] exampleInputs) { this.exampleInputs = exampleInputs; }
}

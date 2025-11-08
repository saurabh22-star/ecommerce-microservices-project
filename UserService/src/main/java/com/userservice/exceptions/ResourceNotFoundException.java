package com.userservice.exceptions;

import java.io.Serial;

public class ResourceNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 2L;

    private final String entityType;
    private final String searchCriteria;
    private final String criteriaValue;

    public ResourceNotFoundException(String entityType, String searchCriteria, String criteriaValue) {
        super(String.format("Entity of type '%s' could not be located using %s: '%s'", 
            entityType, searchCriteria, criteriaValue));
        this.entityType = entityType;
        this.searchCriteria = searchCriteria;
        this.criteriaValue = criteriaValue;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getSearchCriteria() {
        return searchCriteria;
    }

    public String getCriteriaValue() {
        return criteriaValue;
    }

    public ResourceNotFoundException(String entityType, String searchCriteria, Long id) {
        super(String.format("Entity of type '%s' could not be located using %s: %d", 
            entityType, searchCriteria, id));
        this.entityType = entityType;
        this.searchCriteria = searchCriteria;
        this.criteriaValue = String.valueOf(id);
    }
}


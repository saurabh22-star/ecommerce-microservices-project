package com.ecommerce.ecommercehub.productmodule.exceptions;

public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	String resourceName;
	String field;
	Long fieldId;

	public ResourceNotFoundException(String resourceName, String field, Long fieldId) {
		super(String.format("Entity of type '%s' could not be located using %s: %d", resourceName, field, fieldId));
		this.resourceName = resourceName;
		this.field = field;
		this.fieldId = fieldId;
	}
}

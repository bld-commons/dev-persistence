package com.bld.proxy.api.find.data;

import java.lang.reflect.Parameter;

/**
 * Holds metadata about a single method parameter captured during proxy invocation.
 *
 * <p>During the interception of a dynamic controller method call, each relevant
 * parameter (e.g., those annotated with {@code @RequestBody} or
 * {@code @AuthenticationPrincipal}) is wrapped in a {@code ParameterDetails}
 * instance so that its reflection {@link Parameter}, runtime value, and positional
 * index are all available in one place.</p>
 *
 * @author Francesco Baldi
 * @see com.bld.proxy.api.find.intecerptor.FindInterceptor
 */
public class ParameterDetails {

	private Parameter parameter;

	private Object value;

	private Integer index;



	/**
	 * Constructs a new {@code ParameterDetails} instance.
	 *
	 * @param parameter the reflection {@link Parameter} descriptor for the method parameter
	 * @param value     the runtime value of the parameter
	 * @param index     the zero-based position of the parameter in the method signature
	 */
	public ParameterDetails(Parameter parameter, Object value, Integer index) {
		super();
		this.parameter = parameter;
		this.value = value;
		this.index = index;
	}

	/**
	 * Returns the reflection descriptor for the method parameter.
	 *
	 * @return the {@link Parameter}; never {@code null}
	 */
	public Parameter getParameter() {
		return parameter;
	}

	/**
	 * Returns the runtime value of the parameter.
	 *
	 * @return the parameter value, or {@code null} if not provided
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Returns the zero-based index of this parameter in the method signature.
	 *
	 * @return the parameter index
	 */
	public Integer getIndex() {
		return index;
	}


}

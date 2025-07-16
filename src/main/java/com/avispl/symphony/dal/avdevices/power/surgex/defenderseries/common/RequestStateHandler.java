/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.common.constants.Constant;

/**
 * Handler is responsible for storing and processing api errors reported by the aggregator.
 * Whenever an important part of the API fails, aggregator should call {@link #pushError(String, Throwable)},
 * when the error is resolved - {@link #resolveError(String)}
 *
 * Then, {@link #verifyAPIState()} is called after the data processing, and if there are errors - the RuntimeException is thrown
 * with the details about the failed API sections and top error cause.
 *
 * @author Kevin/Symphony Team
 * @since 1.0.0
 */
public class RequestStateHandler {
	/**
	 * Map of api sections and corresponding instances of {@link Throwable}
	 */
	private final Map<String, Throwable> apiErrors = new ConcurrentHashMap<>();

	/**
	 * Add an error to the {@link #apiErrors}
	 *
	 * @param apiSection api section identifier (property group)
	 * @param error instance of Throwable thrown
	 */
	public void pushError(String apiSection, Throwable error) {
		this.apiErrors.put(apiSection, error);
	}

	/**
	 * Remove an error from {@link #apiErrors}
	 *
	 * @param apiSection API section name to remove from {@link #apiErrors}
	 */
	public void resolveError(String apiSection) {
		this.apiErrors.remove(apiSection);
	}

	/**
	 * Process {@link #apiErrors} contents and throw an error if any errors remain.
	 *
	 * @throws ResourceNotReachableException if {@link #apiErrors} is not empty
	 */
	public void verifyAPIState() {
		if (this.apiErrors.isEmpty()) {
			return;
		}

		String apiSections = String.join(Constant.COMMA, this.apiErrors.keySet());
		Throwable error = this.apiErrors.values().iterator().next();
		String errorText = error != null ? error.getMessage() : Constant.NOT_AVAILABLE;
		throw new ResourceNotReachableException(String.format(Constant.REQUEST_APIS_FAILED, apiSections, errorText));
	}
}

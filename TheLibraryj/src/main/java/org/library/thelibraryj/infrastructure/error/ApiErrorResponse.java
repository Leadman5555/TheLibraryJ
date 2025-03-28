package org.library.thelibraryj.infrastructure.error;

import lombok.Builder;

@Builder
public record ApiErrorResponse(String status, String message, Integer code, String path) { }

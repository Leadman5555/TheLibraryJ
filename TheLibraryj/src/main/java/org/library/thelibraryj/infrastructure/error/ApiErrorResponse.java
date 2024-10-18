package org.library.thelibraryj.infrastructure.error;

import lombok.Builder;
import lombok.Data;

@Builder
public record ApiErrorResponse(String status, String message, Integer code, String path) { }

package org.library.thelibraryj.book.dto;

import com.blazebit.persistence.KeysetPage;

import java.util.List;

public record PagedBookPreviewsResponse(List<BookPreviewResponse> response, int page, int totalPages, KeysetPage keysetPage) {}

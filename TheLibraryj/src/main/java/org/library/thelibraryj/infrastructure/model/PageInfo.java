package org.library.thelibraryj.infrastructure.model;

import com.blazebit.persistence.KeysetPage;

public record PageInfo(int page, int totalPages, KeysetPage keysetPage) { }

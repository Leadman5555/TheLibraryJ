package org.library.thelibraryj.book.dto.pagingDto;

import com.blazebit.persistence.Keyset;
import com.blazebit.persistence.KeysetPage;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PreviewKeySetPage(int firstResult,
                                int maxResults,
                                @Schema(implementation = PreviewKeySet.class) PreviewKeySet highest,
                                @Schema(implementation = PreviewKeySet.class) PreviewKeySet lowest,
                                @Schema(implementation = Keyset.class) List<Keyset> keysets) implements KeysetPage {

    @Override
    public int getFirstResult() {
        return firstResult;
    }

    @Override
    public int getMaxResults() {
        return maxResults;
    }

    @Override
    public Keyset getLowest() {
        return lowest;
    }

    @Override
    public Keyset getHighest() {
        return highest;
    }

    @Override
    public List<Keyset> getKeysets() {
        return keysets;
    }
}

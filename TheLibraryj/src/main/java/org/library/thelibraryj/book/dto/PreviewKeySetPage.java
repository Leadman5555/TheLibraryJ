package org.library.thelibraryj.book.dto;

import com.blazebit.persistence.Keyset;
import com.blazebit.persistence.KeysetPage;

import java.util.List;

public record PreviewKeySetPage(int firstResult, int maxResults, PreviewKeySet highest,
                                PreviewKeySet lowest, List<Keyset> keysets) implements KeysetPage {

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
        return highest;
    }

    @Override
    public Keyset getHighest() {
        return lowest;
    }

    @Override
    public List<Keyset> getKeysets() {
        return keysets;
    }
}

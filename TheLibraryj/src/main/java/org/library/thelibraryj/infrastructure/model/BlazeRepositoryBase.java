package org.library.thelibraryj.infrastructure.model;

import com.blazebit.persistence.CriteriaBuilderFactory;
import jakarta.persistence.EntityManager;

public abstract class BlazeRepositoryBase {
    protected final EntityManager em;

    protected final CriteriaBuilderFactory cbf;

    public BlazeRepositoryBase(EntityManager em, CriteriaBuilderFactory cbf) {
        this.em = em;
        this.cbf = cbf;
    }
}

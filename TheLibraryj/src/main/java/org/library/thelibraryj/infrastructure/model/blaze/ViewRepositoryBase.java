package org.library.thelibraryj.infrastructure.model.blaze;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import jakarta.persistence.EntityManager;

public abstract class ViewRepositoryBase extends BlazeRepositoryBase {

    protected final EntityViewManager evm;

    public ViewRepositoryBase(EntityManager entityManager, CriteriaBuilderFactory builderFactory,
                              EntityViewManager viewManager) {
        super(entityManager, builderFactory);
        this.evm = viewManager;
    }
}

package org.library.thelibraryj.authentication.userAuth.domain;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import jakarta.persistence.EntityManager;
import org.library.thelibraryj.infrastructure.model.blaze.ViewRepositoryBase;

class UserAuthViewRepositoryImpl extends ViewRepositoryBase implements UserAuthViewRepository {
    public UserAuthViewRepositoryImpl(EntityManager entityManager, CriteriaBuilderFactory builderFactory, EntityViewManager viewManager) {
        super(entityManager, builderFactory, viewManager);
    }

    @Override
    public BasicUserAuthView getBasicUserAuthData(String email) {
        return evm.applySetting(EntityViewSetting.create(BasicUserAuthView.class),
                        cbf.create(em, UserAuth.class, "u")
                                .where("u.email").eq(email))
                .getSingleResult();
    }

    @Override
    public LoginDataView getLoginData(String email) {
        return evm.applySetting(EntityViewSetting.create(LoginDataView.class),
                        cbf.create(em, UserAuth.class, "u")
                                .where("u.email").eq(email))
                .getSingleResult();
    }

    @Override
    public PasswordResetView getPasswordResetData(String email) {
        return evm.applySetting(EntityViewSetting.create(PasswordResetView.class),
                        cbf.create(em, UserAuth.class, "u")
                                .where("u.email").eq(email))
                .getSingleResult();
    }
}

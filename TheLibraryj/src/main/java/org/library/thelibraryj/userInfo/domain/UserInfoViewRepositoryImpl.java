package org.library.thelibraryj.userInfo.domain;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import jakarta.persistence.EntityManager;
import org.library.thelibraryj.infrastructure.model.ViewRepositoryBase;
import org.springframework.stereotype.Repository;

@Repository
class UserInfoViewRepositoryImpl extends ViewRepositoryBase implements UserInfoViewRepository {

    public UserInfoViewRepositoryImpl(EntityManager entityManager, CriteriaBuilderFactory builderFactory, EntityViewManager viewManager) {
        super(entityManager, builderFactory, viewManager);
    }

    @Override
    public RatingUpsertView getRatingUpsertView(String email) {
        return evm.applySetting(EntityViewSetting.create(RatingUpsertView.class),
                        cbf.create(em, UserInfo.class, "u")
                                .where("u.email").eq(email))
                .getSingleResult();
    }

    @Override
    public BookCreationUserView getBookCreationUserView(String email) {
        return evm.applySetting(EntityViewSetting.create(BookCreationUserView.class),
                        cbf.create(em, UserInfo.class, "u")
                                .where("u.email").eq(email))
                .getSingleResult();
    }

    @Override
    public UserInfoDetailsView getUserInfoDetailsView(String username) {
        return evm.applySetting(EntityViewSetting.create(UserInfoDetailsView.class),
                cbf.create(em, UserInfo.class, "u")
                        .where("u.username").eq(username))
                .getSingleResult();
    }

    @Override
    public UserInfoMiniView getUserInfoMiniView(String email) {
        return evm.applySetting(EntityViewSetting.create(UserInfoMiniView.class),
                        cbf.create(em, UserInfo.class, "u")
                                .where("u.email").eq(email))
                .getSingleResult();
    }
}

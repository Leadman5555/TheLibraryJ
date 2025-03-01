package org.library.thelibraryj.userInfo.domain;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import jakarta.persistence.EntityManager;
import org.library.thelibraryj.infrastructure.model.blaze.ViewRepositoryBase;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class UserInfoViewRepositoryImpl extends ViewRepositoryBase implements UserInfoViewRepository {

    public UserInfoViewRepositoryImpl(EntityManager entityManager, CriteriaBuilderFactory builderFactory, EntityViewManager viewManager) {
        super(entityManager, builderFactory, viewManager);
    }

    @Override
    public Optional<RatingUpsertView> getRatingUpsertView(String email) {
        List<RatingUpsertView> resultList = evm.applySetting(EntityViewSetting.create(RatingUpsertView.class),
                        cbf.create(em, UserInfo.class)
                                .where("email").eq(email))
                .getResultList();
        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.getFirst());
    }

    @Override
    public Optional<BookCreationUserView> getBookCreationUserView(String email) {
        List<BookCreationUserView> resultList = evm.applySetting(EntityViewSetting.create(BookCreationUserView.class),
                        cbf.create(em, UserInfo.class)
                                .where("email").eq(email))
                .getResultList();
        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.getFirst());
    }

    @Override
    public Optional<UserInfoDetailsView> getUserInfoDetailsView(String username) {
        List<UserInfoDetailsView> resultList = evm.applySetting(EntityViewSetting.create(UserInfoDetailsView.class),
                        cbf.create(em, UserInfo.class)
                                .where("username").eq(username))
                .getResultList();
        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.getFirst());
    }

    @Override
    public Optional<UserInfoMiniView> getUserInfoMiniView(String email) {
        List<UserInfoMiniView> resultList = evm.applySetting(EntityViewSetting.create(UserInfoMiniView.class),
                        cbf.create(em, UserInfo.class)
                                .where("email").eq(email))
                .getResultList();
        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.getFirst());
    }

    @Override
    public List<UserInfoRankView> getTopRatedUsersRankView(int limit) {
        return evm.applySetting(EntityViewSetting.create(UserInfoRankView.class)
                        , cbf.create(em, UserInfo.class)
                                .orderByDesc("rank")
                                .orderByDesc("currentScore")
                                .setMaxResults(limit))
                .getResultList();
    }
}

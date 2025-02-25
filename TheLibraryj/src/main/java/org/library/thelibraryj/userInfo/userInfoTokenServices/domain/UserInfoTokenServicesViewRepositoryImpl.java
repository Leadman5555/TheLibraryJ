package org.library.thelibraryj.userInfo.userInfoTokenServices.domain;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import jakarta.persistence.EntityManager;
import org.library.thelibraryj.infrastructure.model.blaze.ViewRepositoryBase;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class UserInfoTokenServicesViewRepositoryImpl extends ViewRepositoryBase implements UserInfoTokenServicesViewRepository {

    public UserInfoTokenServicesViewRepositoryImpl(EntityManager entityManager, CriteriaBuilderFactory builderFactory, EntityViewManager viewManager) {
        super(entityManager, builderFactory, viewManager);
    }

    @Override
    public Optional<MiniFavouriteBookTokenView> fetchByUserId(UUID userId) {
        List<MiniFavouriteBookTokenView> resultList = evm.applySetting(EntityViewSetting.create(MiniFavouriteBookTokenView.class),
                        cbf.create(em, FavouriteBookToken.class)
                                .where("forUserId").eq(userId))
                .getResultList();
        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.getFirst());
    }

    @Override
    public Optional<EssentialFavouriteBookTokenView> fetchByToken(UUID token) {
        List<EssentialFavouriteBookTokenView> resultList = evm.applySetting(EntityViewSetting.create(EssentialFavouriteBookTokenView.class),
                        cbf.create(em, FavouriteBookToken.class)
                                .where("token").eq(token))
                .getResultList();
        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.getFirst());
    }
}

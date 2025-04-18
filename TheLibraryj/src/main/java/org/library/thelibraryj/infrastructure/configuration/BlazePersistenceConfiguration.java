package org.library.thelibraryj.infrastructure.configuration;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.spi.CriteriaBuilderConfiguration;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViews;
import com.blazebit.persistence.view.spi.EntityViewConfiguration;
import io.hypersistence.utils.spring.repository.BaseJpaRepositoryImpl;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.library.thelibraryj.authentication.userAuth.domain.BasicUserAuthView;
import org.library.thelibraryj.authentication.userAuth.domain.LoginDataView;
import org.library.thelibraryj.authentication.userAuth.domain.PasswordResetView;
import org.library.thelibraryj.book.domain.ChapterPreviewContentView;
import org.library.thelibraryj.book.domain.NotificationEssentialsView;
import org.library.thelibraryj.userInfo.domain.BookCreationUserView;
import org.library.thelibraryj.userInfo.domain.RatingUpsertView;
import org.library.thelibraryj.userInfo.domain.UserInfoDetailsView;
import org.library.thelibraryj.userInfo.domain.UserInfoMiniView;
import org.library.thelibraryj.userInfo.domain.UserInfoRankView;
import org.library.thelibraryj.userInfo.userInfoTokenServices.domain.EssentialFavouriteBookTokenView;
import org.library.thelibraryj.userInfo.userInfoTokenServices.domain.MiniFavouriteBookTokenView;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        value = {"org.library.thelibraryj.book.domain",
                "org.library.thelibraryj.userInfo.domain",
                "org.library.thelibraryj.authentication.userAuth.domain",
                "org.library.thelibraryj.authentication.authTokenServices.domain",
                "org.library.thelibraryj.userInfo.userInfoTokenServices.domain"},
        repositoryBaseClass = BaseJpaRepositoryImpl.class
)
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class BlazePersistenceConfiguration {

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    @Lazy(false)
    public CriteriaBuilderFactory createCriteriaBuilderFactory() {
        CriteriaBuilderConfiguration config = Criteria.getDefault();
        return config.createCriteriaBuilderFactory(entityManagerFactory);
    }

    @Bean
    @Lazy(false)
    EntityViewConfiguration entityViewConfiguration() {
        EntityViewConfiguration evc = EntityViews.createDefaultConfiguration();
        evc.addEntityView(BookCreationUserView.class);
        evc.addEntityView(UserInfoDetailsView.class);
        evc.addEntityView(RatingUpsertView.class);
        evc.addEntityView(BasicUserAuthView.class);
        evc.addEntityView(LoginDataView.class);
        evc.addEntityView(PasswordResetView.class);
        evc.addEntityView(ChapterPreviewContentView.class);
        evc.addEntityView(UserInfoMiniView.class);
        evc.addEntityView(MiniFavouriteBookTokenView.class);
        evc.addEntityView(EssentialFavouriteBookTokenView.class);
        evc.addEntityView(UserInfoRankView.class);
        evc.addEntityView(NotificationEssentialsView.class);
        return evc;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    @Lazy(false)
    public EntityViewManager createEntityViewManager(CriteriaBuilderFactory cbf, EntityViewConfiguration entityViewConfiguration) {
        return entityViewConfiguration.createEntityViewManager(cbf);
    }
}

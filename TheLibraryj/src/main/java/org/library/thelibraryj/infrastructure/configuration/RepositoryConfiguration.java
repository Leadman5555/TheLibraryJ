package org.library.thelibraryj.infrastructure.configuration;

import io.hypersistence.utils.spring.repository.BaseJpaRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        value = {"org.library.thelibraryj.book.domain","org.library.thelibraryj.userInfo.domain",
        "org.library.thelibraryj.authentication.userAuth.domain"},
        repositoryBaseClass = BaseJpaRepositoryImpl.class
)
public class RepositoryConfiguration {
}

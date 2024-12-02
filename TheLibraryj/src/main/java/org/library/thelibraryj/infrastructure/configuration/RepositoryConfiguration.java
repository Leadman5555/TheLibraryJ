package org.library.thelibraryj.infrastructure.configuration;

import io.hypersistence.utils.spring.repository.BaseJpaRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        value = {"org.library.thelibraryj.book.domain","org.library.thelibraryj.userInfo.domain",
        "org.library.thelibraryj.authentication.userAuth.domain", "org.library.thelibraryj.authentication.tokenServices.domain"},
        repositoryBaseClass = BaseJpaRepositoryImpl.class
)
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class RepositoryConfiguration {
}

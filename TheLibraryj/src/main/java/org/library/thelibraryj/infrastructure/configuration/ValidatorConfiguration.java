package org.library.thelibraryj.infrastructure.configuration;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
class ValidatorConfiguration {

    @Bean
    public LocalValidatorFactoryBean validatorFactory() {
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        factoryBean.setProviderClass(HibernateValidator.class);
        return factoryBean;
    }

}

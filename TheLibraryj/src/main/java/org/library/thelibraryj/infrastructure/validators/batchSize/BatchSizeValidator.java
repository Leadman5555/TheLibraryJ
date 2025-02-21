package org.library.thelibraryj.infrastructure.validators.batchSize;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class BatchSizeValidator implements ConstraintValidator<ValidBatchSize, List<?>> {

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int maxBatchSize;

    @Override
    public boolean isValid(List<?> requestList, ConstraintValidatorContext constraintValidatorContext) {
        return requestList != null && !requestList.isEmpty() && requestList.size() <= maxBatchSize;
    }
}

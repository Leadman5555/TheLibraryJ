package org.library.thelibraryj.infrastructure.validators.fileValidators.imageFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.library.thelibraryj.infrastructure.validators.fileValidators.FileFormatMatcher;
import org.springframework.web.multipart.MultipartFile;

class ImageFormatValidator implements ConstraintValidator<ValidImageFormat, MultipartFile> {

    private final FileFormatMatcher fileFormatMatcher =  FileFormatMatcher.IMAGE_FILE_FORMAT_MATCHER;

    @Override
    public boolean isValid(MultipartFile requestFile, ConstraintValidatorContext constraintValidatorContext) {
        return requestFile == null || (!requestFile.isEmpty() &&  fileFormatMatcher.isValidFormat(requestFile));
    }
}

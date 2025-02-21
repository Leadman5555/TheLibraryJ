package org.library.thelibraryj.infrastructure.validators.fileValidators.textFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.library.thelibraryj.infrastructure.validators.fileValidators.FileFormatMatcher;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

class TextFileArrayFormatValidator implements ConstraintValidator<ValidTextFileFormat, List<MultipartFile>> {

    private final FileFormatMatcher fileFormatMatcher =  FileFormatMatcher.TEXT_FILE_FORMAT_MATCHER;

    @Override
    public boolean isValid(List<MultipartFile> requestFiles, ConstraintValidatorContext constraintValidatorContext) {
        return requestFiles != null && fileFormatMatcher.isValidFormat(requestFiles);
    }
}
package org.library.thelibraryj.infrastructure.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ValidationException;
import org.library.thelibraryj.book.domain.BookTag;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookTagListConverter implements Converter<String, List<BookTag>> {

    private final ObjectMapper om = new ObjectMapper();

    @Override
    public List<BookTag> convert(@NonNull String source) {
        try {
            return om.readValue(source, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new ValidationException("Cannot convert source to List<BookTag>");
        }
    }
}

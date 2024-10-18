package org.library.thelibraryj.book.domain;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.library.thelibraryj.book.dto.BookDetailsResponse;
import org.library.thelibraryj.book.dto.BookPreviewResponse;
import org.library.thelibraryj.infrastructure.error.BookError;
import org.library.thelibraryj.infrastructure.error.GeneralError;
import org.library.thelibraryj.infrastructure.error.ServiceError;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
record BookService(BookDetailRepository bookDetailRepository, BookPreviewRepository bookPreviewRepository, BookMapper mapper)
        implements org.library.thelibraryj.book.BookService {

    @Override
    public Either<GeneralError, BookDetailsResponse> getBookDetail(UUID detailsId) {
        return Try.of(() -> bookDetailRepository.findById(detailsId))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(optionalEntity -> optionalEntity.toEither(new BookError.BookEntityNotFound(detailsId, null)))
                .map(mapper::bookDetailsToBookDetailsResponse);
    }

    @Override
    @Cacheable("bookPreviews")
    public List<BookPreviewResponse> getBookPreviews() {
        return bookPreviewRepository.findAll().stream().map(mapper::bookPreviewToBookPreviewResponse).toList();
    }

    @CacheEvict("bookPreviews")
    @Scheduled(fixedDelayString = "${library.caching.bookPreviewTTL}")
    public void resetBookPreviewsCache(){}
}

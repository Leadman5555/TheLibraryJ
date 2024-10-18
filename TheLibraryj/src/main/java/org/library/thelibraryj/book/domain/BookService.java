package org.library.thelibraryj.book.domain;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.library.thelibraryj.book.dto.*;
import org.library.thelibraryj.infrastructure.error.errorTypes.BookError;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.error.errorTypes.ServiceError;
import org.library.thelibraryj.userDetails.UserDetailsService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
class BookService implements org.library.thelibraryj.book.BookService {
    private final BookDetailRepository bookDetailRepository;
    private final BookPreviewRepository bookPreviewRepository;
    private final BookMapper mapper;
    private final UserDetailsService userDetailsService;

    public BookService(BookDetailRepository bookDetailRepository, BookPreviewRepository bookPreviewRepository, BookMapper mapper, UserDetailsService userDetailsService) {
        this.bookDetailRepository = bookDetailRepository;
        this.bookPreviewRepository = bookPreviewRepository;
        this.mapper = mapper;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Either<GeneralError, BookDetailResponse> getBookDetailResponse(UUID detailId) {
        Either<GeneralError, BookDetail> fetched = getBookDetail(detailId);
        if(fetched.isRight()) return Either.right(mapper.bookDetailToBookDetailResponse(fetched.get()));
        return Either.left(fetched.getLeft());
    }

    private Either<GeneralError, BookDetail> getBookDetail(UUID detailId) {
        return Try.of(() -> bookDetailRepository.findById(detailId))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(optionalEntity -> optionalEntity.toEither(new BookError.BookDetailEntityNotFound(detailId)));
    }

    @Override
    public Either<GeneralError, BookPreviewResponse> getBookPreviewResponse(UUID detailId) {
        Either<GeneralError, BookPreview> fetched = getBookPreview(detailId);
        if(fetched.isRight()) return Either.right(mapper.bookPreviewToBookPreviewResponse(fetched.get()));
        return Either.left(fetched.getLeft());
    }

    private Either<GeneralError, BookPreview> getBookPreview(UUID previewId) {
        return Try.of(() -> bookPreviewRepository.findById(previewId))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(optionalEntity -> optionalEntity.toEither(new BookError.BookPreviewEntityNotFound(previewId, null)));
    }

    @Override
    public Either<GeneralError, BookResponse> createBook(BookCreationRequest bookCreationRequest) {
        Either<GeneralError, String> fetchedAuthor = userDetailsService.getUsernameById(bookCreationRequest.authorId());
        if(fetchedAuthor.isLeft()) return Either.left(fetchedAuthor.getLeft());

        UUID bookId = UUID.randomUUID();
        BookDetail detail = BookDetail.builder()
                .id(bookId)
                .author(fetchedAuthor.get())
                .authorId(bookCreationRequest.authorId())
                .description(bookCreationRequest.description())
                .build();
        BookPreview preview = BookPreview.builder()
                .id(bookId)
                .title(bookCreationRequest.title())
                .ratingCount(0)
                .averageRating(0)
                .bookState(BookState.UNKNOWN)
                .build();

        bookDetailRepository.save(detail);
        bookPreviewRepository.save(preview);
        return Either.right(mapper.bookToBookResponse(detail, preview));
    }

    public Either<GeneralError, BookResponse> updateBook(BookRequest bookRequest) {
        Either<GeneralError, BookDetail> detail = getBookDetail(bookRequest.bookId());
        if(detail.isLeft()) return Either.left(detail.getLeft());
        Either<GeneralError, BookPreview> preview = getBookPreview(bookRequest.bookId());
        if(preview.isLeft()) return Either.left(preview.getLeft());

        boolean previewChanged = false;
        if(bookRequest.state() != null) {
            preview.get().setBookState(bookRequest.state());
            previewChanged = true;
        }
        if(bookRequest.title() != null) {
            preview.get().setTitle(bookRequest.title());
            previewChanged = true;
        }
        if(bookRequest.description() != null) {
            detail.get().setDescription(bookRequest.description());
            bookDetailRepository.save(detail.get());
        }
        if(previewChanged) bookPreviewRepository.save(preview.get());

        return Either.right(mapper.bookToBookResponse(detail.get(), preview.get()));
    }

    @Override
    public Either<GeneralError, BookResponse> getBook(String title) {
        Either<GeneralError, BookPreview> preview = Try.of(() -> bookPreviewRepository.findByTitle(title))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(optionalEntity -> optionalEntity.toEither(new BookError.BookPreviewEntityNotFound(null, title)));
        if(preview.isLeft()) return Either.left(preview.getLeft());
        Either<GeneralError, BookDetail> detail = getBookDetail(preview.get().getId());
        if(detail.isLeft()) return Either.left(detail.getLeft());
        return Either.right(mapper.bookToBookResponse(detail.get(), preview.get()));
    }

    @Override
    @Cacheable("bookPreviews")
    public List<BookPreviewResponse> getBookPreviewResponses() {
        return bookPreviewRepository.findAll().stream().map(mapper::bookPreviewToBookPreviewResponse).toList();
    }

    @CacheEvict("bookPreviews")
    @Scheduled(fixedDelayString = "${library.caching.bookPreviewTTL}")
    public void resetBookPreviewsCache(){}
}

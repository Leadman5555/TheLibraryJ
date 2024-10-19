package org.library.thelibraryj.book.domain;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.library.thelibraryj.book.dto.BookCreationRequest;
import org.library.thelibraryj.book.dto.BookDetailResponse;
import org.library.thelibraryj.book.dto.BookPreviewResponse;
import org.library.thelibraryj.book.dto.BookUpdateRequest;
import org.library.thelibraryj.book.dto.BookResponse;
import org.library.thelibraryj.infrastructure.error.errorTypes.BookError;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.error.errorTypes.ServiceError;
import org.library.thelibraryj.userInfo.UserInfoService;
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
    private final UserInfoService userInfoService;

    public BookService(BookDetailRepository bookDetailRepository, BookPreviewRepository bookPreviewRepository, BookMapper mapper, UserInfoService userInfoService) {
        this.bookDetailRepository = bookDetailRepository;
        this.bookPreviewRepository = bookPreviewRepository;
        this.mapper = mapper;
        this.userInfoService = userInfoService;
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
        Either<GeneralError, String> fetchedAuthor = userInfoService.getAuthorUsernameAndCheckValid(bookCreationRequest.authorId());
        if(fetchedAuthor.isLeft()) return Either.left(fetchedAuthor.getLeft());

        BookDetail detail = BookDetail.builder()
                .author(fetchedAuthor.get())
                .authorId(bookCreationRequest.authorId())
                .description(bookCreationRequest.description())
                .build();
        BookPreview preview = BookPreview.builder()
                .bookDetail(detail)
                .title(bookCreationRequest.title())
                .ratingCount(0)
                .averageRating(0)
                .bookState(BookState.UNKNOWN)
                .build();
        bookDetailRepository.save(detail);
        bookPreviewRepository.save(preview);
        return Either.right(mapper.bookToBookResponse(detail, preview));
    }

    public Either<GeneralError, BookResponse> updateBook(BookUpdateRequest bookUpdateRequest) {
        Either<GeneralError, BookDetail> detail = getBookDetail(bookUpdateRequest.bookId());
        if(detail.isLeft()) return Either.left(detail.getLeft());
        Either<GeneralError, BookPreview> preview = getBookPreview(bookUpdateRequest.bookId());
        if(preview.isLeft()) return Either.left(preview.getLeft());

        boolean previewChanged = false;
        if(bookUpdateRequest.state() != null) {
            preview.get().setBookState(bookUpdateRequest.state());
            previewChanged = true;
        }
        if(bookUpdateRequest.title() != null) {
            preview.get().setTitle(bookUpdateRequest.title());
            previewChanged = true;
        }
        if(bookUpdateRequest.description() != null) {
            detail.get().setDescription(bookUpdateRequest.description());
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

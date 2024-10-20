package org.library.thelibraryj.book.domain;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.library.thelibraryj.book.dto.*;
import org.library.thelibraryj.infrastructure.error.errorTypes.BookError;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.error.errorTypes.ServiceError;
import org.library.thelibraryj.infrastructure.error.errorTypes.UserDetailsError;
import org.library.thelibraryj.userInfo.UserInfoService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
class BookService implements org.library.thelibraryj.book.BookService {
    private final BookDetailRepository bookDetailRepository;
    private final BookPreviewRepository bookPreviewRepository;
    private final RatingRepository ratingRepository;
    private final ChapterPreviewRepository chapterPreviewRepository;
    private final BookMapper mapper;
    private final UserInfoService userInfoService;

    public BookService(BookDetailRepository bookDetailRepository, BookPreviewRepository bookPreviewRepository, BookMapper mapper, UserInfoService userInfoService, RatingRepository ratingRepository, ChapterPreviewRepository chapterPreviewRepository) {
        this.bookDetailRepository = bookDetailRepository;
        this.bookPreviewRepository = bookPreviewRepository;
        this.mapper = mapper;
        this.userInfoService = userInfoService;
        this.ratingRepository = ratingRepository;
        this.chapterPreviewRepository = chapterPreviewRepository;
    }

    @Override
    public Either<GeneralError, BookDetailResponse> getBookDetailResponse(UUID detailId) {
        Either<GeneralError, BookDetail> fetched = getBookDetail(detailId);
        if (fetched.isRight())
            return Either.right(mapper.bookDetailToBookDetailResponse(
                    fetched.get(),
                    getChapterPreviewResponsesForBook(detailId),
                    getRatingResponsesForBook(detailId)));
        return Either.left(fetched.getLeft());
    }

    public Either<GeneralError, BookDetail> getBookDetail(UUID detailId) {
        return Try.of(() -> bookDetailRepository.findById(detailId))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(optionalEntity -> optionalEntity.toEither(new BookError.BookDetailEntityNotFound(detailId)));
    }

    @Override
    public Either<GeneralError, BookPreviewResponse> getBookPreviewResponse(UUID detailId) {
        Either<GeneralError, BookPreview> fetched = getBookPreviewEager(detailId);
        if (fetched.isRight()) return Either.right(mapper.bookPreviewToBookPreviewResponse(fetched.get()));
        return Either.left(fetched.getLeft());
    }

    public Either<GeneralError, BookPreview> getBookPreviewLazy(UUID previewId) {
        return Try.of(() -> bookPreviewRepository.findById(previewId))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(optionalEntity -> optionalEntity.toEither(new BookError.BookPreviewEntityNotFound(previewId, null)));
    }

    public Either<GeneralError, BookPreview> getBookPreviewEager(UUID previewId) {
        return Try.of(() -> bookPreviewRepository.getBookPreviewEager(previewId))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(optionalEntity -> optionalEntity.toEither(new BookError.BookPreviewEntityNotFound(previewId, null)));
    }

    @Transactional
    @Override
    public Either<GeneralError, BookResponse> createBook(BookCreationRequest bookCreationRequest) {
        Either<GeneralError, String> fetchedAuthor = userInfoService.getAuthorUsernameAndCheckValid(bookCreationRequest.authorId());
        if (fetchedAuthor.isLeft()) return Either.left(fetchedAuthor.getLeft());
        if(bookPreviewRepository.existsByTitle(bookCreationRequest.title())) return Either.left(new BookError.DuplicateTitleError());

        BookDetail detail = BookDetail.builder()
                .author(fetchedAuthor.get())
                .authorId(bookCreationRequest.authorId())
                .description(escapeHtml(bookCreationRequest.description()))
                .build();
        BookPreview preview = BookPreview.builder()
                .title(escapeHtml(bookCreationRequest.title()))
                .ratingCount(0)
                .averageRating(0)
                .bookState(BookState.UNKNOWN)
                .bookTags(bookCreationRequest.tags())
                .build();
        preview.setBookDetail(detail);
        bookDetailRepository.persist(detail);
        bookPreviewRepository.persist(preview);
        return Either.right(getBookResponse(detail, preview));
    }

    @Transactional
    @Override
    public Either<GeneralError, BookResponse> updateBook(BookUpdateRequest bookUpdateRequest) {
        Either<GeneralError, BookDetail> detail = getBookDetail(bookUpdateRequest.bookId());
        if (detail.isLeft()) return Either.left(detail.getLeft());
        Either<GeneralError, BookPreview> preview = getBookPreviewLazy(bookUpdateRequest.bookId());
        if (preview.isLeft()) return Either.left(preview.getLeft());

        boolean previewChanged = false;
        if (bookUpdateRequest.state() != null) {
            preview.get().setBookState(bookUpdateRequest.state());
            previewChanged = true;
        }
        if (bookUpdateRequest.title() != null) {
            preview.get().setTitle(escapeHtml(bookUpdateRequest.title()));
            previewChanged = true;
        }
        if (bookUpdateRequest.bookTags() != null) {
            preview.get().setBookTags(bookUpdateRequest.bookTags());
            previewChanged = true;
        }
        if (bookUpdateRequest.description() != null) {
            detail.get().setDescription(escapeHtml(bookUpdateRequest.description()));
            bookDetailRepository.update(detail.get());
        }
        if (previewChanged) bookPreviewRepository.update(preview.get());

        return Either.right(getBookResponse(detail.get(), preview.get()));
    }

    @Override
    public Either<GeneralError, BookResponse> getBook(String title) {
        Either<GeneralError, BookPreview> preview = Try.of(() -> bookPreviewRepository.findByTitle(title))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(optionalEntity -> optionalEntity.toEither(new BookError.BookPreviewEntityNotFound(null, title)));
        if (preview.isLeft()) return Either.left(preview.getLeft());
        Either<GeneralError, BookDetail> detail = getBookDetail(preview.get().getId());
        if (detail.isLeft()) return Either.left(detail.getLeft());
        return Either.right(getBookResponse(detail.get(), preview.get()));
    }

    @Transactional
    @Override
    public Either<GeneralError, RatingResponse> upsertRating(RatingRequest ratingRequest) {
        if (!userInfoService.existsById(ratingRequest.userId()))
            return Either.left(new UserDetailsError.UserDetailsEntityNotFound(ratingRequest.userId()));

        Either<GeneralError, BookPreview> ePreview = getBookPreviewLazy(ratingRequest.bookId());
        if (ePreview.isLeft()) return Either.left(ePreview.getLeft());

        Optional<Rating> prevRating = ratingRepository.getRatingForBookAndUser(ratingRequest.bookId(), ratingRequest.userId());
        BookPreview preview = ePreview.get();
        BookDetail detailReference = bookDetailRepository.getReferenceById(ratingRequest.bookId());

        final String escapedComment;
        if (ratingRequest.comment() == null) escapedComment = "";
        else escapedComment = escapeHtml(ratingRequest.comment());

        if (prevRating.isPresent()) {
            Rating rating = prevRating.get();
            preview.setAverageRating(
                    (preview.getAverageRating() * preview.getRatingCount() - rating.getCurrentRating() + ratingRequest.currentRating()) / preview.getRatingCount()
            );
            rating.setCurrentRating(ratingRequest.currentRating());
            rating.setComment(escapedComment);
            ratingRepository.update(rating);
        } else {
            preview.setAverageRating(
                    (preview.getAverageRating() + ratingRequest.currentRating()) / (preview.getRatingCount() + 1)
            );
            preview.setRatingCount(preview.getRatingCount() + 1);
            ratingRepository.persist(Rating.builder()
                    .currentRating(ratingRequest.currentRating())
                    .userId(ratingRequest.userId())
                    .comment(escapedComment)
                    .bookDetail(detailReference)
                    .build());
        }
        bookPreviewRepository.update(preview);
        return Either.right(new RatingResponse(ratingRequest.userId(), ratingRequest.currentRating(), escapedComment, LocalDateTime.now()));
    }

    @Override
    public Either<GeneralError, ChapterPreviewResponse> createChapter(ChapterRequest chapterRequest) {
        return null
    }

    public BookResponse getBookResponse(BookDetail bookDetail, BookPreview bookPreviewEager) {
        return new BookResponse(
                bookPreviewEager.getTitle(),
                bookDetail.getAuthor(),
                bookDetail.getAuthorId(),
                bookDetail.getDescription(),
                bookPreviewEager.getChapterCount(),
                bookPreviewEager.getAverageRating(),
                bookPreviewEager.getRatingCount(),
                getChapterPreviewResponsesForBook(bookDetail.getId()),
                getRatingResponsesForBook(bookDetail.getId()),
                bookPreviewEager.getBookTags(),
                bookPreviewEager.getBookState()
        );
    }

    public List<RatingResponse> getRatingResponsesForBook(UUID bookId) {
        return mapper.ratingsToRatingResponseList(getRatingsForBook(bookId));
    }

    public List<Rating> getRatingsForBook(UUID bookId) {
        return ratingRepository.getAllRatingsForBook(bookId);
    }

    public List<ChapterPreviewResponse> getChapterPreviewResponsesForBook(UUID bookId) {
        return mapper.chapterPreviewsToChapterPreviewResponseList(getChapterPreviewsForBook(bookId));
    }

    public List<ChapterPreview> getChapterPreviewsForBook(UUID bookId) {
        return chapterPreviewRepository.getAllChapterPreviewsForBook(bookId);
    }

    @Override
    @Cacheable("bookPreviews")
    public List<BookPreviewResponse> getBookPreviewResponses() {
        return bookPreviewRepository.getAllBookPreviewsEager().stream().map(mapper::bookPreviewToBookPreviewResponse).toList();
    }

    @Override
    @CacheEvict("bookPreviews")
    @Scheduled(fixedDelayString = "${library.caching.bookPreviewTTL}")
    public void resetBookPreviewsCache() {
        bookPreviewRepository.flush();
        ratingRepository.flush();
        bookDetailRepository.flush();
        chapterPreviewRepository.flush();
    }

    private static String escapeHtml(String toEscape) {
        return HtmlUtils.htmlEscape(toEscape);
    }
}

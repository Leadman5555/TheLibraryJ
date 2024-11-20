package org.library.thelibraryj.book.domain;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import jakarta.validation.constraints.NotEmpty;
import org.library.thelibraryj.book.dto.*;
import org.library.thelibraryj.infrastructure.error.errorTypes.BookError;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.error.errorTypes.ServiceError;
import org.library.thelibraryj.userInfo.UserInfoService;
import org.library.thelibraryj.userInfo.dto.BookCreationUserData;
import org.library.thelibraryj.userInfo.dto.RatingUpsertData;
import org.library.thelibraryj.userInfo.dto.UserInfoScoreUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
class BookServiceImpl implements org.library.thelibraryj.book.BookService{
    private final BookDetailRepository bookDetailRepository;
    private final BookPreviewRepository bookPreviewRepository;
    private final RatingRepository ratingRepository;
    private final ChapterPreviewRepository chapterPreviewRepository;
    private final ChapterRepository chapterRepository;
    private final BookMapper mapper;
    private final BookImageHandler bookImageHandler;
    private UserInfoService userInfoService;

    @Autowired
    public void setUserInfoService(@Lazy UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    public BookServiceImpl(BookDetailRepository bookDetailRepository, BookPreviewRepository bookPreviewRepository, BookMapper mapper, RatingRepository ratingRepository, ChapterPreviewRepository chapterPreviewRepository, ChapterRepository chapterRepository, BookImageHandler bookImageHandler) {
        this.bookDetailRepository = bookDetailRepository;
        this.bookPreviewRepository = bookPreviewRepository;
        this.mapper = mapper;
        this.ratingRepository = ratingRepository;
        this.chapterPreviewRepository = chapterPreviewRepository;
        this.chapterRepository = chapterRepository;
        this.bookImageHandler = bookImageHandler;
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

    Either<GeneralError, BookDetail> getBookDetail(UUID detailId) {
        return Try.of(() -> bookDetailRepository.findById(detailId))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(optionalEntity -> optionalEntity.toEither(new BookError.BookDetailEntityNotFound(detailId)));
    }

    @Override
    public Either<GeneralError, BookPreviewResponse> getBookPreviewResponse(UUID detailId) {
        Either<GeneralError, BookPreview> fetched = getBookPreviewEager(detailId);
        if (fetched.isRight()) return Either.right(mapper.bookPreviewWithCoverToBookPreviewResponse(fetched.get(), bookImageHandler.fetchCoverImage(fetched.get().getTitle())));
        return Either.left(fetched.getLeft());
    }

    Either<GeneralError, BookPreview> getBookPreviewLazy(UUID previewId) {
        return Try.of(() -> bookPreviewRepository.findById(previewId))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(optionalEntity -> optionalEntity.toEither(new BookError.BookPreviewEntityNotFound(previewId, null)));
    }

    Either<GeneralError, BookPreview> getBookPreviewEager(UUID previewId) {
        return Try.of(() -> bookPreviewRepository.getBookPreviewEager(previewId))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(optionalEntity -> optionalEntity.toEither(new BookError.BookPreviewEntityNotFound(previewId, null)));
    }

    @Transactional
    @Override
    public Either<GeneralError, BookResponse> createBook(BookCreationRequest bookCreationRequest) {
        Either<GeneralError, BookCreationUserData> fetchedAuthorData = userInfoService.getAndValidateAuthorData(bookCreationRequest.authorEmail());
        if (fetchedAuthorData.isLeft()) return Either.left(fetchedAuthorData.getLeft());
        if (bookPreviewRepository.existsByTitle(bookCreationRequest.title()))
            return Either.left(new BookError.DuplicateTitle());

        BookDetail detail = BookDetail.builder()
                .author(fetchedAuthorData.get().authorUsername())
                .authorId(fetchedAuthorData.get().authorId())
                .description(escapeHtml(bookCreationRequest.description()))
                .build();
        BookPreview preview = BookPreview.builder()
                .title(escapeHtml(bookCreationRequest.title()))
                .ratingCount(0)
                .averageRating(0)
                .bookState(BookState.UNKNOWN)
                .bookTags(bookCreationRequest.tags().isEmpty() ? List.of(BookTag.UNTAGGED) : bookCreationRequest.tags())
                .build();
        preview.setBookDetail(detail);
        bookDetailRepository.persist(detail);
        bookPreviewRepository.persist(preview);
        if(bookCreationRequest.coverImage() != null) bookImageHandler.upsertCoverImage(bookCreationRequest.title(), bookCreationRequest.coverImage());
        return Either.right(getLazyBookResponse(detail, preview));
    }

    @Transactional
    @Override
    public Either<GeneralError, BookResponse> updateBook(BookUpdateRequest bookUpdateRequest) {
        Either<GeneralError, BookDetail> detailE = getDetailAndValidateUUIDs(bookUpdateRequest.bookId(), bookUpdateRequest.authorEmail());

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
            detailE.get().setDescription(escapeHtml(bookUpdateRequest.description()));
            bookDetailRepository.update(detailE.get());
        }
        if (previewChanged) bookPreviewRepository.update(preview.get());
        if(bookUpdateRequest.coverImage() != null) bookImageHandler.upsertCoverImage(bookUpdateRequest.title(), bookUpdateRequest.coverImage());
        return Either.right(getEagerBookResponse(detailE.get(), preview.get()));
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
        return Either.right(getEagerBookResponse(detail.get(), preview.get()));
    }

    @Transactional
    @Override
    public Either<GeneralError, RatingResponse> upsertRating(RatingRequest ratingRequest) {
        Either<GeneralError, RatingUpsertData> fetchedE = userInfoService.getUsernameAndIdByEmail(ratingRequest.userEmail());
        if (fetchedE.isLeft()) return Either.left(fetchedE.getLeft());
        UUID userId = fetchedE.get().userId();
        Either<GeneralError, BookPreview> ePreview = getBookPreviewLazy(ratingRequest.bookId());
        if (ePreview.isLeft()) return Either.left(ePreview.getLeft());

        Optional<Rating> prevRating = ratingRepository.getRatingForBookAndUser(ratingRequest.bookId(), userId);
        BookPreview preview = ePreview.get();

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
            BookDetail detail = getBookDetail(ratingRequest.bookId()).get();
            userInfoService.updateRatingScore(new UserInfoScoreUpdateRequest(
                    userId,
                    detail.getAuthorId(),
                    ratingRequest.comment() != null
            ));
            preview.setAverageRating(
                    (preview.getAverageRating() * preview.getRatingCount() + ratingRequest.currentRating()) / (preview.getRatingCount() + 1)
            );
            preview.setRatingCount(preview.getRatingCount() + 1);
            ratingRepository.persist(Rating.builder()
                    .currentRating(ratingRequest.currentRating())
                    .userId(userId)
                    .comment(escapedComment)
                    .bookDetail(detail)
                    .username(fetchedE.get().username())
                    .build());
        }
        bookPreviewRepository.update(preview);
        return Either.right(new RatingResponse(fetchedE.get().username(), ratingRequest.currentRating(), escapedComment, LocalDateTime.now()));
    }

    Either<GeneralError, BookDetail> getDetailAndValidateUUIDs(UUID bookId, String authorEmail) {
        Either<GeneralError, BookDetail> bookDetailE = getBookDetail(bookId);
        if (bookDetailE.isLeft()) return Either.left(bookDetailE.getLeft());
        Either<GeneralError,UUID> fetchedAuthIdFromEmail = userInfoService.getUserInfoIdByEmail(authorEmail);
        if(fetchedAuthIdFromEmail.isLeft()) return Either.left(fetchedAuthIdFromEmail.getLeft());
        if (!bookDetailE.get().getAuthorId().equals(fetchedAuthIdFromEmail.get())) return Either.left(new BookError.UserNotAuthor(authorEmail));
        return bookDetailE;
    }

    @Override
    @Transactional
    public Either<GeneralError, ChapterPreviewResponse> createChapter(ChapterRequest chapterRequest) {
        Either<GeneralError, BookDetail> bookDetail = getDetailAndValidateUUIDs(chapterRequest.bookId(), chapterRequest.authorEmail());
        if (bookDetail.isLeft()) return Either.left(bookDetail.getLeft());
        Either<GeneralError, BookPreview> bookPreview = getBookPreviewLazy(chapterRequest.bookId());
        if (bookPreview.isLeft()) return Either.left(bookPreview.getLeft());
        Chapter chapterToSave = Chapter.builder()
                .text(chapterRequest.chapterText())
                .build();
        ChapterPreview previewToSave = ChapterPreview.builder()
                .title(chapterRequest.title() == null ? "No title" : escapeHtml(chapterRequest.title()))
                .number(chapterRequest.number())
                .bookDetail(bookDetail.get())
                .build();
        chapterToSave.setChapterPreview(previewToSave);
        bookPreview.get().increaseChapterCount(1);
        bookPreviewRepository.update(bookPreview.get());
        previewToSave = chapterPreviewRepository.persist(previewToSave);
        chapterRepository.persistAndFlush(chapterToSave);
        return Either.right(mapper.chapterPreviewToChapterPreviewResponse(previewToSave));
    }

    @Override
    @Transactional
    public Either<GeneralError, List<ChapterPreviewResponse>> createChapters(@NotEmpty List<ChapterRequest> chapterRequests) {
        Either<GeneralError, BookDetail> bookDetail = getDetailAndValidateUUIDs(chapterRequests.getFirst().bookId(), chapterRequests.getFirst().authorEmail());
        if (bookDetail.isLeft()) return Either.left(bookDetail.getLeft());
        Either<GeneralError, BookPreview> bookPreview = getBookPreviewLazy(chapterRequests.getFirst().bookId());
        if (bookPreview.isLeft()) return Either.left(bookPreview.getLeft());
        List<ChapterPreview> previewsToSave = new ArrayList<>();
        for (ChapterRequest chapterRequest : chapterRequests) {
            Chapter chapterToSave = Chapter.builder()
                    .text(chapterRequest.chapterText())
                    .build();
            ChapterPreview previewToSave = ChapterPreview.builder()
                    .title(chapterRequest.title() == null ? "No title" : escapeHtml(chapterRequest.title()))
                    .number(chapterRequest.number())
                    .bookDetail(bookDetail.get())
                    .build();
            chapterToSave.setChapterPreview(previewToSave);
            previewsToSave.add(chapterPreviewRepository.persist(previewToSave));
            chapterRepository.persist(chapterToSave);
        }
        bookPreview.get().increaseChapterCount(chapterRequests.size());
        bookPreviewRepository.update(bookPreview.get());
        chapterRepository.flush();
        return Either.right(mapper.chapterPreviewsToChapterPreviewResponseList(previewsToSave));
    }

    Either<GeneralError, UUID> getAuthorId(UUID bookId) {
        return Try.of(() -> bookDetailRepository.getAuthorId(bookId))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(optionalEntity -> optionalEntity.toEither(new BookError.BookDetailEntityNotFound(bookId)));
    }

    @Override
    @Transactional
    public Either<GeneralError, ContentRemovalSuccess> deleteChapter(ContentRemovalRequest removalRequest, int chapterNumber) {
        Either<GeneralError, UUID> validated = validateUUIDs(removalRequest.userEmail(), removalRequest.bookId());
        if(validated.isLeft()) return Either.left(validated.getLeft());
        Either<GeneralError, UUID> fetchedChapterId = Try.of(() -> chapterPreviewRepository.findChapterPreviewByBookIdAndNumber(validated.get(), chapterNumber))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(optionalEntity -> optionalEntity.toEither(new BookError.ChapterNotFound(removalRequest.bookId(), chapterNumber)));
        if (fetchedChapterId.isLeft()) return Either.left(fetchedChapterId.getLeft());
        chapterPreviewRepository.deleteById(fetchedChapterId.get());
        chapterRepository.deleteById(fetchedChapterId.get());
        return Either.right(new ContentRemovalSuccess(removalRequest.bookId(), removalRequest.userEmail()));
    }

    @Override
    @Transactional
    public Either<GeneralError, ContentRemovalSuccess> deleteBook(ContentRemovalRequest removalRequest) {
        Either<GeneralError, UUID> validated = validateUUIDs(removalRequest.userEmail(), removalRequest.bookId());
        if(validated.isLeft()) return Either.left(validated.getLeft());
        chapterRepository.deleteBook(removalRequest.bookId());
        chapterPreviewRepository.deleteBook(removalRequest.bookId());
        ratingRepository.deleteBook(removalRequest.bookId());
        bookPreviewRepository.deleteById(removalRequest.bookId());
        bookDetailRepository.deleteById(removalRequest.bookId());
        return Either.right(new ContentRemovalSuccess(removalRequest.bookId(), removalRequest.userEmail()));
    }

    @Override
    @Transactional
    public void updateAllForNewUsername(UUID forUserId, String newUsername) {
        List<BookDetail> toUpdateDetail = bookDetailRepository.getBookDetailByAuthorId(forUserId);
        for (BookDetail bookDetail : toUpdateDetail) {
            bookDetail.setAuthor(newUsername);
            bookDetailRepository.update(bookDetail);
        }
        List<Rating> toUpdateRating = ratingRepository.getRatingsByUserId(forUserId);
        for (Rating rating : toUpdateRating) {
            rating.setUsername(newUsername);
            ratingRepository.update(rating);
        }
    }

    Either<GeneralError, UUID> validateUUIDs(String forEmail, UUID bookId){
        Either<GeneralError,UUID> fetchedAuthIdFromEmail = userInfoService.getUserInfoIdByEmail(forEmail);
        if(fetchedAuthIdFromEmail.isLeft()) return Either.left(fetchedAuthIdFromEmail.getLeft());
        UUID userId = fetchedAuthIdFromEmail.get();
        Either<GeneralError, UUID> fetchedAuthorId = getAuthorId(bookId);
        if (fetchedAuthorId.isLeft()) return Either.left(fetchedAuthorId.getLeft());
        if (!fetchedAuthorId.get().equals(userId))
            return Either.left(new BookError.UserNotAuthor(forEmail));
        return Either.right(userId);
    }

    BookResponse getEagerBookResponse(BookDetail bookDetail, BookPreview bookPreviewEager) {
        return new BookResponse(
                bookPreviewEager.getTitle(),
                bookDetail.getAuthor(),
                bookDetail.getDescription(),
                bookPreviewEager.getChapterCount(),
                bookPreviewEager.getAverageRating(),
                bookPreviewEager.getRatingCount(),
                getChapterPreviewResponsesForBook(bookDetail.getId()),
                getRatingResponsesForBook(bookDetail.getId()),
                bookPreviewEager.getBookTags(),
                bookPreviewEager.getBookState(),
                bookImageHandler.fetchCoverImage(bookPreviewEager.getTitle())
        );
    }

    private static BookResponse getLazyBookResponse(BookDetail bookDetail, BookPreview bookPreviewEager) {
        return new BookResponse(
                bookPreviewEager.getTitle(),
                bookDetail.getAuthor(),
                bookDetail.getDescription(),
                bookPreviewEager.getChapterCount(),
                bookPreviewEager.getAverageRating(),
                bookPreviewEager.getRatingCount(),
                List.of(),
                List.of(),
                bookPreviewEager.getBookTags(),
                bookPreviewEager.getBookState(),
                null
        );
    }

    List<RatingResponse> getRatingResponsesForBook(UUID bookId) {
        return mapper.ratingsToRatingResponseList(getRatingsForBook(bookId));
    }

    List<Rating> getRatingsForBook(UUID bookId) {
        return ratingRepository.getAllRatingsForBook(bookId);
    }

    List<ChapterPreviewResponse> getChapterPreviewResponsesForBook(UUID bookId) {
        return mapper.chapterPreviewsToChapterPreviewResponseList(getChapterPreviewsForBook(bookId));
    }

    List<ChapterPreview> getChapterPreviewsForBook(UUID bookId) {
        return chapterPreviewRepository.getAllChapterPreviewsForBook(bookId);
    }

    @Override
    @Cacheable("bookPreviews")
    public List<BookPreviewResponse> getBookPreviewResponses() {
        return bookPreviewRepository.getAllBookPreviewsEager().stream().map(this::mapPreviewWithCover).toList();
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

    private BookPreviewResponse mapPreviewWithCover(BookPreview bookPreview) {
        return mapper.bookPreviewWithCoverToBookPreviewResponse(bookPreview, bookImageHandler.fetchCoverImage(bookPreview.getTitle()));
    }
}

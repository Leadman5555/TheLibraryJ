package org.library.thelibraryj.book.domain;

import com.blazebit.persistence.KeysetPage;
import com.blazebit.persistence.PagedList;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.library.thelibraryj.book.BookService;
import org.library.thelibraryj.book.dto.bookDto.BookCreationModel;
import org.library.thelibraryj.book.dto.bookDto.BookCreationRequest;
import org.library.thelibraryj.book.dto.bookDto.BookDetailResponse;
import org.library.thelibraryj.book.dto.bookDto.BookPreviewResponse;
import org.library.thelibraryj.book.dto.bookDto.BookResponse;
import org.library.thelibraryj.book.dto.bookDto.BookUpdateModel;
import org.library.thelibraryj.book.dto.bookDto.BookUpdateRequest;
import org.library.thelibraryj.book.dto.chapterDto.ChapterBatchRequest;
import org.library.thelibraryj.book.dto.chapterDto.ChapterPreviewResponse;
import org.library.thelibraryj.book.dto.chapterDto.ChapterResponse;
import org.library.thelibraryj.book.dto.chapterDto.ChapterUpsertResponse;
import org.library.thelibraryj.book.dto.pagingDto.PagedBookPreviewsResponse;
import org.library.thelibraryj.book.dto.pagingDto.PagedChapterPreviewResponse;
import org.library.thelibraryj.book.dto.ratingDto.RatingRequest;
import org.library.thelibraryj.book.dto.ratingDto.RatingResponse;
import org.library.thelibraryj.book.dto.sharedDto.ContentRemovalRequest;
import org.library.thelibraryj.book.dto.sharedDto.ContentRemovalSuccess;
import org.library.thelibraryj.infrastructure.error.errorTypes.BookError;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.error.errorTypes.ServiceError;
import org.library.thelibraryj.infrastructure.model.paging.PageInfo;
import org.library.thelibraryj.infrastructure.textParsers.fileParsers.TextParser;
import org.library.thelibraryj.infrastructure.textParsers.inputParsers.HtmlEscaper;
import org.library.thelibraryj.userInfo.UserInfoService;
import org.library.thelibraryj.userInfo.domain.BookCreationUserView;
import org.library.thelibraryj.userInfo.domain.RatingUpsertView;
import org.library.thelibraryj.userInfo.dto.request.UserInfoScoreUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
class BookServiceImpl implements BookService {
    private final BookDetailRepository bookDetailRepository;
    private final BookPreviewRepository bookPreviewRepository;
    private final RatingRepository ratingRepository;
    private final ChapterPreviewRepository chapterPreviewRepository;
    private final ChapterRepository chapterRepository;
    private final BookMapper mapper;
    private final BookImageHandler bookImageHandler;
    private final BookBlazeRepository bookBlazeRepository;
    private final BookProperties bookProperties;
    private UserInfoService userInfoService;
    private final Pattern chapterTitleMatcher = Pattern.compile("^([0-9])+(\\s-\\s(?=.*[a-zA-Z0-9]+)[a-zA-Z0-9\\s'_\"!.-]*)?$");
    private final TextParser textParser;
    private final HtmlEscaper htmlEscaper;

    @Autowired
    public void setUserInfoService(@Lazy UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    public BookServiceImpl(BookDetailRepository bookDetailRepository, BookPreviewRepository bookPreviewRepository, @Qualifier("bookMapperImpl") BookMapper mapper, RatingRepository ratingRepository, ChapterPreviewRepository chapterPreviewRepository, ChapterRepository chapterRepository, BookImageHandler bookImageHandler, BookBlazeRepository bookBlazeRepository, BookProperties bookProperties, TextParser textParser, HtmlEscaper htmlEscaper) {
        this.bookDetailRepository = bookDetailRepository;
        this.bookPreviewRepository = bookPreviewRepository;
        this.mapper = mapper;
        this.ratingRepository = ratingRepository;
        this.chapterPreviewRepository = chapterPreviewRepository;
        this.chapterRepository = chapterRepository;
        this.bookImageHandler = bookImageHandler;
        this.bookBlazeRepository = bookBlazeRepository;
        this.bookProperties = bookProperties;
        this.textParser = textParser;
        this.htmlEscaper = htmlEscaper;
    }

    @Override
    public Either<GeneralError, BookDetailResponse> getBookDetailResponse(UUID detailId) {
        Either<GeneralError, BookDetail> fetched = getBookDetail(detailId);
        if (fetched.isRight()) return Either.right(mapper.bookDetailToBookDetailResponse(fetched.get()));
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
        if (fetched.isRight())
            return Either.right(mapper.bookPreviewWithCoverToBookPreviewResponse(fetched.get(), bookImageHandler.fetchCoverImage(fetched.get().getTitle())));
        return Either.left(fetched.getLeft());
    }

    @Override
    public boolean checkIfBookExists(UUID bookId) {
        return bookPreviewRepository.existsById(bookId);
    }

    @Override
    public Set<BookPreviewResponse> getBookPreviewsByIds(Set<UUID> bookIds) {
        return bookPreviewRepository.getBookPreviewsEagerByIds(bookIds).stream().map(bookPreview -> mapper.bookPreviewWithCoverToBookPreviewResponse(bookPreview, bookImageHandler.fetchCoverImage(bookPreview.getTitle()))).collect(Collectors.toSet());
    }

    Either<GeneralError, BookPreview> getBookPreviewLazy(UUID previewId) {
        return Try.of(() -> bookPreviewRepository.findById(previewId))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(optionalEntity -> optionalEntity.toEither(new BookError.BookPreviewEntityNotFound(previewId.toString())));
    }

    Either<GeneralError, BookPreview> getBookPreviewEager(UUID previewId) {
        return Try.of(() -> bookPreviewRepository.getBookPreviewEager(previewId))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(optionalEntity -> optionalEntity.toEither(new BookError.BookPreviewEntityNotFound(previewId.toString())));
    }

    @Transactional
    @Override
    public Either<GeneralError, BookResponse> createBook(BookCreationRequest bookCreationRequest) {
        Either<GeneralError, BookCreationUserView> fetchedAuthorData = userInfoService.getAndValidateAuthorData(bookCreationRequest.authorEmail());
        if (fetchedAuthorData.isLeft()) return Either.left(fetchedAuthorData.getLeft());
        BookCreationModel model = bookCreationRequest.bookCreationModel();
        if (bookPreviewRepository.existsByTitle(model.title()))
            return Either.left(new BookError.DuplicateTitle("New entry"));
        String escapedDescription = htmlEscaper.escapeHtml(model.description());
        if (escapedDescription.length() > bookProperties.getDescription_max_length())
            escapedDescription = escapedDescription.substring(0, bookProperties.getDescription_max_length());
        BookDetail detail = BookDetail.builder()
                .author(fetchedAuthorData.get().getAuthorUsername())
                .authorId(fetchedAuthorData.get().getAuthorId())
                .description(escapedDescription)
                .build();
        BookPreview preview = BookPreview.builder()
                .title(htmlEscaper.escapeHtml(model.title()))
                .ratingCount(0)
                .averageRating(0)
                .bookState(BookState.UNKNOWN)
                .bookTags(model.tags().isEmpty() ? List.of(BookTag.UNTAGGED) : model.tags())
                .build();
        preview.setBookDetail(detail);
        bookDetailRepository.persist(detail);
        bookPreviewRepository.persist(preview);
        if (bookCreationRequest.coverImage() != null)
            return Either.right(getLazyBookResponse(detail, preview, bookImageHandler.upsertCoverImage(model.title(), bookCreationRequest.coverImage())));
        return Either.right(getLazyBookResponse(detail, preview, bookImageHandler.getDefaultImage()));
    }

    @Transactional
    @Override
    public Either<GeneralError, BookResponse> updateBook(BookUpdateRequest bookUpdateRequest) {
        Either<GeneralError, BookDetail> detailE = getDetailAndValidateUUIDs(bookUpdateRequest.bookId(), bookUpdateRequest.authorEmail());
        if (detailE.isLeft()) return Either.left(detailE.getLeft());
        boolean previewChanged = false;
        BookUpdateModel model = bookUpdateRequest.bookUpdateModel();

        Either<GeneralError, BookPreview> previewE;
        BookPreview preview;
        if (model.bookTags() != null) {
            previewE = getBookPreviewLazy(bookUpdateRequest.bookId());
            if (previewE.isLeft()) return Either.left(previewE.getLeft());
            preview = previewE.get();
            preview.setBookTags(model.bookTags());
            previewChanged = true;
        } else {
            previewE = getBookPreviewEager(bookUpdateRequest.bookId());
            if (previewE.isLeft()) return Either.left(previewE.getLeft());
            preview = previewE.get();
        }
        BookDetail detail = detailE.get();
        if (model.title() != null) {
            if (bookPreviewRepository.existsByTitle(model.title()))
                return Either.left(new BookError.DuplicateTitle(preview.getId().toString()));
            preview.setTitle(htmlEscaper.escapeHtml(model.title()));
            previewChanged = true;
        }
        if (model.state() != null) {
            preview.setBookState(model.state());
            previewChanged = true;
        }
        if (model.description() != null) {
            String escapedDescription = htmlEscaper.escapeHtml(model.description());
            if (escapedDescription.length() > bookProperties.getDescription_max_length())
                escapedDescription = escapedDescription.substring(0, bookProperties.getDescription_max_length());
            detail.setDescription(escapedDescription);
            detail = bookDetailRepository.update(detail);
        }
        if (previewChanged) preview = bookPreviewRepository.update(preview);

        if (model.resetCoverImage()) {
            bookImageHandler.removeExistingCoverImage(preview.getTitle());
            return Either.right(getLazyBookResponse(detail, preview, bookImageHandler.getDefaultImage()));
        } else if (bookUpdateRequest.coverImage() != null)
            return Either.right(getLazyBookResponse(detail, preview, bookImageHandler.upsertCoverImage(preview.getTitle(), bookUpdateRequest.coverImage())));
        return Either.right(getEagerBookResponse(detail, preview));
    }

    @Override
    public Either<GeneralError, BookResponse> getBook(String title) {
        Either<GeneralError, BookPreview> preview = Try.of(() -> bookPreviewRepository.findByTitleEager(title))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(optionalEntity -> optionalEntity.toEither(new BookError.BookPreviewEntityNotFound(title)));
        if (preview.isLeft()) return Either.left(preview.getLeft());
        Either<GeneralError, BookDetail> detail = getBookDetail(preview.get().getId());
        if (detail.isLeft()) return Either.left(detail.getLeft());
        return Either.right(getEagerBookResponse(detail.get(), preview.get()));
    }

    @Transactional
    @Override
    public Either<GeneralError, RatingResponse> upsertRating(RatingRequest ratingRequest) {
        Either<GeneralError, RatingUpsertView> fetchedE = userInfoService.getUsernameAndIdByEmail(ratingRequest.userEmail());
        if (fetchedE.isLeft()) return Either.left(fetchedE.getLeft());
        RatingUpsertView fetched = fetchedE.get();
        UUID userId = fetched.getUserId();
        Either<GeneralError, BookPreview> ePreview = getBookPreviewLazy(ratingRequest.bookId());
        if (ePreview.isLeft()) return Either.left(ePreview.getLeft());

        Optional<Rating> prevRating = ratingRepository.getRatingForBookAndUser(ratingRequest.bookId(), userId);
        BookPreview preview = ePreview.get();

        final String escapedComment = ratingRequest.comment() == null ? "" : htmlEscaper.escapeHtml(ratingRequest.comment());

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
                    ratingRequest.comment() != null && !ratingRequest.comment().isBlank()
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
                    .username(fetched.getUsername())
                    .build());
        }
        bookPreviewRepository.update(preview);
        return Either.right(new RatingResponse(fetched.getUsername(), ratingRequest.currentRating(), escapedComment, LocalDateTime.now()));
    }

    @Override
    public Either<GeneralError, ChapterResponse> getChapterByBookIdAndNumber(UUID bookId, int chapterNumber) {
        ChapterPreviewTitleView fetchedData = chapterPreviewRepository.findChapterPreviewTitleAndIdByBookIdAndNumber(bookId, chapterNumber);
        return Try.of(() -> chapterRepository.getChapterContentById(fetchedData.getId()))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(optionalEntity -> optionalEntity.toEither(new BookError.ChapterNotFound(bookId, chapterNumber)))
                .map(value -> mapper.chapterDataToChapterResponse(value, fetchedData.getTitle()));
    }

    Either<GeneralError, BookDetail> getDetailAndValidateUUIDs(UUID bookId, String authorEmail) {
        Either<GeneralError, BookDetail> bookDetailE = getBookDetail(bookId);
        if (bookDetailE.isLeft()) return Either.left(bookDetailE.getLeft());
        Either<GeneralError, UUID> fetchedAuthIdFromEmail = userInfoService.getUserInfoIdByEmail(authorEmail);
        if (fetchedAuthIdFromEmail.isLeft()) return Either.left(fetchedAuthIdFromEmail.getLeft());
        if (!bookDetailE.get().getAuthorId().equals(fetchedAuthIdFromEmail.get()))
            return Either.left(new BookError.UserNotAuthor(authorEmail, bookId));
        return bookDetailE;
    }

    private record ChapterDraft(String escapedTitle, int number, String escapedText) {
        ChapterDraft(String escapedText, ChapterUpsertResponse draft) {
            this(draft.title(), draft.number(), escapedText);
        }
    }

    private record FetchedChapterData(ChapterPreview chapterPreview, Chapter chapter) { }

    @Async
    @Transactional
    void createChapter(ChapterDraft chapterDraft, BookDetail bookDetailReference) {
        Chapter chapterToSave = Chapter.builder()
                .text(chapterDraft.escapedText)
                .build();
        ChapterPreview previewToSave = ChapterPreview.builder()
                .title(chapterDraft.escapedTitle)
                .number(chapterDraft.number)
                .bookDetail(bookDetailReference)
                .build();
        chapterToSave.setChapterPreview(chapterPreviewRepository.persist(previewToSave));
        chapterRepository.persist(chapterToSave);
    }

    @Async
    @Transactional
    void updateChapter(ChapterDraft chapterDraft, FetchedChapterData fetchedChapterData) {
        if (!fetchedChapterData.chapterPreview.getTitle().equals(chapterDraft.escapedTitle)) {
            fetchedChapterData.chapterPreview.setTitle(chapterDraft.escapedTitle);
            chapterPreviewRepository.update(fetchedChapterData.chapterPreview);
        }
        if (fetchedChapterData.chapter != null) {
            if (!fetchedChapterData.chapter.getText().equals(chapterDraft.escapedText)) {
                fetchedChapterData.chapter.setText(chapterDraft.escapedText);
                chapterRepository.update(fetchedChapterData.chapter);
            }
        } else {
            Chapter chapterToSave = Chapter.builder()
                    .text(chapterDraft.escapedText)
                    .build();
            chapterToSave.setChapterPreview(fetchedChapterData.chapterPreview);
            chapterRepository.persist(chapterToSave);
        }
    }

    private static PriorityQueue<FetchedChapterData> mergeChapterData(List<ChapterPreview> existingChapterPreviews, List<Chapter> existingChapters) {
        PriorityQueue<FetchedChapterData> fetchedChapterData = new PriorityQueue<>(Comparator.comparingInt(data -> data.chapterPreview.getNumber()));
        if (existingChapterPreviews.size() != existingChapters.size()) {
            for (int i = 0; i < existingChapterPreviews.size(); i++)
                if (!existingChapterPreviews.get(i).getId().equals(existingChapters.get(i).getId()))
                    fetchedChapterData.add(new FetchedChapterData(existingChapterPreviews.get(i), null));
                else
                    fetchedChapterData.add(new FetchedChapterData(existingChapterPreviews.get(i), existingChapters.get(i)));
        } else for (int i = 0; i < existingChapterPreviews.size(); i++)
            fetchedChapterData.add(new FetchedChapterData(existingChapterPreviews.get(i), existingChapters.get(i)));
        return fetchedChapterData;
    }

    @Async
    @Transactional
    void updateAndCreate(List<ChapterPreview> existingChapterPreviews, List<ChapterDraft> mergedList, BookDetail bookDetailRef) {
        List<Chapter> existingChapters = bookBlazeRepository.getSortedChapters(existingChapterPreviews.stream().map(ChapterPreview::getId).collect(Collectors.toSet()));
        PriorityQueue<FetchedChapterData> fetchedChapterData = mergeChapterData(existingChapterPreviews, existingChapters);
        int currentIndex = 0;
        while (!fetchedChapterData.isEmpty()) {
            FetchedChapterData fetched = fetchedChapterData.poll();
            while (fetched.chapterPreview.getNumber() != mergedList.get(currentIndex).number())
                createChapter(mergedList.get(currentIndex++), bookDetailRef);
            updateChapter(mergedList.get(currentIndex++), fetched);
        }
        for (; currentIndex < mergedList.size(); currentIndex++) createChapter(mergedList.get(currentIndex), bookDetailRef);
    }

    private Either<GeneralError, List<ChapterUpsertResponse>> validateAndParseChapterRequests(List<MultipartFile> chapterFiles, UUID forBookId, Set<Integer> numbers) {
        List<ChapterUpsertResponse> chapterEntries = new ArrayList<>();
        for (MultipartFile file : chapterFiles) {
            String name = file.getOriginalFilename();
            if (name == null || name.isBlank())
                return Either.left(new BookError.InvalidChapterTitleFormat(forBookId, ""));
            String fileName;
            try{
                fileName = Paths.get(file.getOriginalFilename()).getFileName().toString();
            }catch (InvalidPathException _){
                return Either.left(new BookError.InvalidChapterTitleFormat(forBookId, file.getOriginalFilename()));
            }
            fileName = fileName.substring(0, fileName.lastIndexOf('.'));
            if (!chapterTitleMatcher.matcher(fileName).matches())
                return Either.left(new BookError.InvalidChapterTitleFormat(forBookId, fileName));
            int numberEnd = fileName.indexOf(' ');
            if (numberEnd > bookProperties.getChapter_max_number())
                return Either.left(new BookError.InvalidChapterTitleFormat(forBookId, fileName));
            String chapterTitle;
            int chapterNumber;
            if (numberEnd != -1){
                chapterTitle = fileName.substring(numberEnd + 3);
                try {
                    chapterNumber = Integer.parseInt(fileName.substring(0, numberEnd));
                } catch (NumberFormatException _) {
                    return Either.left(new BookError.InvalidChapterTitleFormat(forBookId, fileName));
                }
            }else{
                try {
                    chapterNumber = Integer.parseInt(fileName);
                } catch (NumberFormatException _) {
                    return Either.left(new BookError.InvalidChapterTitleFormat(forBookId, fileName));
                }
                chapterTitle = "No title";
            }
            if (numbers.contains(chapterNumber))
                return Either.left(new BookError.DuplicateChapter(forBookId, chapterNumber));
            numbers.add(chapterNumber);
            chapterEntries.add(new ChapterUpsertResponse(chapterNumber, chapterTitle));
        }
        return Either.right(chapterEntries);
    }

    @Override
    @Transactional
    public Either<GeneralError, List<ChapterUpsertResponse>> upsertChapters(ChapterBatchRequest chapterBatchRequest) {
        Set<Integer> chapterNumbers = new HashSet<>();
        Either<GeneralError, List<ChapterUpsertResponse>> parsedFiles = validateAndParseChapterRequests(
                chapterBatchRequest.chapterFiles(),
                chapterBatchRequest.bookId(),
                chapterNumbers
        );
        if (parsedFiles.isLeft()) return Either.left(parsedFiles.getLeft());
        Either<GeneralError, BookDetail> bookDetailE = getDetailAndValidateUUIDs(chapterBatchRequest.bookId(), chapterBatchRequest.authorEmail());
        if (bookDetailE.isLeft()) return Either.left(bookDetailE.getLeft());
        BookDetail bookDetail = bookDetailE.get();

        List<ChapterDraft> mergedList = new ArrayList<>();
        List<ChapterUpsertResponse> chapterEntries = parsedFiles.get();
        List<MultipartFile> chapterFiles = chapterBatchRequest.chapterFiles();
        UUID bookId = chapterBatchRequest.bookId();
        for (int i = 0; i < chapterFiles.size(); i++) {
            String parsedText = textParser.parseTextFile(chapterFiles.get(i));
            if(parsedText == null) return Either.left(new BookError.MalformedChapterText(bookId, chapterEntries.get(i).number()));
            String escapedText = htmlEscaper.escapeHtml(parsedText);
            if (escapedText.length() > bookProperties.getChapter_max_length())
                return Either.left(new BookError.InvalidChapterTextLength(bookId, chapterEntries.get(i).number()));
            mergedList.add(new ChapterDraft(escapedText, chapterEntries.get(i)));
        }
        mergedList.sort(Comparator.comparingInt(ChapterDraft::number));

        List<ChapterPreview> existingChapterPreviews = bookBlazeRepository.getSortedChapterPreviews(bookId, chapterNumbers);
        if (!existingChapterPreviews.isEmpty()) updateAndCreate(existingChapterPreviews, mergedList, bookDetail);
        else for (ChapterDraft draft : mergedList) createChapter(draft, bookDetail);
        bookPreviewRepository.changeChapterCount(bookId, mergedList.size() - existingChapterPreviews.size());

        return Either.right(chapterEntries);
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
        if (validated.isLeft()) return Either.left(validated.getLeft());
        Either<GeneralError, UUID> fetchedChapterId = Try.of(() -> chapterPreviewRepository.findChapterPreviewIdByBookIdAndNumber(validated.get(), chapterNumber))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(optionalEntity -> optionalEntity.toEither(new BookError.ChapterNotFound(removalRequest.bookId(), chapterNumber)));
        if (fetchedChapterId.isLeft()) return Either.left(fetchedChapterId.getLeft());
        chapterPreviewRepository.deleteById(fetchedChapterId.get());
        bookPreviewRepository.decrementChapterCount(removalRequest.bookId());
        return Either.right(new ContentRemovalSuccess(removalRequest.bookId(), removalRequest.userEmail()));
    }

    @Override
    @Transactional
    public Either<GeneralError, ContentRemovalSuccess> deleteBook(ContentRemovalRequest removalRequest) {
        Either<GeneralError, UUID> validated = validateUUIDs(removalRequest.userEmail(), removalRequest.bookId());
        if (validated.isLeft()) return Either.left(validated.getLeft());
        chapterPreviewRepository.deleteBook(removalRequest.bookId());
        ratingRepository.deleteBook(removalRequest.bookId());
        bookDetailRepository.deleteById(removalRequest.bookId());
        userInfoService.removeBookFromFavouritesForAllUsers(removalRequest.bookId());
        log.info("Book {} has been deleted", removalRequest.bookId());
        return Either.right(new ContentRemovalSuccess(removalRequest.bookId(), removalRequest.userEmail()));
    }

    @Override
    @Transactional
    public void updateAllForNewUsername(UUID forUserId, String newUsername) {
        bookBlazeRepository.updateAllForNewUsername(forUserId, newUsername);
    }

    Either<GeneralError, UUID> validateUUIDs(String forEmail, UUID bookId) {
        Either<GeneralError, UUID> fetchedAuthIdFromEmail = userInfoService.getUserInfoIdByEmail(forEmail);
        if (fetchedAuthIdFromEmail.isLeft()) return Either.left(fetchedAuthIdFromEmail.getLeft());
        UUID userId = fetchedAuthIdFromEmail.get();
        Either<GeneralError, UUID> fetchedAuthorId = getAuthorId(bookId);
        if (fetchedAuthorId.isLeft()) return Either.left(fetchedAuthorId.getLeft());
        if (!fetchedAuthorId.get().equals(userId))
            return Either.left(new BookError.UserNotAuthor(forEmail, bookId));
        return Either.right(userId);
    }

    BookResponse getEagerBookResponse(BookDetail bookDetail, BookPreview bookPreviewEager) {
        return new BookResponse(
                bookPreviewEager.getId(),
                bookPreviewEager.getTitle(),
                bookDetail.getAuthor(),
                bookDetail.getDescription(),
                bookPreviewEager.getChapterCount(),
                bookPreviewEager.getAverageRating(),
                bookPreviewEager.getRatingCount(),
                bookPreviewEager.getBookTags(),
                bookPreviewEager.getBookState(),
                bookImageHandler.fetchCoverImage(bookPreviewEager.getTitle())
        );
    }

    private static BookResponse getLazyBookResponse(BookDetail bookDetail, BookPreview bookPreviewEager, byte[] coverImage) {
        return new BookResponse(
                bookPreviewEager.getId(),
                bookPreviewEager.getTitle(),
                bookDetail.getAuthor(),
                bookDetail.getDescription(),
                bookPreviewEager.getChapterCount(),
                bookPreviewEager.getAverageRating(),
                bookPreviewEager.getRatingCount(),
                bookPreviewEager.getBookTags(),
                bookPreviewEager.getBookState(),
                coverImage
        );
    }

    @Override
    public List<RatingResponse> getRatingResponsesForBook(UUID bookId) {
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
    @Cacheable(value = "bookPreviewsKeySet")
    public PagedBookPreviewsResponse getKeySetPagedBookPreviewResponses(KeysetPage lastPage, int page) {
        PagedList<BookPreview> pagedList = bookBlazeRepository.getKeySetPagedBookPreviewNext(lastPage, page);
        return new PagedBookPreviewsResponse(pagedList.stream().map(this::mapPreviewWithCover).toList(), new PageInfo(page, pagedList.getTotalPages(), pagedList.getKeysetPage()));
    }

    @Override
    @Cacheable(value = "bookPreviewsOffset", keyGenerator = "offsetKeyGenerator")
    public PagedBookPreviewsResponse getOffsetPagedBookPreviewResponses(int pageSize, int page) {
        PagedList<BookPreview> pagedList = bookBlazeRepository.getOffsetBookPreviewPaged(pageSize, page);
        return new PagedBookPreviewsResponse(pagedList.stream().map(this::mapPreviewWithCover).toList(), new PageInfo(page, pagedList.getTotalPages(), pagedList.getKeysetPage()));
    }

    @Override
    public PagedBookPreviewsResponse getByParamsOffsetPaged(String titleLike, Integer minChapters, Float minRating, BookState state, BookTag[] hasTags, Boolean sortAscByRating, int pageSize, int page) {
        PagedList<BookPreview> pagedList = bookBlazeRepository.getOffsetBookPreviewByParams(titleLike, minChapters, minRating, state, hasTags, sortAscByRating, pageSize, page);
        return new PagedBookPreviewsResponse(pagedList.stream().map(this::mapPreviewWithCover).toList(), new PageInfo(page, pagedList.getTotalPages(), pagedList.getKeysetPage()));
    }

    @Override
    public PagedBookPreviewsResponse getByParamsKeySetPaged(String titleLike, Integer minChapters, Float minRating, BookState state, BookTag[] hasTags, Boolean sortAscByRating, KeysetPage lastPage, int page) {
        PagedList<BookPreview> pagedList = bookBlazeRepository.getKeySetPagedBookPreviewByParams(titleLike, minChapters, minRating, state, hasTags, sortAscByRating, lastPage, page);
        return new PagedBookPreviewsResponse(pagedList.stream().map(this::mapPreviewWithCover).toList(), new PageInfo(page, pagedList.getTotalPages(), pagedList.getKeysetPage()));
    }

    @Override
    public List<BookPreviewResponse> getBookPreviewsByAuthor(String byUser) {
        return bookBlazeRepository.getAuthoredBookPreviews(byUser)
                .stream().map(this::mapPreviewWithCover).toList();
    }

    @Override
    @Cacheable(value = "chapterPreviewOffset", keyGenerator = "offsetKeyGenerator")
    public PagedChapterPreviewResponse getOffsetPagedChapterPreviewResponses(int pageSize, int page, UUID bookId) {
        PagedList<ChapterPreview> pagedList = bookBlazeRepository.getOffsetChapterPreviewPaged(pageSize, page, bookId);
        return new PagedChapterPreviewResponse(mapper.chapterPreviewsToChapterPreviewResponseList(pagedList), new PageInfo(page, pagedList.getTotalPages(), pagedList.getKeysetPage()), bookId);
    }

    @Override
    public PagedChapterPreviewResponse getKeySetPagedChapterPreviewResponses(KeysetPage lastPage, int page, UUID bookId) {
        PagedList<ChapterPreview> pagedList = bookBlazeRepository.getKeySetPagedChapterPreviewNext(lastPage, page, bookId);
        return new PagedChapterPreviewResponse(mapper.chapterPreviewsToChapterPreviewResponseList(pagedList), new PageInfo(page, pagedList.getTotalPages(), pagedList.getKeysetPage()), bookId);
    }


    @Override
    @CacheEvict(value = {"bookPreviewsOffset", "bookPreviewsKeySet", "chapterPreviewOffset"}, allEntries = true)
    @Scheduled(cron = "0 */${library.caching.bookPreviewTTL} * * * *")
    public void resetBookPreviewsCache() {
        bookPreviewRepository.flush();
        ratingRepository.flush();
        bookDetailRepository.flush();
        chapterPreviewRepository.flush();
        log.info("Book preview cache and repositories flushed.");
    }

    private BookPreviewResponse mapPreviewWithCover(BookPreview bookPreview) {
        return mapper.bookPreviewWithCoverToBookPreviewResponse(bookPreview, bookImageHandler.fetchCoverImage(bookPreview.getTitle()));
    }
}

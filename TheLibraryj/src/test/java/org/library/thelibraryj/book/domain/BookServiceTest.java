package org.library.thelibraryj.book.domain;

import io.vavr.control.Either;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.library.thelibraryj.book.dto.BookCreationRequest;
import org.library.thelibraryj.book.dto.BookDetailResponse;
import org.library.thelibraryj.book.dto.BookPreviewResponse;
import org.library.thelibraryj.book.dto.BookResponse;
import org.library.thelibraryj.book.dto.BookUpdateRequest;
import org.library.thelibraryj.book.dto.ChapterPreviewResponse;
import org.library.thelibraryj.book.dto.ChapterRequest;
import org.library.thelibraryj.book.dto.RatingRequest;
import org.library.thelibraryj.book.dto.RatingResponse;
import org.library.thelibraryj.infrastructure.error.errorTypes.BookError;
import org.library.thelibraryj.userInfo.UserInfoService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookDetailRepository bookDetailRepository;
    @Mock
    private BookPreviewRepository bookPreviewRepository;
    @Mock
    private ChapterPreviewRepository chapterPreviewRepository;
    @Mock
    private ChapterRepository chapterRepository;
    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private UserInfoService userInfoService;
    @Spy
    private BookMapper bookMapper = new BookMapperImpl();
    @InjectMocks
    private BookService bookService;
    private UUID bookId;
    private UUID authorId;
    private String title;
    private String author;
    private String description;
    private BookDetail bookDetail;
    private BookPreview bookPreview;
    private ChapterPreview chapterPreview;
    private Rating rating;

    @BeforeEach
    public void setUp() {
        bookId = UUID.randomUUID();
        authorId = UUID.randomUUID();
        title = "Sample title";
        author = "Sample author";
        description = "Sample description";
        bookDetail = BookDetail.builder()
                .authorId(authorId)
                .author(author)
                .description(description)
                .build();
        bookPreview = BookPreview.builder()
                .title(title)
                .bookState(BookState.UNKNOWN)
                .averageRating(0)
                .bookTags(List.of(BookTag.UNTAGGED))
                .ratingCount(0).build();
        bookPreview.setBookDetail(bookDetail);
        chapterPreview = new ChapterPreview(UUID.randomUUID(), 0L, Instant.now(), Instant.now(), title, 1, bookDetail);
        rating = Rating.builder()
                .userId(authorId)
                .currentRating(5)
                .bookDetail(bookDetail)
                .comment("")
                .updatedAt(Instant.now())
                .createdAt(Instant.now())
                .build();
    }

    @Test
    public void testGetBookDetailResponse() {
        List<ChapterPreview> chapterPreviews = List.of(chapterPreview);
        List<ChapterPreviewResponse> mappedChapters = bookMapper.chapterPreviewsToChapterPreviewResponseList(chapterPreviews);
        List<RatingResponse> mappedRatings = bookMapper.ratingsToRatingResponseList(List.of(rating));
        when(bookDetailRepository.findById(bookId)).thenReturn(Optional.ofNullable(bookDetail));
        when(chapterPreviewRepository.getAllChapterPreviewsForBook(bookId)).thenReturn(chapterPreviews);
        when(ratingRepository.getAllRatingsForBook(bookId)).thenReturn(List.of(rating));
        BookDetailResponse mapped = bookMapper.bookDetailToBookDetailResponse(bookDetail, mappedChapters, mappedRatings);
        BookDetailResponse fetched = bookService.getBookDetailResponse(bookId).get();
        Assertions.assertEquals(mapped, fetched);
        Assertions.assertEquals(mappedChapters, fetched.chapterPreviews());
        when(bookDetailRepository.findById(bookId)).thenReturn(Optional.empty());
        Assertions.assertEquals(new BookError.BookDetailEntityNotFound(bookId), bookService.getBookDetailResponse(bookId).getLeft());
    }

    @Test
    public void testGetBookPreviewResponses() {
        List<BookPreview> baseList = List.of(BookPreview.builder().title(title + '1').build(), BookPreview.builder().title(title + '2').build());
        when(bookPreviewRepository.getAllBookPreviewsEager()).thenReturn(baseList);
        List<BookPreviewResponse> fetchedList = bookService.getBookPreviewResponses();
        List<BookPreviewResponse> expectedList = baseList.stream().map(bookMapper::bookPreviewToBookPreviewResponse).toList();
        Assertions.assertEquals(expectedList, fetchedList);
    }

    @Test
    public void testCreateAndUpdateBook() {
        BookCreationRequest bookCreationRequest = new BookCreationRequest(
                title,
                authorId,
                description,
                List.of()
        );
        bookPreview.setBookDetail(bookDetail);
        when(bookPreviewRepository.existsByTitle(title)).thenReturn(false);
        when(userInfoService.getAuthorUsernameAndCheckValid(authorId)).thenReturn(Either.right(author));
        BookResponse response = bookService.createBook(bookCreationRequest).get();
        verify(bookDetailRepository).persist(bookDetail);
        verify(bookPreviewRepository).persist(bookPreview);
        Assertions.assertEquals(bookDetail.getAuthor(), response.author());
        Assertions.assertEquals(bookPreview.getTitle(), response.title());

        UUID bookId = UUID.randomUUID();
        when(bookDetailRepository.findById(bookId)).thenReturn(Optional.ofNullable(bookDetail));
        when(bookPreviewRepository.findById(bookId)).thenReturn(Optional.ofNullable(bookPreview));
        BookUpdateRequest bookUpdateRequest = new BookUpdateRequest(null, null, BookState.IN_PROGRESS, null, bookId);
        BookResponse updated = bookService.updateBook(bookUpdateRequest).get();
        verify(bookDetailRepository, never()).update(bookDetail);
        verify(bookPreviewRepository).update(bookPreview);
        Assertions.assertNotEquals(response.bookState(), updated.bookState());
        Assertions.assertEquals(response.title(), updated.title());
    }

    @Test
    public void testGetBook() {
        bookDetail.setId(bookId);
        bookPreview.setId(bookId);
        when(ratingRepository.getAllRatingsForBook(bookId))
                .thenReturn(List.of(rating));
        when(chapterPreviewRepository.getAllChapterPreviewsForBook(bookId))
                .thenReturn(List.of(chapterPreview));
        when(bookPreviewRepository.findByTitle(title)).thenReturn(Optional.ofNullable(bookPreview));
        when(bookDetailRepository.findById(bookId)).thenReturn(Optional.ofNullable(bookDetail));
        BookResponse response = bookService.getBook(title).get();
        Assertions.assertAll(
                () -> Assertions.assertEquals(author, response.author()),
                () -> Assertions.assertEquals(title, response.title()),
                () -> Assertions.assertEquals(1, response.chapterPreviews().size()),
                () -> Assertions.assertEquals(1, response.ratings().size())
        );
    }

    @Test
    public void testUpsertRating() {
        when(userInfoService.existsById(authorId)).thenReturn(true);
        when(bookPreviewRepository.findById(bookId)).thenReturn(Optional.ofNullable(bookPreview));
        when(bookDetailRepository.getReferenceById(bookId)).thenReturn(bookDetail);
        when(ratingRepository.getRatingForBookAndUser(bookId, authorId)).thenReturn(Optional.empty());
        RatingRequest request = new RatingRequest(rating.getUserId(), rating.getCurrentRating(), bookId, rating.getComment());
        bookService.upsertRating(request);
        when(ratingRepository.getRatingForBookAndUser(bookId,authorId)).thenReturn(Optional.ofNullable(rating));
        bookService.upsertRating(request);
        verify(ratingRepository, times(1)).update(rating);
        verify(bookPreviewRepository, times(2)).update(bookPreview);
    }

    @Test
    public void testCreateChapter(){
        when(bookDetailRepository.findById(bookId)).thenReturn(Optional.ofNullable(bookDetail));
        when(bookPreviewRepository.findById(bookId)).thenReturn(Optional.ofNullable(bookPreview));
        ChapterRequest request = new ChapterRequest(chapterPreview.getNumber(), chapterPreview.getTitle(), "text", bookId, authorId);
        Chapter chapter = Chapter.builder().text("text").build();
        chapter.setChapterPreview(chapterPreview);
        bookService.createChapter(request);
        verify(bookPreviewRepository).update(bookPreview);
        verify(chapterPreviewRepository).persist(any());
        verify(chapterRepository).persistAndFlush(any());
    }

}

package org.library.thelibraryj.book.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.library.thelibraryj.book.dto.BookDetailResponse;
import org.library.thelibraryj.book.dto.BookPreviewResponse;
import org.library.thelibraryj.book.dto.ChapterPreviewResponse;
import org.library.thelibraryj.infrastructure.error.errorTypes.BookError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@SpringBootTest
public class BookServiceTest {
    @Autowired
    private BookService bookService;
    @MockBean
    private BookDetailRepository bookDetailRepository;
    @MockBean
    private BookPreviewRepository bookPreviewRepository;
    @Autowired
    private BookMapper bookMapper;

    @Test
    public void testGetBookDetailResponse(){
        UUID detailId = UUID.randomUUID();
        List<ChapterPreview> chapterPreviews = List.of(ChapterPreview.builder().title("sample title")
                .updatedAt(Instant.now())
                .id(UUID.randomUUID())
                .build());
        BookDetail bookDetail = BookDetail.builder()
                .id(detailId)
                .author("Sample")
                .chapters(chapterPreviews)
                .build();
        when(bookDetailRepository.findById(detailId)).thenReturn(Optional.ofNullable(bookDetail));
        BookDetailResponse mapped = bookMapper.bookDetailToBookDetailResponse(bookDetail);
        BookDetailResponse fetched = bookService.getBookDetailResponse(detailId).get();
        Assertions.assertEquals(mapped, fetched);
        List<ChapterPreviewResponse> mapped2 = bookMapper.chapterPreviewsToChapterPreviewResponseList(chapterPreviews);
        Assertions.assertEquals(mapped2, fetched.chapterPreviews());
        when(bookDetailRepository.findById(detailId)).thenReturn(Optional.empty());
        Assertions.assertEquals(new BookError.BookDetailEntityNotFound(detailId), bookService.getBookDetailResponse(detailId).getLeft());
    }

    @Test
    public void testGetBookPreviewResponsesAndCacheResponse(){
        List<BookPreview> baseList = List.of(BookPreview.builder().title("title1").build(), BookPreview.builder().title("title2").build());
        when(bookPreviewRepository.getAllBookPreviewsEager()).thenReturn(baseList);
        List<BookPreviewResponse> fetchedList = bookService.getBookPreviewResponses();
        List<BookPreviewResponse> expectedList = baseList.stream().map(bookMapper::bookPreviewToBookPreviewResponse).toList();
        Assertions.assertEquals(expectedList, fetchedList);
        fetchedList = bookService.getBookPreviewResponses();
        Assertions.assertEquals(expectedList, fetchedList);
        verify(bookPreviewRepository, times(1)).getAllBookPreviewsEager();
    }
}

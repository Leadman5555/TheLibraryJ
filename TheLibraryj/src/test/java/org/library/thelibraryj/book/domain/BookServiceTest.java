package org.library.thelibraryj.book.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.library.thelibraryj.book.dto.BookDetailResponse;
import org.library.thelibraryj.book.dto.BookPreviewResponse;
import org.library.thelibraryj.infrastructure.error.BookError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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
        BookDetail bookDetail = BookDetail.builder()
                .id(detailId)
                .author("Sample")
                .build();
        when(bookDetailRepository.findById(detailId)).thenReturn(Optional.ofNullable(bookDetail));
        BookDetailResponse mapped = bookMapper.bookDetailToBookDetailResponse(bookDetail);
        Assertions.assertEquals(mapped, bookService.getBookDetailResponse(detailId).get());
        when(bookDetailRepository.findById(detailId)).thenReturn(Optional.empty());
        Assertions.assertEquals(new BookError.BookPreviewEntityNotFound(detailId), bookService.getBookDetailResponse(detailId).getLeft());
    }

    @Test
    public void testGetBookPreviewsAndCacheResponse(){
        List<BookPreview> baseList = List.of(BookPreview.builder().title("title1").build(), BookPreview.builder().title("title2").build());
        when(bookPreviewRepository.findAll()).thenReturn(baseList);
        List<BookPreviewResponse> fetchedList = bookService.getBookPreviews();
        List<BookPreviewResponse> expectedList = baseList.stream().map(bookMapper::bookPreviewToBookPreviewResponse).toList();
        Assertions.assertEquals(expectedList, fetchedList);
        fetchedList = bookService.getBookPreviews();
        Assertions.assertEquals(expectedList, fetchedList);
        verify(bookPreviewRepository, times(1)).findAll();
    }
}

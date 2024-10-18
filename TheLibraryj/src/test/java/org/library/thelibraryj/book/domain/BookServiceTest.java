package org.library.thelibraryj.book.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.library.thelibraryj.book.dto.BookDetailsResponse;
import org.library.thelibraryj.infrastructure.error.BookError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

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
    public void testGetBookDetail(){
        UUID detailId = UUID.randomUUID();
        BookDetail bookDetail = BookDetail.builder()
                .id(detailId)
                .bookPreviewId(UUID.randomUUID())
                .author("Sample")
                .build();
        when(bookDetailRepository.findById(detailId)).thenReturn(Optional.ofNullable(bookDetail));
        BookDetailsResponse mapped = bookMapper.bookDetailsToBookDetailsResponse(bookDetail);
        Assertions.assertEquals(mapped, bookService.getBookDetail(detailId).get());
        when(bookDetailRepository.findById(detailId)).thenReturn(Optional.empty());
        Assertions.assertEquals(new BookError.BookEntityNotFound(detailId, null), bookService.getBookDetail(detailId).getLeft());
    }
}

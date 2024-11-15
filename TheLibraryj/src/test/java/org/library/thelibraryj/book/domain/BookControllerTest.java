package org.library.thelibraryj.book.domain;

import io.vavr.control.Either;
import org.junit.jupiter.api.Test;
import org.library.thelibraryj.TestProperties;
import org.library.thelibraryj.book.BookService;
import org.library.thelibraryj.book.dto.BookDetailResponse;
import org.library.thelibraryj.infrastructure.error.errorTypes.BookError;
import org.library.thelibraryj.authentication.jwtAuth.domain.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtFilter disabledFilter;

    @MockBean
    private BookService bookService;

    private static final String URL_BASE = TestProperties.BASE_URL;

    private static final String ENDPOINT = URL_BASE;

    private static final UUID bookId = UUID.randomUUID();

    @Test
    public void testGetBookPreviews() throws Exception {
        mockMvc.perform(get(ENDPOINT + "/na/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(bookService).getBookPreviewResponses();
    }

    @Test
    public void testGetBookDetail() throws Exception {
        BookDetailResponse detailResponse = new BookDetailResponse("author", UUID.randomUUID(), "des", null, null);
        when(bookService.getBookDetailResponse(bookId)).thenReturn(Either.right(detailResponse));

        mockMvc.perform(get(ENDPOINT + "/na/books/" + bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{'author': 'author'}"));

        UUID invalidId = UUID.randomUUID();
        when(bookService.getBookDetailResponse(invalidId)).thenReturn(Either.left(new BookError.BookDetailEntityNotFound(invalidId)));

        mockMvc.perform(get(ENDPOINT + "/na/books/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message", is("Book data (details) missing. Id: " + invalidId)));
    }
}

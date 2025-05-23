package org.library.thelibraryj.book.domain;

import io.vavr.control.Either;
import org.junit.jupiter.api.Test;
import org.library.thelibraryj.EndpointsRegistry;
import org.library.thelibraryj.book.BookService;
import org.library.thelibraryj.book.dto.bookDto.response.BookDetailResponse;
import org.library.thelibraryj.book.dto.pagingDto.PreviewKeySet;
import org.library.thelibraryj.book.dto.pagingDto.PreviewKeySetPage;
import org.library.thelibraryj.infrastructure.error.errorTypes.BookError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {BookController.class, PublicBookController.class})
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(roles = "ADMIN")
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    private static final String URL_BASE = EndpointsRegistry.PUBLIC_BOOKS_URL;
    private static final UUID bookId = UUID.randomUUID();

    @Test
    public void testGetBookPreviews() throws Exception {
        int page = 0;
        int pageSize = 1;
        mockMvc.perform(get(URL_BASE)
                        .param("page", String.valueOf(page))
                        .param("pageSize", String.valueOf(pageSize))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(bookService).getOffsetPagedBookPreviewResponses(pageSize, page);
        final UUID id1 = UUID.randomUUID();
        final UUID id2 = UUID.randomUUID();
        PreviewKeySetPage keySetPage = new PreviewKeySetPage(0, 3, new PreviewKeySet(1, id1), new PreviewKeySet(0, id2), List.of());
        String jsonKeyset = "{\n" +
                "  \"firstResult\": 0,\n" +
                "  \"maxResults\": 3,\n" +
                "  \"lowest\": {\n" +
                "    \"tuple\": [0, \"" + id2 + "\"]\n" +
                "  },\n" +
                "  \"highest\": {\n" +
                "    \"tuple\": [1, \"" + id1 + "\"]\n" +
                "  },\n" +
                "  \"keysets\": []\n" +
                "}";

        mockMvc.perform(post(URL_BASE)
                        .param("page", String.valueOf(page))
                        .content(jsonKeyset)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(bookService).getKeySetPagedBookPreviewResponses(keySetPage, page);
    }

    @Test
    public void testGetBookDetail() throws Exception {
        BookDetailResponse detailResponse = new BookDetailResponse("author", "des");
        when(bookService.getBookDetailResponse(bookId)).thenReturn(Either.right(detailResponse));

        mockMvc.perform(get(URL_BASE + '/' + bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{'author': 'author'}"));

        UUID invalidId = UUID.randomUUID();
        when(bookService.getBookDetailResponse(invalidId)).thenReturn(Either.left(new BookError.BookDetailEntityNotFound(invalidId)));

        mockMvc.perform(get(URL_BASE + '/' + invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorDetails.message", is("Book data (details) missing. Id: " + invalidId)));
    }
}

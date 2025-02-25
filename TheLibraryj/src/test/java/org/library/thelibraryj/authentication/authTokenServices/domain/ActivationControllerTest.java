package org.library.thelibraryj.authentication.authTokenServices.domain;

import io.vavr.control.Either;
import org.junit.jupiter.api.Test;
import org.library.thelibraryj.TestProperties;
import org.library.thelibraryj.authentication.jwtAuth.domain.JwtFilter;
import org.library.thelibraryj.authentication.authTokenServices.ActivationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ActivationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ActivationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtFilter disabledFilter;

    @MockBean
    private ActivationService activationService;

    private static final String URL_BASE = TestProperties.BASE_URL;

    private static final String ENDPOINT =  URL_BASE + "/na/auth/activation";

    @Test
    public void testConsumeActivationToken() throws Exception {
        UUID tokenId = UUID.randomUUID();
        when(activationService.consumeActivationToken(tokenId)).thenReturn(Either.right(true));
        mockMvc.perform(patch(ENDPOINT)
                        .param("tokenId", tokenId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}

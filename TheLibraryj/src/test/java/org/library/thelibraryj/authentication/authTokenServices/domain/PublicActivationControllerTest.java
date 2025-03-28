package org.library.thelibraryj.authentication.authTokenServices.domain;

import io.vavr.control.Either;
import org.junit.jupiter.api.Test;
import org.library.thelibraryj.TestProperties;
import org.library.thelibraryj.authentication.jwtAuth.domain.JwtFilter;
import org.library.thelibraryj.authentication.authTokenServices.ActivationTokenService;
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

@WebMvcTest(value = PublicActivationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PublicActivationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtFilter disabledFilter;

    @MockBean
    private ActivationTokenService activationTokenService;

    private static final String URL_BASE = TestProperties.BASE_AUTH_FREE_URL;

    @Test
    public void testConsumeActivationToken() throws Exception {
        UUID tokenId = UUID.randomUUID();
        when(activationTokenService.consumeActivationToken(tokenId)).thenReturn(Either.right(true));
        mockMvc.perform(patch(URL_BASE + "/auth/activation")
                        .param("tokenId", tokenId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}

package org.library.thelibraryj.authentication.tokenServices.domain;

import io.vavr.control.Either;
import org.junit.jupiter.api.Test;
import org.library.thelibraryj.TestProperties;
import org.library.thelibraryj.authentication.tokenServices.dto.password.PasswordResetRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PasswordResetController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PasswordControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private static final String URL_BASE = TestProperties.BASE_URL;

    private static final String ENDPOINT =  URL_BASE + "/auth/password";

    @MockBean
    private PasswordResetServiceImpl passwordResetService;

    @Test
    public void testConsumePasswordResetToken() throws Exception {
        UUID tokenId = UUID.randomUUID();
        char[] newPassword = "pass".toCharArray();
        when(passwordResetService.consumePasswordResetToken(any(PasswordResetRequest.class))).thenReturn(Either.right(true));
        mockMvc.perform(patch(ENDPOINT)
                        .content("{\"tokenId\":\"" + tokenId + "\",\"newPassword\":\"" + new String(newPassword) + "\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}

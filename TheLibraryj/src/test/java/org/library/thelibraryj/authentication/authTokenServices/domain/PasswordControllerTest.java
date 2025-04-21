package org.library.thelibraryj.authentication.authTokenServices.domain;

import io.vavr.control.Either;
import org.junit.jupiter.api.Test;
import org.library.thelibraryj.TestProperties;
import org.library.thelibraryj.authentication.authTokenServices.dto.password.PasswordResetRequest;
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

@WebMvcTest(PublicPasswordResetController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PasswordControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private static final String URL_BASE = TestProperties.BASE_AUTH_FREE_URL + "/auth/password";

    @MockBean
    private PasswordResetTokenServiceImpl passwordResetService;

    @Test
    public void testConsumePasswordResetToken() throws Exception {
        UUID tokenId = UUID.randomUUID();
        char[] newPassword = "P@ssword123".toCharArray();
        when(passwordResetService.consumePasswordResetToken(any(PasswordResetRequest.class))).thenReturn(Either.right(true));
        mockMvc.perform(patch(URL_BASE)
                        .content("{\"tokenId\":\"" + tokenId + "\",\"newPassword\":\"" + new String(newPassword) + "\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}

package org.library.thelibraryj.userInfo.domain;

import io.vavr.control.Either;
import org.junit.jupiter.api.Test;
import org.library.thelibraryj.TestProperties;
import org.library.thelibraryj.infrastructure.error.errorTypes.UserInfoError;
import org.library.thelibraryj.userInfo.UserInfoService;
import org.library.thelibraryj.userInfo.dto.UserInfoRankUpdateRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoResponse;
import org.library.thelibraryj.userInfo.dto.UserInfoUsernameUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserInfoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserInfoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserInfoService userInfoService;

    private static final String URL_BASE = TestProperties.BASE_URL;

    private static final String ENDPOINT =  URL_BASE + "/user";

    private static final UUID userId = UUID.randomUUID();

    @Test
    public void testGetUserInfoResponseById() throws Exception {
        when(userInfoService.getUserInfoResponseById(userId)).thenReturn(Either.right(new UserInfoResponse(userId, null, null, null,0, null)));
        mockMvc.perform(get(ENDPOINT + '/' + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userInfoService).getUserInfoResponseById(userId);
    }

    @Test
    public void testUpdateUserInfoRank() throws Exception {
        UserInfoRankUpdateRequest request = new UserInfoRankUpdateRequest(userId, 10);
        UserInfoResponse response = new UserInfoResponse(userId, UUID.randomUUID(), "sample", "email@sample.com",10, Instant.now());
        when(userInfoService.updateRank(request)).thenReturn(Either.right(response));

        mockMvc.perform(patch(ENDPOINT + "/profile/rank")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"" + userId + "\",\"rankChange\":\"10\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{'rank': 10}"));

        UUID invalidId = UUID.randomUUID();
        UserInfoRankUpdateRequest request2 = new UserInfoRankUpdateRequest(invalidId, -10);
        when(userInfoService.updateRank(request2)).thenReturn(Either.left(new UserInfoError.UserInfoEntityNotFound(invalidId)));

        mockMvc.perform(patch(ENDPOINT + "/profile/rank")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"" + invalidId + "\",\"rankChange\":\"-10\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message", is("User data (details) missing. Id: " + invalidId)));
    }

    @Test
    public void testUpdateUserInfoUsername() throws Exception {
        UserInfoUsernameUpdateRequest request = new UserInfoUsernameUpdateRequest(userId, "new username");
        when(userInfoService.updateUserInfoUsername(request)).thenReturn(Either.left(new UserInfoError.UsernameNotUnique()));
        mockMvc.perform(patch(ENDPOINT + "/profile/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"" + userId + "\",\"username\":\"new username\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.message", is("Username not unique")));
    }
}

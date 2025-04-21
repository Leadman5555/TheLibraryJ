package org.library.thelibraryj.userInfo.domain;

import io.vavr.control.Either;
import org.junit.jupiter.api.Test;
import org.library.thelibraryj.TestProperties;
import org.library.thelibraryj.infrastructure.error.errorTypes.UserInfoError;
import org.library.thelibraryj.userInfo.UserInfoService;
import org.library.thelibraryj.userInfo.dto.request.UserInfoRankUpdateRequest;
import org.library.thelibraryj.userInfo.dto.request.UserInfoUsernameUpdateRequest;
import org.library.thelibraryj.userInfo.dto.response.UserProfileResponse;
import org.library.thelibraryj.userInfo.dto.response.UserRankUpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {UserInfoController.class, PublicUserInfoController.class})
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(roles = "ADMIN")
public class UserInfoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserInfoService userInfoService;

    private static final String URL_BASE = TestProperties.BASE_URL + "/user";
    private static final String BASE_AUTH_FREE_URL = TestProperties.BASE_AUTH_FREE_URL + "/user";

    private static final String email = "email@sample.com";


    @Test
    public void testGetUserInfoResponseByUsername() throws Exception {
        final String username = "username";
        when(userInfoService.getUserProfileByUsername(username)).thenReturn(Either.right(new UserProfileResponse(username, email,1, 1,null, (short) 0, null, LocalDateTime.now(),LocalDateTime.now())));
        mockMvc.perform(get( BASE_AUTH_FREE_URL+ '/' + username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userInfoService).getUserProfileByUsername(username);
    }

    @Test
    public void testForceUpdateUserInfoRank() throws Exception {
        UserInfoRankUpdateRequest request = new UserInfoRankUpdateRequest(email, 10);
        UserRankUpdateResponse response = new UserRankUpdateResponse(10, 0, (short) 0);
        when(userInfoService.forceUpdateRank(request)).thenReturn(Either.right(response));

        mockMvc.perform(patch(URL_BASE + "/profile/rank/force")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"rankChange\":\"10\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{'newRank': 10}"));
        final String invalidEmail = "invalid@gmail.com";
        UserInfoRankUpdateRequest request2 = new UserInfoRankUpdateRequest(invalidEmail, -10);
        when(userInfoService.forceUpdateRank(request2)).thenReturn(Either.left(new UserInfoError.UserInfoEntityNotFoundByEmail(invalidEmail)));

        mockMvc.perform(patch(URL_BASE + "/profile/rank/force")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + invalidEmail + "\",\"rankChange\":\"-10\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorDetails.message", is("User data (details) missing. Email: " + invalidEmail)));
    }

    @Test
    public void testUpdateUserInfoRank() throws Exception {
        UserRankUpdateResponse response = new UserRankUpdateResponse(10, 0, (short) 0);
        when(userInfoService.updateRank(email)).thenReturn(Either.right(response));

        mockMvc.perform(patch(URL_BASE + "/profile/rank/" + email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{'newRank': 10}"));

        int missingScore = 10;
        when(userInfoService.updateRank(email)).thenReturn(Either.left(new UserInfoError.UserNotEligibleForRankIncrease(email, missingScore)));

        mockMvc.perform(patch(URL_BASE + "/profile/rank/" + email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorDetails.message", is("User not eligible for rank increase. Missing score: " + missingScore)));
    }

    @Test
    public void testUpdateUserInfoUsername() throws Exception {
        UserInfoUsernameUpdateRequest request = new UserInfoUsernameUpdateRequest(email, "newUsername");
        when(userInfoService.updateUserInfoUsername(request)).thenReturn(Either.left(new UserInfoError.UsernameNotUnique(email)));
        mockMvc.perform(patch(URL_BASE + "/profile/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"username\":\"newUsername\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorDetails.message", is("The chosen username is not unique.")));
    }
}

package com.playground.backend.controller;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.playground.backend.dto.ChangePasswordRequest;
import com.playground.backend.dto.ChangePasswordResult;
import com.playground.backend.dto.ChangePasswordStatus;
import com.playground.backend.dto.LoginRequest;
import com.playground.backend.dto.LoginResult;
import com.playground.backend.dto.LoginStatus;
import com.playground.backend.dto.SignupRequest;
import com.playground.backend.dto.UpdateUserRequest;
import com.playground.backend.dto.UpdateUserResult;
import com.playground.backend.dto.UpdateUserStatus;
import com.playground.backend.dto.UserResponse;
import com.playground.backend.service.AuthService;

import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    void register_DuplicateUsername_ReturnsConflict() throws Exception {
        SignupRequest request = new SignupRequest("name", "username", "password");

        when(authService.register(request))
                .thenReturn(false);

        mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request))
        )
        .andExpect(status().isConflict());

        verify(authService).register(request);
    }

    @Test
    void register_ValidRequest_ReturnsIsCreated() throws Exception {
        SignupRequest request = new SignupRequest("name", "username", "password");

        when(authService.register(request))
                .thenReturn(true);

        mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request))
        )
        .andExpect(status().isCreated())
        .andExpect(content().string(""));

        verify(authService).register(request);
    }

    @Test
    void login_ValidRequest_ReturnsOk() throws Exception {
        LoginRequest request = new LoginRequest("username", "password");

        when(authService.login(request))
                .thenReturn(new LoginResult(LoginStatus.SUCCESS, "token"));

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(content().string("token"));

        verify(authService).login(request);
    }

    @Test
    void login_InvalidUsername_ReturnsUserNotFound() throws Exception {
        LoginRequest request = new LoginRequest("username", "password");

        when(authService.login(request))
                .thenReturn(new LoginResult(LoginStatus.USER_NOT_FOUND, null));

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request))
        )
        .andExpect(status().isNotFound())
        .andExpect(content().string(""));

        verify(authService).login(request);
    }

    @Test
    void login_InvalidPassword_ReturnsInvalidPassword() throws Exception {
        LoginRequest request = new LoginRequest("username", "password");

        when(authService.login(request))
                .thenReturn(new LoginResult(LoginStatus.INVALID_PASSWORD, null));

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request))
        )
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(""));

        verify(authService).login(request);
    }

    @Test
    void getCurrentUser_ReturnsCurrentUser() throws Exception {
        when(authService.getCurrentUserResponse())
                .thenReturn(new UserResponse(1L, "username", "name"));

        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("username"))
                .andExpect(jsonPath("$.name").value("name"));

        verify(authService).getCurrentUserResponse();
    }

    @Test
    void updateCurrentUser_DuplicateUsername_ReturnsConflict() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest("name", "username");

        when(authService.updateCurrentUser(request))
                .thenReturn(new UpdateUserResult(UpdateUserStatus.USERNAME_ALREADY_EXISTS, null));

        mockMvc.perform(
                patch("/auth/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request))   
        )
        .andExpect(status().isConflict())
        .andExpect(content().string(""));

        verify(authService).updateCurrentUser(request);
    }

    @Test
    void updateCurrentUser_ValidRequest_ReturnsOk() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest("name", "username");

        when(authService.updateCurrentUser(request))
                .thenReturn(new UpdateUserResult(UpdateUserStatus.SUCCESS, new UserResponse(1L, "username", "name")));

        mockMvc.perform(
                patch("/auth/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request))   
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.name").value("name"))
        .andExpect(jsonPath("$.username").value("username"));

        verify(authService).updateCurrentUser(request);
    }

    @Test
    void updatePassword_InvalidCurrentPassword_ReturnsBadRequest() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("currentPassword", "newPassword");

        when(authService.changePassword(request))
                .thenReturn(new ChangePasswordResult(ChangePasswordStatus.CURRENT_PASSWORD_INCORRECT));

        mockMvc.perform(
                patch("/auth/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request))    
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().string(""));

        verify(authService).changePassword(request);
    }

    @Test
    void updatePassword_ValidCurrentPassword_ReturnsOk() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("currentPassword", "newPassword");

        when(authService.changePassword(request))
                .thenReturn(new ChangePasswordResult(ChangePasswordStatus.SUCCESS));

        mockMvc.perform(
                patch("/auth/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request))    
        )
        .andExpect(status().isOk())
        .andExpect(content().string(""));

        verify(authService).changePassword(request);
    }
}
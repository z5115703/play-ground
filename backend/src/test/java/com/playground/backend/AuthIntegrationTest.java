package com.playground.backend;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.notNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.playground.backend.dto.ChangePasswordRequest;
import com.playground.backend.dto.LoginRequest;
import com.playground.backend.dto.SignupRequest;
import com.playground.backend.dto.UpdateUserRequest;
import com.playground.backend.model.User;
import com.playground.backend.repository.UserRepository;
import com.playground.backend.util.JwtUtil;

import tools.jackson.databind.json.JsonMapper;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void register_ValidRequest_CreatesUser() throws Exception {
        SignupRequest request = new SignupRequest("name", "username", "password");

        mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request))
        )
        .andExpect(status().isCreated());

        User user = userRepository.findByUsername("username");

        Assertions.assertNotNull(user);
        Assertions.assertEquals("username", user.getUsername());
        Assertions.assertEquals("name", user.getName());

    }

    @Test
    void register_DuplicateUsername_ReturnsConflict() throws Exception {
        User user = new User("name", "username", "password");
        userRepository.save(user);

        SignupRequest request = new SignupRequest("name", "username", "password");

        mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request))
        )
        .andExpect(status().isConflict());

        User originalUser = userRepository.findByUsername("username");

        Assertions.assertNotNull(originalUser);
        Assertions.assertEquals(user.getName(), originalUser.getName());
        Assertions.assertEquals(1, userRepository.count());
    }

    @Test
    void login_ValidRequest_ReturnsOk() throws Exception {
        User user = new User("name", "username", "password");
        userRepository.save(user);

        LoginRequest request = new LoginRequest("username", "password");

        MvcResult result = mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andReturn();
        
        String token = result.getResponse().getContentAsString();

        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.isBlank());
    }

    @Test
    void login_InvalidUsername_ReturnsNotFound() throws Exception {
        LoginRequest request = new LoginRequest("wrongUsername", "password");

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request))
        )
        .andExpect(status().isNotFound())
        .andExpect(content().string(""));
    }

    @Test
    void login_InvalidPassword_ReturnsUnauthorized() throws Exception {
        User user = new User("name", "username", passwordEncoder.encode("password"));
        userRepository.save(user);

        LoginRequest request = new LoginRequest("username", "wrongPassword");

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request))
        )
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(""));
    }

    @Test
    void getCurrentUser_ValidJwt_ReturnsOk() throws Exception {
        User user = new User("name", "username", "password");
        userRepository.save(user);

        String token = JwtUtil.generateToken(String.valueOf(user.getId()));
        
        mockMvc.perform(
            get("/auth/me")
                    .header("Authorization", "Bearer " + token)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.username").value("username"))
        .andExpect(jsonPath("$.name").value("name"));
    }

    @Test
    void getCurrentUser_InvalidUwt_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(
            get("/auth/me")
                    .header("Authorization", "Bearer invalid-token")
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void updateCurrentUser_ValidRequest_ReturnsOk() throws Exception {
        User user = new User("name", "username", "password");
        userRepository.save(user);

        String token = JwtUtil.generateToken(String.valueOf(user.getId()));

        UpdateUserRequest request = new UpdateUserRequest("newName", "newUsername");

        mockMvc.perform(
                patch("/auth/me")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk());
        
        User updatedUser = userRepository.findByUsername(request.username());

        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals(request.name(), updatedUser.getName());
        Assertions.assertEquals(request.username(), updatedUser.getUsername());
        Assertions.assertNull(userRepository.findByUsername(user.getUsername()));
    }

    @Test
    void updateCurrentUser_DuplicateUsername_ReturnsConflict() throws Exception {
        User user = new User("name", "username", "password");
        userRepository.save(user);
        User duplicateUser = new User("duplicateName", "duplicateUser", "duplicatePassword");
        userRepository.save(duplicateUser);

        String token = JwtUtil.generateToken(String.valueOf(user.getId()));

        UpdateUserRequest request = new UpdateUserRequest("newName", "duplicateUser");

        mockMvc.perform(
                patch("/auth/me")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request))
        )
        .andExpect(status().isConflict());
        
        User updatedUser = userRepository.findByUsername(user.getUsername());
        Assertions.assertEquals(user.getName(), updatedUser.getName());
        Assertions.assertEquals(user.getUsername(), updatedUser.getUsername());

    }

    @Test
    void updatePassword_ValidCurrentPassword_ReturnsOk() throws Exception {
        User user = new User("name", "username", passwordEncoder.encode("password"));
        userRepository.save(user);

        String token = JwtUtil.generateToken(String.valueOf(user.getId()));

        ChangePasswordRequest request = new ChangePasswordRequest("password", "newPassword");

        mockMvc.perform(
                patch("/auth/me/password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk());

        User updatedUser = userRepository.findByUsername(user.getUsername());

        Assertions.assertTrue(passwordEncoder.matches("newPassword", updatedUser.getPassword()));
    }

    @Test
    void updatePassword_InvalidCurrentPassword_ReturnsBadRequest() throws Exception {
        User user = new User("name", "username", passwordEncoder.encode("password"));
        userRepository.save(user);

        String token = JwtUtil.generateToken(String.valueOf(user.getId()));

        ChangePasswordRequest request = new ChangePasswordRequest("wrongPassword", "newPassword");

        mockMvc.perform(
                patch("/auth/me/password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request))
        )
        .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findByUsername(user.getUsername());

        Assertions.assertTrue(passwordEncoder.matches("password", updatedUser.getPassword()));
    }
}
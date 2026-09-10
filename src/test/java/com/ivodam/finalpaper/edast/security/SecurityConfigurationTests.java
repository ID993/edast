package com.ivodam.finalpaper.edast.security;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigurationTests {

  private static final String RESPONSE_URL =
      "/responses/00000000-0000-0000-0000-000000000000";

  @Autowired private MockMvc mockMvc;

  @Test
  void loginPageIsPublicAndRendersCsrfToken() throws Exception {
    mockMvc.perform(get("/login"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("name=\"_csrf\"")));
  }

  @Test
  void loginWithoutCsrfIsForbidden() throws Exception {
    mockMvc
        .perform(post("/login")
                     .param("username", "nobody@example.invalid")
                     .param("password", "wrong"))
        .andExpect(status().isForbidden());
  }

  @Test
  void loginWithCsrfReachesAuthenticationFlow() throws Exception {
    mockMvc
        .perform(post("/login")
                     .with(csrf())
                     .param("username", "nobody@example.invalid")
                     .param("password", "wrong"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/login?error"));
  }

  @Test
  void logoutWithoutCsrfIsForbidden() throws Exception {
    mockMvc.perform(post("/logout")).andExpect(status().isForbidden());
  }

  @Test
  void logoutWithCsrfRedirectsHome() throws Exception {
    mockMvc.perform(post("/logout").with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"));
  }

  @Test
  void regularUserCannotAccessAdminRoutes() throws Exception {
    mockMvc
        .perform(get("/users/security-probe")
                     .with(user("user@example.test").roles("USER")))
        .andExpect(status().isForbidden());
  }

  @Test
  void multipartPostWithoutCsrfIsForbidden() throws Exception {
    mockMvc.perform(multipart(RESPONSE_URL).file(testFile()))
        .andExpect(status().isForbidden());
  }

  @Test
  void multipartPostWithCsrfReachesAuthenticationCheck() throws Exception {
    mockMvc.perform(multipart(RESPONSE_URL).file(testFile()).with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrlPattern("**/login"));
  }

  @Test
  void missingPublicResourceReturnsNotFound() throws Exception {
    mockMvc.perform(get("/styles/definitely-does-not-exist.css"))
        .andExpect(status().isNotFound());
  }

  private MockMultipartFile testFile() {
    return new MockMultipartFile("files", "test.txt", "text/plain",
                                 "test".getBytes(StandardCharsets.UTF_8));
  }

  @Test
  void regularUserCannotOpenAdminRegistrationPage() throws Exception {
    mockMvc
        .perform(get("/admin/register")
                     .with(user("user@example.test").roles("USER")))
        .andExpect(status().isForbidden());
  }

  @Test
  void regularUserCannotSubmitAdminRegistration() throws Exception {
    mockMvc
        .perform(post("/admin/register")
                     .with(user("user@example.test").roles("USER"))
                     .with(csrf())
                     .param("job", "Archivist"))
        .andExpect(status().isForbidden());
  }
}
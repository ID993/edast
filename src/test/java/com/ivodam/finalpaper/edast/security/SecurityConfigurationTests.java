package com.ivodam.finalpaper.edast.security;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigurationTests {

  private static final String RESPONSE_URL =
      "/responses/00000000-0000-0000-0000-000000000000";

  @Autowired private MockMvc mockMvc;
  @Autowired private RequestMappingHandlerMapping handlerMapping;

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

  @Test
  void accountCannotBeReadByChangingUrlId() throws Exception {
    mockMvc
        .perform(get("/account/00000000-0000-0000-0000-000000000000")
                     .with(user("user@example.test").roles("USER")))
        .andExpect(status().isNotFound());
  }

  @Test
  void accountCannotBeDeletedWithGet() throws Exception {
    mockMvc
        .perform(get("/account/delete/00000000-0000-0000-0000-000000000000")
                     .with(user("user@example.test").roles("USER")))
        .andExpect(status().isNotFound());
  }

  @Test
  void employeeCannotDeleteAccount() throws Exception {
    mockMvc
        .perform(post("/account/delete")
                     .with(user("employee@example.test").roles("EMPLOYEE"))
                     .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest(name = "{index}: {0}")
  @ValueSource(
      strings = {"/users/add-admin/00000000-0000-0000-0000-000000000000",
                 "/users/add-employee/00000000-0000-0000-0000-000000000000",
                 "/admin/account/delete/00000000-0000-0000-0000-000000000000"})
  void adminMutationsRequirePostCsrfAndAdminRole(String path) throws Exception {

    mockMvc.perform(get(path).with(user("admin@example.test").roles("ADMIN")))
        .andExpect(status().isMethodNotAllowed());

    mockMvc.perform(post(path).with(user("admin@example.test").roles("ADMIN")))
        .andExpect(status().isForbidden());

    mockMvc
        .perform(post(path)
                     .with(user("user@example.test").roles("USER"))
                     .with(csrf()))
        .andExpect(status().isForbidden());

    mockMvc
        .perform(post(path)
                     .with(user("admin@example.test").roles("ADMIN"))
                     .with(csrf()))
        .andExpect(status().isNotFound());
  }

  @Test
  void workRequestCreationRequiresUserRole() throws Exception {
    mockMvc
        .perform(get("/work-requests")
                     .with(user("employee@example.test").roles("EMPLOYEE")))
        .andExpect(status().isForbidden());

    mockMvc
        .perform(post("/work-requests")
                     .with(user("employee@example.test").roles("EMPLOYEE"))
                     .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @Test
  void workRequestAdminListRequiresAdminRole() throws Exception {
    mockMvc
        .perform(get("/work-requests/all")
                     .with(user("user@example.test").roles("USER")))
        .andExpect(status().isForbidden());
  }

  @Test
  void workRequestDeletionRequiresPostCsrfAndUserRole() throws Exception {

    var path = "/work-requests/delete/"
               + "00000000-0000-0000-0000-000000000000";

    mockMvc.perform(get(path).with(user("user@example.test").roles("USER")))
        .andExpect(status().isMethodNotAllowed());

    mockMvc.perform(post(path).with(user("user@example.test").roles("USER")))
        .andExpect(status().isForbidden());

    mockMvc
        .perform(post(path)
                     .with(user("admin@example.test").roles("ADMIN"))
                     .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @Test
  void ownWorkRequestListDoesNotAcceptUserId() throws Exception {
    mockMvc
        .perform(get("/user-work-requests/all/"
                     + "00000000-0000-0000-0000-000000000000")
                     .with(user("user@example.test").roles("USER")))
        .andExpect(status().isNotFound());

    mockMvc
        .perform(get("/user-work-requests/all")
                     .with(user("employee@example.test").roles("EMPLOYEE")))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest(name = "{index}: {0}")
  @ValueSource(strings = {"bdm", "education", "cadastral", "special"})
  void requestRoutesRequireExpectedRolesMethodsAndCsrf(String requestType)
      throws Exception {

    var requestBase = "/" + requestType + "-requests";
    var userList = "/user-" + requestType + "-requests/all";
    var requestId = "00000000-0000-0000-0000-000000000000";
    var deletePath = requestBase + "/delete/" + requestId;

    var userListMethods =
        handlerMapping.getHandlerMethods()
            .keySet()
            .stream()
            .filter(mapping -> mapping.getPatternValues().contains(userList))
            .flatMap(
                mapping -> mapping.getMethodsCondition().getMethods().stream())
            .toList();

    assertThat(userListMethods).containsExactly(RequestMethod.GET);

    mockMvc
        .perform(get(requestBase)
                     .with(user("employee@example.test").roles("EMPLOYEE")))
        .andExpect(status().isForbidden());

    mockMvc
        .perform(post(requestBase)
                     .with(user("employee@example.test").roles("EMPLOYEE"))
                     .with(csrf()))
        .andExpect(status().isForbidden());

    mockMvc
        .perform(get(requestBase + "/all")
                     .with(user("user@example.test").roles("USER")))
        .andExpect(status().isForbidden());

    mockMvc
        .perform(get(userList + "/" + requestId)
                     .with(user("user@example.test").roles("USER")))
        .andExpect(status().isNotFound());

    mockMvc
        .perform(
            get(userList).with(user("employee@example.test").roles("EMPLOYEE")))
        .andExpect(status().isForbidden());

    mockMvc
        .perform(get("/search-" + requestType + "-requests")
                     .with(user("employee@example.test").roles("EMPLOYEE")))
        .andExpect(status().isForbidden());

    mockMvc
        .perform(get(deletePath).with(user("user@example.test").roles("USER")))
        .andExpect(status().isMethodNotAllowed());

    mockMvc
        .perform(post(deletePath).with(user("user@example.test").roles("USER")))
        .andExpect(status().isForbidden());

    mockMvc
        .perform(post(deletePath)
                     .with(user("admin@example.test").roles("ADMIN"))
                     .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest(name = "{index}: {0}")
  @ValueSource(
      strings = {"/requests/all", "/requests/assigned",
                 "/requests/assigned/bdm", "/requests/assigned/work",
                 "/requests/assigned/education", "/requests/assigned/cadastral",
                 "/requests/assigned/special", "/requests/assigned/unread",
                 "/requests/reassign/00000000-0000-0000-0000-000000000000",
                 "/search", "/search-bdm", "/search-work", "/search-education",
                 "/search-cadastral", "/search-special"})
  void regularUserCannotAccessRegistryWorkflow(String path) throws Exception {
    mockMvc.perform(get(path).with(user("user@example.test").roles("USER")))
        .andExpect(status().isForbidden());
  }

  @Test
  void employeeCannotUseAdministrativeRegistryActions() throws Exception {
    var reassignPath =
        "/requests/reassign/00000000-0000-0000-0000-000000000000";

    mockMvc
        .perform(get("/requests/all")
                     .with(user("employee@example.test").roles("EMPLOYEE")))
        .andExpect(status().isForbidden());

    mockMvc
        .perform(get(reassignPath)
                     .with(user("employee@example.test").roles("EMPLOYEE")))
        .andExpect(status().isForbidden());

    mockMvc
        .perform(post(reassignPath)
                     .with(user("employee@example.test").roles("EMPLOYEE"))
                     .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest(name = "{index}: removed route {0}")
  @ValueSource(
      strings = {"/requests/all/00000000-0000-0000-0000-000000000000",
                 "/requests/all/bdm/00000000-0000-0000-0000-000000000000",
                 "/requests/all/work/00000000-0000-0000-0000-000000000000",
                 "/requests/all/education/00000000-0000-0000-0000-000000000000",
                 "/requests/all/cadastral/00000000-0000-0000-0000-000000000000",
                 "/requests/all/special/00000000-0000-0000-0000-000000000000",
                 "/requests/unread/00000000-0000-0000-0000-000000000000"})
  void employeeIdentityCannotBeSelectedThroughUrl(String oldPath)
      throws Exception {

    mockMvc
        .perform(
            get(oldPath).with(user("employee@example.test").roles("EMPLOYEE")))
        .andExpect(status().isNotFound());
  }

  @ParameterizedTest(name = "{index}: GET mapping {0}")
  @ValueSource(strings = {"/requests/assigned", "/requests/assigned/bdm",
                          "/requests/assigned/work",
                          "/requests/assigned/education",
                          "/requests/assigned/cadastral",
                          "/requests/assigned/special",
                          "/requests/assigned/unread"})
  void assignedQueueRoutesAreGetMappings(String path) {
    var methods =
        handlerMapping.getHandlerMethods()
            .keySet()
            .stream()
            .filter(mapping -> mapping.getPatternValues().contains(path))
            .flatMap(
                mapping -> mapping.getMethodsCondition().getMethods().stream())
            .toList();

    assertThat(methods).containsExactly(RequestMethod.GET);
  }
}
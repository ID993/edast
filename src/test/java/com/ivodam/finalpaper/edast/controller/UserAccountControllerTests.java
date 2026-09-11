package com.ivodam.finalpaper.edast.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ivodam.finalpaper.edast.dto.AccountUpdateDto;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.service.UserService;
import com.ivodam.finalpaper.edast.utility.PasswordHandler;
import jakarta.validation.Validation;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;

@ExtendWith(MockitoExtension.class)
class UserAccountControllerTests {

  @Mock private UserService userService;

  @Mock private PasswordHandler passwordHandler;

  @Mock private Authentication authentication;

  @InjectMocks private UserAccountController userAccountController;

  @Test
  void editPageUsesAuthenticatedAccount() throws Exception {

    var existingUser = new User();
    existingUser.setName("Owner User");
    existingUser.setEmail("owner@example.test");
    existingUser.setJobTitle("Software Engineer");

    when(authentication.getName()).thenReturn("owner@example.test");
    when(userService.findByEmail("owner@example.test"))
        .thenReturn(existingUser);

    var model = new ExtendedModelMap();

    var view = userAccountController.editUser(authentication, model);

    assertThat(view).isEqualTo("account/account-edit");
    assertThat(model.get("email")).isEqualTo("owner@example.test");
    assertThat(model.get("user")).isInstanceOf(AccountUpdateDto.class);

    var account = (AccountUpdateDto)model.get("user");

    assertThat(account.getName()).isEqualTo("Owner User");
    assertThat(account.getJobTitle()).isEqualTo("Software Engineer");
  }

  @Test
  void invalidAccountUpdateReturnsFormWithoutSaving() throws Exception {

    var submittedUser = AccountUpdateDto.builder()
                            .name("")
                            .jobTitle("Software Engineer")
                            .build();

    var result = new BeanPropertyBindingResult(submittedUser, "user");

    result.rejectValue("name", "name.invalid", "Name is required");

    when(authentication.getName()).thenReturn("owner@example.test");

    var model = new ExtendedModelMap();

    var view = userAccountController.updateUser(authentication, submittedUser,
                                                result, model);

    assertThat(view).isEqualTo("account/account-edit");
    assertThat(model.get("email")).isEqualTo("owner@example.test");

    verify(userService, never())
        .updateOwnAccount(anyString(), any(AccountUpdateDto.class));
  }

  @Test
  void validAccountUpdateUsesAuthenticatedIdentity() throws Exception {

    var submittedUser = AccountUpdateDto.builder()
                            .name("Updated User")
                            .jobTitle("Software Engineer")
                            .build();

    var result = new BeanPropertyBindingResult(submittedUser, "user");

    when(authentication.getName()).thenReturn("owner@example.test");

    var view = userAccountController.updateUser(authentication, submittedUser,
                                                result, new ExtendedModelMap());

    assertThat(view).isEqualTo("redirect:/account");

    verify(userService).updateOwnAccount("owner@example.test", submittedUser);
  }

  @Test
  void accountUpdateRejectsBlankName() {

    var submittedUser = AccountUpdateDto.builder().name(" ").build();

    var validator = Validation.buildDefaultValidatorFactory().getValidator();

    var violations = validator.validate(submittedUser);

    assertThat(violations)
        .anySatisfy(violation
                    -> assertThat(violation.getPropertyPath().toString())
                           .isEqualTo("name"));
  }

  @Test
  void accountEditTemplateUsesRelativeFormBindings() throws Exception {

    var resource = new ClassPathResource("templates/account/account-edit.html");

    String template;

    try (var inputStream = resource.getInputStream()) {
      template = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    assertThat(template)
        .contains("th:object=\"${user}\"")
        .doesNotContain("*{user.")
        .doesNotContain("th:field=\"*{email}\"");
  }
}
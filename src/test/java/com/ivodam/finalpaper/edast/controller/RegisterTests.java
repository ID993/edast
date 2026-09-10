package com.ivodam.finalpaper.edast.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.ivodam.finalpaper.edast.dto.UserDto;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;

@ExtendWith(MockitoExtension.class)
class RegisterTests {

  @Mock private UserService userService;

  @InjectMocks private Register register;

  @Test
  void adminRegistrationPageDoesNotProvideDefaultPassword() {
    var model = new ExtendedModelMap();

    var view = register.adminGetRegister(model);

    assertThat(view).isEqualTo("admin/admin-register");
    assertThat(model.get("user"))
        .isInstanceOfSatisfying(
            UserDto.class, user -> assertThat(user.getPassword()).isNull());
    assertThat(model.get("jobs")).isNotNull();

    verifyNoInteractions(userService);
  }

  @Test
  void adminRegistrationRejectsPasswordMismatch() throws AppException {

    var submittedUser = validStaffRegistration();
    submittedUser.setConfirmPassword("DifferentPassword1");

    var result = new BeanPropertyBindingResult(submittedUser, "user");
    var model = new ExtendedModelMap();

    when(userService.existsByEmail(submittedUser.getEmail())).thenReturn(false);

    var view =
        register.adminPostRegister(submittedUser, result, "Archivist", model);

    assertThat(view).isEqualTo("admin/admin-register");
    assertThat(result.hasFieldErrors("confirmPassword")).isTrue();
    assertThat(model.get("jobs")).isNotNull();

    verify(userService, never())
        .createStaffUser(any(UserDto.class), any(Enums.Roles.class),
                         anyString());
  }

  @Test
  void adminRegistrationRejectsNonStaffRole() throws AppException {

    var submittedUser = validStaffRegistration();
    submittedUser.setRole(Enums.Roles.ROLE_USER);

    var result = new BeanPropertyBindingResult(submittedUser, "user");
    var model = new ExtendedModelMap();

    when(userService.existsByEmail(submittedUser.getEmail())).thenReturn(false);

    var view =
        register.adminPostRegister(submittedUser, result, "Archivist", model);

    assertThat(view).isEqualTo("admin/admin-register");
    assertThat(result.hasFieldErrors("role")).isTrue();

    verify(userService, never())
        .createStaffUser(any(UserDto.class), any(Enums.Roles.class),
                         anyString());
  }

  @Test
  void validAdminRegistrationCreatesStaffUser() throws AppException {

    var submittedUser = validStaffRegistration();
    var result = new BeanPropertyBindingResult(submittedUser, "user");
    var model = new ExtendedModelMap();

    var savedUser = new User();
    savedUser.setRole(Enums.Roles.ROLE_EMPLOYEE);

    when(userService.existsByEmail(submittedUser.getEmail())).thenReturn(false);
    when(userService.createStaffUser(submittedUser, Enums.Roles.ROLE_EMPLOYEE,
                                     "Archivist"))
        .thenReturn(savedUser);

    var view =
        register.adminPostRegister(submittedUser, result, "Archivist", model);

    assertThat(view).isEqualTo("redirect:/users?type=" +
                               Enums.Roles.ROLE_EMPLOYEE.getDisplayName());

    verify(userService)
        .createStaffUser(submittedUser, Enums.Roles.ROLE_EMPLOYEE, "Archivist");
  }

  private UserDto validStaffRegistration() {
    return UserDto.builder()
        .name("Staff User")
        .email("staff@example.test")
        .password("ValidPassword1")
        .confirmPassword("ValidPassword1")
        .role(Enums.Roles.ROLE_EMPLOYEE)
        .build();
  }
}
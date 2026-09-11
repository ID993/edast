package com.ivodam.finalpaper.edast.utility;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.ivodam.finalpaper.edast.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class PasswordHandlerTests {

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private PasswordHandler passwordHandler;

  @ParameterizedTest
  @ValueSource(strings = {"Password1", "A1bcdefg", "LongPassword123"})
  void acceptsValidPasswords(String password) {
    assertThat(passwordHandler.isValid(password)).isTrue();
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"Aa1", "password1", "PASSWORD1", "Password"})
  void rejectsInvalidPasswords(String password) {
    assertThat(passwordHandler.isValid(password)).isFalse();
  }

  @Test
  void verifiesOldPasswordUsingPasswordEncoder() {
    var user = new User();
    user.setPassword("encoded-password");

    when(passwordEncoder.matches("OldPassword1", "encoded-password"))
        .thenReturn(true);

    assertThat(passwordHandler.checkIfValidOldPassword(user, "OldPassword1"))
        .isTrue();

    verify(passwordEncoder).matches("OldPassword1", "encoded-password");
  }

  @Test
  void rejectsIncompleteOldPasswordInput() {
    var userWithoutPassword = new User();

    assertThat(passwordHandler.checkIfValidOldPassword(null, "Password1"))
        .isFalse();
    assertThat(passwordHandler.checkIfValidOldPassword(userWithoutPassword,
                                                       "Password1"))
        .isFalse();
    assertThat(passwordHandler.checkIfValidOldPassword(new User(), null))
        .isFalse();

    verifyNoInteractions(passwordEncoder);
  }

  @Test
  void comparesPasswordConfirmationSafely() {
    assertThat(passwordHandler.checkConfirmPassword("Password1", "Password1"))
        .isTrue();
    assertThat(passwordHandler.checkConfirmPassword("Password1", "Different1"))
        .isFalse();
    assertThat(passwordHandler.checkConfirmPassword(null, null)).isFalse();
  }
}
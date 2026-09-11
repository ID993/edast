package com.ivodam.finalpaper.edast.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ivodam.finalpaper.edast.dto.AccountUpdateDto;
import com.ivodam.finalpaper.edast.dto.UserDto;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.mappers.UserMapper;
import com.ivodam.finalpaper.edast.repository.UserRepository;
import com.ivodam.finalpaper.edast.utility.PasswordHandler;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private UserMapper userMapper;

  @Mock private PasswordHandler passwordHandler;

  @InjectMocks private UserService userService;

  @Test
  void userCanUpdateOwnNameAndOccupationWithoutChangingProtectedFields()
      throws AppException {

    var originalId = UUID.randomUUID();

    var existingUser = new User();
    existingUser.setId(originalId);
    existingUser.setName("Old name");
    existingUser.setEmail("owner@example.test");
    existingUser.setPassword("encoded-password");
    existingUser.setJoinDate("01.01.2024.");
    existingUser.setRole(Enums.Roles.ROLE_USER);
    existingUser.setJobTitle("Old job");

    var submittedUser = AccountUpdateDto.builder()
                            .name(" New name ")
                            .jobTitle(" New job ")
                            .build();

    when(userRepository.findByEmail("owner@example.test"))
        .thenReturn(Optional.of(existingUser));

    userService.updateOwnAccount("owner@example.test", submittedUser);

    assertThat(existingUser.getId()).isEqualTo(originalId);
    assertThat(existingUser.getName()).isEqualTo("New name");
    assertThat(existingUser.getEmail()).isEqualTo("owner@example.test");
    assertThat(existingUser.getPassword()).isEqualTo("encoded-password");
    assertThat(existingUser.getJoinDate()).isEqualTo("01.01.2024.");
    assertThat(existingUser.getRole()).isEqualTo(Enums.Roles.ROLE_USER);
    assertThat(existingUser.getJobTitle()).isEqualTo("New job");

    verify(userRepository).save(existingUser);
  }

  @Test
  void employeeCannotChangeOwnJobTitle() throws AppException {

    var employee = new User();
    employee.setEmail("employee@example.test");
    employee.setName("Old name");
    employee.setRole(Enums.Roles.ROLE_EMPLOYEE);
    employee.setJobTitle("Archivist");

    var submittedUser = AccountUpdateDto.builder()
                            .name("New name")
                            .jobTitle("Administrator")
                            .build();

    when(userRepository.findByEmail("employee@example.test"))
        .thenReturn(Optional.of(employee));

    userService.updateOwnAccount("employee@example.test", submittedUser);

    assertThat(employee.getName()).isEqualTo("New name");
    assertThat(employee.getJobTitle()).isEqualTo("Archivist");

    verify(userRepository).save(employee);
  }

  @Test
  void administratorCannotChangeOwnJobTitle() throws AppException {

    var administrator = new User();
    administrator.setEmail("admin@example.test");
    administrator.setName("Old admin name");
    administrator.setRole(Enums.Roles.ROLE_ADMIN);
    administrator.setJobTitle("Administrator");

    var submittedUser = AccountUpdateDto.builder()
                            .name("New admin name")
                            .jobTitle("Archivist")
                            .build();

    when(userRepository.findByEmail("admin@example.test"))
        .thenReturn(Optional.of(administrator));

    userService.updateOwnAccount("admin@example.test", submittedUser);

    assertThat(administrator.getName()).isEqualTo("New admin name");
    assertThat(administrator.getJobTitle()).isEqualTo("Administrator");

    verify(userRepository).save(administrator);
  }

  @Test
  void emptyOccupationUsesDefaultValue() throws AppException {

    var user = new User();
    user.setEmail("owner@example.test");
    user.setRole(Enums.Roles.ROLE_USER);
    user.setJobTitle("Software Engineer");

    var submittedUser =
        AccountUpdateDto.builder().name("Owner User").jobTitle("   ").build();

    when(userRepository.findByEmail("owner@example.test"))
        .thenReturn(Optional.of(user));

    userService.updateOwnAccount("owner@example.test", submittedUser);

    assertThat(user.getJobTitle()).isEqualTo("Unemployed");

    verify(userRepository).save(user);
  }

  @Test
  void publicRegistrationPreservesOccupationAndIgnoresProtectedDtoFields() {
    var submittedUser = UserDto.builder()
                            .id(UUID.randomUUID())
                            .name("New User")
                            .email("new-user@example.test")
                            .password("ValidPassword1")
                            .joinDate("01.01.1900.")
                            .role(Enums.Roles.ROLE_ADMIN)
                            .jobTitle(" Software Engineer ")
                            .build();

    when(passwordEncoder.encode("ValidPassword1"))
        .thenReturn("encoded-password");
    when(userRepository.save(any(User.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var savedUser = userService.registerUser(submittedUser);

    assertThat(savedUser.getId()).isNull();
    assertThat(savedUser.getName()).isEqualTo("New User");
    assertThat(savedUser.getEmail()).isEqualTo("new-user@example.test");
    assertThat(savedUser.getPassword()).isEqualTo("encoded-password");
    assertThat(savedUser.getJoinDate()).matches("\\d{2}\\.\\d{2}\\.\\d{4}\\.");
    assertThat(savedUser.getJoinDate()).isNotEqualTo("01.01.1900.");
    assertThat(savedUser.getRole()).isEqualTo(Enums.Roles.ROLE_USER);
    assertThat(savedUser.getJobTitle()).isEqualTo("Software Engineer");

    verify(userRepository).save(savedUser);
    verify(userMapper, never()).userDtoToUser(any(UserDto.class));
  }

  @Test
  void staffRegistrationUsesOnlyExplicitRoleAndJobTitle() throws AppException {

    var submittedUser = UserDto.builder()
                            .id(UUID.randomUUID())
                            .name("Staff User")
                            .email("staff@example.test")
                            .password("ValidPassword1")
                            .joinDate("01.01.1900.")
                            .role(Enums.Roles.ROLE_USER)
                            .jobTitle("Forged title")
                            .build();

    when(passwordEncoder.encode("ValidPassword1"))
        .thenReturn("encoded-password");
    when(userRepository.save(any(User.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var savedUser = userService.createStaffUser(
        submittedUser, Enums.Roles.ROLE_EMPLOYEE, "Archivist");

    assertThat(savedUser.getId()).isNull();
    assertThat(savedUser.getRole()).isEqualTo(Enums.Roles.ROLE_EMPLOYEE);
    assertThat(savedUser.getJobTitle()).isEqualTo("Archivist");
    assertThat(savedUser.getJoinDate()).isNotEqualTo("01.01.1900.");
    assertThat(savedUser.getPassword()).isEqualTo("encoded-password");

    verify(userRepository).save(savedUser);
    verify(userMapper, never()).userDtoToUser(any(UserDto.class));
  }

  @Test
  void staffRegistrationRejectsNonStaffRole() {
    var submittedUser = UserDto.builder().password("ValidPassword1").build();

    assertThatThrownBy(()
                           -> userService.createStaffUser(submittedUser,
                                                          Enums.Roles.ROLE_USER,
                                                          "Archivist"))
        .isInstanceOfSatisfying(AppException.class,
                                exception
                                -> assertThat(exception.getStatus())
                                       .isEqualTo(HttpStatus.BAD_REQUEST));

    verify(passwordEncoder, never()).encode(any(CharSequence.class));
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void publicRegistrationUsesDefaultJobTitleWhenMissing() {
    var submittedUser = UserDto.builder()
                            .name("New User")
                            .email("new-user@example.test")
                            .password("ValidPassword1")
                            .build();

    when(passwordEncoder.encode("ValidPassword1"))
        .thenReturn("encoded-password");
    when(userRepository.save(any(User.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var savedUser = userService.registerUser(submittedUser);

    assertThat(savedUser.getJobTitle()).isEqualTo("Unemployed");
    verify(userRepository).save(savedUser);
  }
}

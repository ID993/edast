package com.ivodam.finalpaper.edast.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.ivodam.finalpaper.edast.entity.RegistryBook;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.repository.RegistryBookRepository;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class RequestAccessServiceTests {

  @Mock private RegistryBookRepository registryBookRepository;

  @InjectMocks private RequestAccessService requestAccessService;

  @Test
  void administratorCanAccessAnyRequest() throws AppException {
    var requestId = UUID.randomUUID();
    var registryBook = registryBook(user(Enums.Roles.ROLE_USER),
                                    user(Enums.Roles.ROLE_EMPLOYEE));

    when(registryBookRepository.findByRequestId(requestId))
        .thenReturn(Optional.of(registryBook));

    var result = requestAccessService.requireAccess(
        requestId, user(Enums.Roles.ROLE_ADMIN));

    assertThat(result).isSameAs(registryBook);
  }

  @Test
  void ownerCanAccessOwnRequest() throws AppException {
    var requestId = UUID.randomUUID();
    var owner = user(Enums.Roles.ROLE_USER);
    var registryBook = registryBook(owner, user(Enums.Roles.ROLE_EMPLOYEE));

    when(registryBookRepository.findByRequestId(requestId))
        .thenReturn(Optional.of(registryBook));

    assertThat(requestAccessService.requireAccess(requestId, owner))
        .isSameAs(registryBook);
  }

  @Test
  void assignedEmployeeCanAccessRequest() throws AppException {
    var requestId = UUID.randomUUID();
    var employee = user(Enums.Roles.ROLE_EMPLOYEE);
    var registryBook = registryBook(user(Enums.Roles.ROLE_USER), employee);

    when(registryBookRepository.findByRequestId(requestId))
        .thenReturn(Optional.of(registryBook));

    assertThat(requestAccessService.requireAccess(requestId, employee))
        .isSameAs(registryBook);
  }

  @Test
  void differentUserCannotAccessRequest() {
    var requestId = UUID.randomUUID();
    var registryBook = registryBook(user(Enums.Roles.ROLE_USER),
                                    user(Enums.Roles.ROLE_EMPLOYEE));

    when(registryBookRepository.findByRequestId(requestId))
        .thenReturn(Optional.of(registryBook));

    assertRequestNotFound(()
                              -> requestAccessService.requireAccess(
                                  requestId, user(Enums.Roles.ROLE_USER)));
  }

  @Test
  void unassignedEmployeeCannotAccessRequest() {
    var requestId = UUID.randomUUID();
    var registryBook = registryBook(user(Enums.Roles.ROLE_USER),
                                    user(Enums.Roles.ROLE_EMPLOYEE));

    when(registryBookRepository.findByRequestId(requestId))
        .thenReturn(Optional.of(registryBook));

    assertRequestNotFound(()
                              -> requestAccessService.requireAccess(
                                  requestId, user(Enums.Roles.ROLE_EMPLOYEE)));
  }

  @Test
  void missingRequestUsesSameNotFoundResponse() {
    var requestId = UUID.randomUUID();

    when(registryBookRepository.findByRequestId(requestId))
        .thenReturn(Optional.empty());

    assertRequestNotFound(()
                              -> requestAccessService.requireAccess(
                                  requestId, user(Enums.Roles.ROLE_USER)));
  }

  private RegistryBook registryBook(User owner, User employee) {
    var registryBook = new RegistryBook();
    registryBook.setUser(owner);
    registryBook.setEmployee(employee);
    return registryBook;
  }

  private User user(Enums.Roles role) {
    var user = new User();
    user.setId(UUID.randomUUID());
    user.setRole(role);
    return user;
  }

  private void assertRequestNotFound(ThrowingCallable operation) {
    assertThatThrownBy(operation).isInstanceOfSatisfying(
        AppException.class, exception -> {
          assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
          assertThat(exception.getMessage()).isEqualTo("Request not found");
        });
  }
}
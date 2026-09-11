package com.ivodam.finalpaper.edast.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ivodam.finalpaper.edast.entity.RegistryBook;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.repository.BDMRequestRepository;
import com.ivodam.finalpaper.edast.repository.CadastralRequestRepository;
import com.ivodam.finalpaper.edast.repository.EducationRequestRepository;
import com.ivodam.finalpaper.edast.repository.RegistryBookRepository;
import com.ivodam.finalpaper.edast.repository.SpecialRequestRepository;
import com.ivodam.finalpaper.edast.repository.WorkRequestRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class RegistryBookServiceTests {

  @Mock private RegistryBookRepository registryBookRepository;

  @Mock private UserService userService;

  @Mock private BDMRequestRepository bdmRequestRepository;

  @Mock private WorkRequestRepository workRequestRepository;

  @Mock private EducationRequestRepository educationRequestRepository;

  @Mock private CadastralRequestRepository cadastralRequestRepository;

  @Mock private SpecialRequestRepository specialRequestRepository;

  @InjectMocks private RegistryBookService registryBookService;

  @Test
  void reassignsRequestToArchivist() throws AppException {
    var registryBookId = UUID.randomUUID();
    var employeeId = UUID.randomUUID();

    var registryBook = new RegistryBook();
    registryBook.setRead(true);

    var archivist = new User();
    archivist.setRole(Enums.Roles.ROLE_EMPLOYEE);
    archivist.setJobTitle("Archivist");

    when(registryBookRepository.findById(registryBookId))
        .thenReturn(Optional.of(registryBook));
    when(userService.findById(employeeId)).thenReturn(archivist);

    registryBookService.reassignToArchivist(registryBookId, employeeId);

    assertThat(registryBook.getEmployee()).isSameAs(archivist);
    assertThat(registryBook.isRead()).isFalse();
    verify(registryBookRepository).save(registryBook);
  }

  @Test
  void rejectsReassignmentToRegularUser() throws AppException {
    var registryBookId = UUID.randomUUID();
    var userId = UUID.randomUUID();

    var registryBook = new RegistryBook();
    var user = new User();
    user.setRole(Enums.Roles.ROLE_USER);
    user.setJobTitle("Archivist");

    when(registryBookRepository.findById(registryBookId))
        .thenReturn(Optional.of(registryBook));
    when(userService.findById(userId)).thenReturn(user);

    assertThatThrownBy(
        () -> registryBookService.reassignToArchivist(registryBookId, userId))
        .isInstanceOf(AppException.class)
        .satisfies(exception
                   -> assertThat(((AppException)exception).getStatus())
                          .isEqualTo(HttpStatus.BAD_REQUEST));

    verify(registryBookRepository, never()).save(any(RegistryBook.class));
  }

  @Test
  void rejectsReassignmentToNonArchivistEmployee() throws AppException {
    var registryBookId = UUID.randomUUID();
    var employeeId = UUID.randomUUID();

    var registryBook = new RegistryBook();
    var employee = new User();
    employee.setRole(Enums.Roles.ROLE_EMPLOYEE);
    employee.setJobTitle("Clerk");

    when(registryBookRepository.findById(registryBookId))
        .thenReturn(Optional.of(registryBook));
    when(userService.findById(employeeId)).thenReturn(employee);

    assertThatThrownBy(()
                           -> registryBookService.reassignToArchivist(
                               registryBookId, employeeId))
        .isInstanceOf(AppException.class)
        .satisfies(exception
                   -> assertThat(((AppException)exception).getStatus())
                          .isEqualTo(HttpStatus.BAD_REQUEST));

    verify(registryBookRepository, never()).save(any(RegistryBook.class));
  }
}
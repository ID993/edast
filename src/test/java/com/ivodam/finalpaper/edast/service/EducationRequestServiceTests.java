package com.ivodam.finalpaper.edast.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ivodam.finalpaper.edast.entity.RegistryBook;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.entity.EducationRequest;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.repository.EducationRequestRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class EducationRequestServiceTests {

  @Mock private EducationRequestRepository educationRequestRepository;

  @Mock private RegistryBookService registryBookService;

  @Mock private RegistryBook registryBook;

  @InjectMocks private EducationRequestService educationRequestService;

  @Test
  void ownerCanDeleteOwnEducationRequest() throws AppException {
    var ownerId = UUID.randomUUID();
    var requestId = UUID.randomUUID();
    var registryBookId = UUID.randomUUID();

    var owner = new User();
    owner.setId(ownerId);

    var educationRequest = new EducationRequest();
    educationRequest.setId(requestId);
    educationRequest.setUser(owner);

    when(educationRequestRepository.findById(requestId))
        .thenReturn(Optional.of(educationRequest));

    when(registryBookService.findByRequestId(requestId))
        .thenReturn(registryBook);
    when(registryBook.getId()).thenReturn(registryBookId);

    educationRequestService.deleteOwnedBy(requestId, ownerId);

    verify(registryBookService).findByRequestId(requestId);
    verify(registryBookService).deleteById(registryBookId);
    verify(educationRequestRepository).delete(educationRequest);
  }

  @Test
  void userCannotDeleteAnotherUsersEducationRequest() throws AppException {
    var ownerId = UUID.randomUUID();
    var otherUserId = UUID.randomUUID();
    var requestId = UUID.randomUUID();

    var owner = new User();
    owner.setId(ownerId);

    var educationRequest = new EducationRequest();
    educationRequest.setId(requestId);
    educationRequest.setUser(owner);

    when(educationRequestRepository.findById(requestId))
        .thenReturn(Optional.of(educationRequest));

    assertThatThrownBy(
        () -> educationRequestService.deleteOwnedBy(requestId, otherUserId))
        .isInstanceOfSatisfying(AppException.class,
                                exception
                                -> assertThat(exception.getStatus())
                                       .isEqualTo(HttpStatus.NOT_FOUND));

    verify(registryBookService, never()).findByRequestId(any(UUID.class));
    verify(educationRequestRepository, never()).delete(any(EducationRequest.class));
  }
}
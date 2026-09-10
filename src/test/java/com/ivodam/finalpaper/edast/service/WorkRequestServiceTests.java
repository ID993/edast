package com.ivodam.finalpaper.edast.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.entity.WorkRequest;
import com.ivodam.finalpaper.edast.exceptions.AppException;
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
class WorkRequestServiceTests {

  @Mock private WorkRequestRepository workRequestRepository;

  @Mock private RegistryBookService registryBookService;

  @InjectMocks private WorkRequestService workRequestService;

  @Test
  void ownerCanDeleteOwnWorkRequest() throws AppException {
    var ownerId = UUID.randomUUID();
    var requestId = UUID.randomUUID();

    var owner = new User();
    owner.setId(ownerId);

    var workRequest = new WorkRequest();
    workRequest.setId(requestId);
    workRequest.setUser(owner);

    when(workRequestRepository.findById(requestId))
        .thenReturn(Optional.of(workRequest));

    workRequestService.deleteOwnedBy(requestId, ownerId);

    verify(registryBookService).findByRequestId(requestId);
    verify(workRequestRepository).delete(workRequest);
  }

  @Test
  void userCannotDeleteAnotherUsersWorkRequest() throws AppException {
    var ownerId = UUID.randomUUID();
    var otherUserId = UUID.randomUUID();
    var requestId = UUID.randomUUID();

    var owner = new User();
    owner.setId(ownerId);

    var workRequest = new WorkRequest();
    workRequest.setId(requestId);
    workRequest.setUser(owner);

    when(workRequestRepository.findById(requestId))
        .thenReturn(Optional.of(workRequest));

    assertThatThrownBy(
        () -> workRequestService.deleteOwnedBy(requestId, otherUserId))
        .isInstanceOfSatisfying(AppException.class,
                                exception
                                -> assertThat(exception.getStatus())
                                       .isEqualTo(HttpStatus.NOT_FOUND));

    verify(registryBookService, never()).findByRequestId(any(UUID.class));
    verify(workRequestRepository, never()).delete(any(WorkRequest.class));
  }
}
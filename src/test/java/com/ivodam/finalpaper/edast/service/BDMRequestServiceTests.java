package com.ivodam.finalpaper.edast.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ivodam.finalpaper.edast.entity.RegistryBook;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.entity.BDMRequest;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.repository.BDMRequestRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class BDMRequestServiceTests {

  @Mock private BDMRequestRepository bdmRequestRepository;

  @Mock private RegistryBookService registryBookService;

  @Mock private RegistryBook registryBook;

  @InjectMocks private BDMRequestService bdmRequestService;

  @Test
  void ownerCanDeleteOwnBDMRequest() throws AppException {
    var ownerId = UUID.randomUUID();
    var requestId = UUID.randomUUID();
    var registryBookId = UUID.randomUUID();

    var owner = new User();
    owner.setId(ownerId);

    var bdmRequest = new BDMRequest();
    bdmRequest.setId(requestId);
    bdmRequest.setUser(owner);

    when(bdmRequestRepository.findById(requestId))
        .thenReturn(Optional.of(bdmRequest));

    when(registryBookService.findByRequestId(requestId))
        .thenReturn(registryBook);
    when(registryBook.getId()).thenReturn(registryBookId);

    bdmRequestService.deleteOwnedBy(requestId, ownerId);

    verify(registryBookService).findByRequestId(requestId);
    verify(registryBookService).deleteById(registryBookId);
    verify(bdmRequestRepository).delete(bdmRequest);
  }

  @Test
  void userCannotDeleteAnotherUsersBDMRequest() throws AppException {
    var ownerId = UUID.randomUUID();
    var otherUserId = UUID.randomUUID();
    var requestId = UUID.randomUUID();

    var owner = new User();
    owner.setId(ownerId);

    var bdmRequest = new BDMRequest();
    bdmRequest.setId(requestId);
    bdmRequest.setUser(owner);

    when(bdmRequestRepository.findById(requestId))
        .thenReturn(Optional.of(bdmRequest));

    assertThatThrownBy(
        () -> bdmRequestService.deleteOwnedBy(requestId, otherUserId))
        .isInstanceOfSatisfying(AppException.class,
                                exception
                                -> assertThat(exception.getStatus())
                                       .isEqualTo(HttpStatus.NOT_FOUND));

    verify(registryBookService, never()).findByRequestId(any(UUID.class));
    verify(bdmRequestRepository, never()).delete(any(BDMRequest.class));
  }
}
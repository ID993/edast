package com.ivodam.finalpaper.edast.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ivodam.finalpaper.edast.entity.RegistryBook;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.entity.SpecialRequest;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.repository.SpecialRequestRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class SpecialRequestServiceTests {

  @Mock private SpecialRequestRepository specialRequestRepository;

  @Mock private RegistryBookService registryBookService;

  @Mock private RegistryBook registryBook;

  @InjectMocks private SpecialRequestService specialRequestService;

  @Test
  void ownerCanDeleteOwnSpecialRequest() throws AppException {
    var ownerId = UUID.randomUUID();
    var requestId = UUID.randomUUID();
    var registryBookId = UUID.randomUUID();

    var owner = new User();
    owner.setId(ownerId);

    var specialRequest = new SpecialRequest();
    specialRequest.setId(requestId);
    specialRequest.setUser(owner);

    when(specialRequestRepository.findById(requestId))
        .thenReturn(Optional.of(specialRequest));

    when(registryBookService.findByRequestId(requestId))
        .thenReturn(registryBook);
    when(registryBook.getId()).thenReturn(registryBookId);

    specialRequestService.deleteOwnedBy(requestId, ownerId);

    verify(registryBookService).findByRequestId(requestId);
    verify(registryBookService).deleteById(registryBookId);
    verify(specialRequestRepository).delete(specialRequest);
  }

  @Test
  void userCannotDeleteAnotherUsersSpecialRequest() throws AppException {
    var ownerId = UUID.randomUUID();
    var otherUserId = UUID.randomUUID();
    var requestId = UUID.randomUUID();

    var owner = new User();
    owner.setId(ownerId);

    var specialRequest = new SpecialRequest();
    specialRequest.setId(requestId);
    specialRequest.setUser(owner);

    when(specialRequestRepository.findById(requestId))
        .thenReturn(Optional.of(specialRequest));

    assertThatThrownBy(
        () -> specialRequestService.deleteOwnedBy(requestId, otherUserId))
        .isInstanceOfSatisfying(AppException.class,
                                exception
                                -> assertThat(exception.getStatus())
                                       .isEqualTo(HttpStatus.NOT_FOUND));

    verify(registryBookService, never()).findByRequestId(any(UUID.class));
    verify(specialRequestRepository, never()).delete(any(SpecialRequest.class));
  }
}
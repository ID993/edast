package com.ivodam.finalpaper.edast.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ivodam.finalpaper.edast.entity.RegistryBook;
import com.ivodam.finalpaper.edast.entity.Response;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.repository.ResponseRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class ResponseServiceTests {

  @Mock private ResponseRepository responseRepository;

  @InjectMocks private ResponseService responseService;

  @Test
  void createsResponseFromAuthorizedRegistryBook() {
    var employee = new User();
    var requester = new User();

    var registryBook = new RegistryBook();
    registryBook.setEmployee(employee);
    registryBook.setUser(requester);

    var input = new Response();
    input.setTitle("Requested document");
    input.setContent("The request has been completed.");

    when(responseRepository.save(any(Response.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var saved = responseService.create(registryBook, input);

    assertThat(saved.getTitle()).isEqualTo(input.getTitle());
    assertThat(saved.getContent()).isEqualTo(input.getContent());
    assertThat(saved.getRegistryBook()).isSameAs(registryBook);
    assertThat(saved.getEmployee()).isSameAs(employee);
    assertThat(saved.getUser()).isSameAs(requester);
    assertThat(saved.isRead()).isFalse();
    assertThat(saved.getDateOfCreation()).isNotBlank();

    verify(responseRepository).save(saved);
  }

  @Test
  void findsResponseByRegistryBookId() throws AppException {
    var registryBookId = UUID.randomUUID();
    var response = new Response();

    when(responseRepository.findByRegistryBookId(registryBookId))
        .thenReturn(Optional.of(response));

    assertThat(responseService.findByRegistryBookId(registryBookId))
        .isSameAs(response);
  }

  @Test
  void missingResponseReturnsNotFound() {
    var registryBookId = UUID.randomUUID();

    when(responseRepository.findByRegistryBookId(registryBookId))
        .thenReturn(Optional.empty());

    assertThatThrownBy(
        () -> responseService.findByRegistryBookId(registryBookId))
        .isInstanceOfSatisfying(AppException.class,
                                exception
                                -> assertThat(exception.getStatus())
                                       .isEqualTo(HttpStatus.NOT_FOUND));
  }
}

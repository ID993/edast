package com.ivodam.finalpaper.edast.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ivodam.finalpaper.edast.entity.RegistryBook;
import com.ivodam.finalpaper.edast.entity.Response;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.service.RequestAccessService;
import com.ivodam.finalpaper.edast.service.ResponseService;
import com.ivodam.finalpaper.edast.utility.Utility;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class DocumentControllerTests {

  @Mock private Utility utility;

  @Mock private RequestAccessService requestAccessService;

  @Mock private ResponseService responseService;

  @Mock private Authentication authentication;

  @InjectMocks private DocumentController documentController;

  private User currentUser;

  @BeforeEach
  void configureSecurityContext() {
    currentUser = new User();

    var securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(authentication);
    SecurityContextHolder.setContext(securityContext);

    when(authentication.getPrincipal()).thenReturn(currentUser);
  }

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void authorizesRequestBeforeCreatingDownload() throws Exception {
    var requestId = UUID.randomUUID();
    var registryBookId = UUID.randomUUID();
    var responseId = UUID.randomUUID();
    var zipContent = new byte[] {1, 2, 3};

    var registryBook = new RegistryBook();
    registryBook.setId(registryBookId);

    var response = new Response();
    response.setId(responseId);

    when(requestAccessService.requireAccess(requestId, currentUser))
        .thenReturn(registryBook);
    when(responseService.findByRegistryBookId(registryBookId))
        .thenReturn(response);
    when(utility.createZipFile(responseId)).thenReturn(zipContent);

    var result = documentController.downloadFiles(requestId);

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).isSameAs(zipContent);
    assertThat(result.getHeaders().getCacheControl()).isEqualTo("no-store");

    var orderedCalls = inOrder(requestAccessService, responseService, utility);

    orderedCalls.verify(requestAccessService)
        .requireAccess(requestId, currentUser);
    orderedCalls.verify(responseService).findByRegistryBookId(registryBookId);
    orderedCalls.verify(utility).createZipFile(responseId);
  }

  @Test
  void deniedRequestAccessPreventsDownload() throws Exception {
    var requestId = UUID.randomUUID();

    when(requestAccessService.requireAccess(requestId, currentUser))
        .thenThrow(new AppException("Request not found", HttpStatus.NOT_FOUND));

    assertThatThrownBy(() -> documentController.downloadFiles(requestId))
        .isInstanceOfSatisfying(AppException.class,
                                exception
                                -> assertThat(exception.getStatus())
                                       .isEqualTo(HttpStatus.NOT_FOUND));

    verify(responseService, never()).findByRegistryBookId(any(UUID.class));
    verify(utility, never()).createZipFile(any(UUID.class));
  }
}
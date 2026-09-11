package com.ivodam.finalpaper.edast.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ivodam.finalpaper.edast.entity.BDMRequest;
import com.ivodam.finalpaper.edast.entity.CadastralRequest;
import com.ivodam.finalpaper.edast.entity.EducationRequest;
import com.ivodam.finalpaper.edast.entity.SpecialRequest;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.entity.WorkRequest;
import com.ivodam.finalpaper.edast.service.BDMRequestService;
import com.ivodam.finalpaper.edast.service.CadastralRequestService;
import com.ivodam.finalpaper.edast.service.EducationRequestService;
import com.ivodam.finalpaper.edast.service.RegistryBookService;
import com.ivodam.finalpaper.edast.service.RequestAccessService;
import com.ivodam.finalpaper.edast.service.ResponseService;
import com.ivodam.finalpaper.edast.service.SpecialRequestService;
import com.ivodam.finalpaper.edast.service.WorkRequestService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;

@ExtendWith(MockitoExtension.class)
class RequestCreationControllerTests {

  @Mock private BDMRequestService bdmRequestService;
  @Mock private WorkRequestService workRequestService;
  @Mock private EducationRequestService educationRequestService;
  @Mock private CadastralRequestService cadastralRequestService;
  @Mock private SpecialRequestService specialRequestService;
  @Mock private RegistryBookService registryBookService;
  @Mock private RequestAccessService requestAccessService;
  @Mock private ResponseService responseService;

  @InjectMocks private BdmRequestController bdmRequestController;
  @InjectMocks private WorkRequestController workRequestController;
  @InjectMocks private EducationRequestController educationRequestController;
  @InjectMocks private CadastralRequestController cadastralRequestController;
  @InjectMocks private SpecialRequestController specialRequestController;

  private User user;

  @BeforeEach
  void authenticateUser() {
    user = new User();
    user.setId(UUID.randomUUID());

    var authentication =
        new UsernamePasswordAuthenticationToken(user, null, List.of());

    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void bdmCreationRedirectsToCurrentUsersList() {
    var request = new BDMRequest();
    request.setId(UUID.randomUUID());
    var result = new BeanPropertyBindingResult(request, "bdmRequest");

    when(bdmRequestService.saveRequest(request)).thenReturn(request);

    var view = bdmRequestController.requests(request, result, "Birth");

    assertThat(view).isEqualTo("redirect:/user-bdm-requests/all");
    verify(registryBookService).create(request.getId(), "Registry", user);
  }

  @Test
  void workCreationRedirectsToCurrentUsersList() {
    var request = new WorkRequest();
    request.setId(UUID.randomUUID());
    var result = new BeanPropertyBindingResult(request, "workRequest");

    when(workRequestService.saveRequest(request)).thenReturn(request);

    var view = workRequestController.workRequests(request, result);

    assertThat(view).isEqualTo("redirect:/user-work-requests/all");
    verify(registryBookService).create(request.getId(), "Work", user);
  }

  @Test
  void educationCreationRedirectsToCurrentUsersList() {
    var request = new EducationRequest();
    request.setId(UUID.randomUUID());
    var result = new BeanPropertyBindingResult(request, "educationRequest");

    when(educationRequestService.saveRequest(request)).thenReturn(request);

    var view = educationRequestController.educationRequests(
        request, result, new ExtendedModelMap());

    assertThat(view).isEqualTo("redirect:/user-education-requests/all");
    verify(registryBookService).create(request.getId(), "Education", user);
  }

  @Test
  void cadastralCreationRedirectsToCurrentUsersList() {
    var request = new CadastralRequest();
    request.setId(UUID.randomUUID());
    request.setLandParcels("123");
    var result = new BeanPropertyBindingResult(request, "cadastralRequest");

    when(cadastralRequestService.saveRequest(request)).thenReturn(request);

    var view = cadastralRequestController.cadastralRequests(
        request, result, new ExtendedModelMap());

    assertThat(view).isEqualTo("redirect:/user-cadastral-requests/all");
    verify(registryBookService).create(request.getId(), "Cadastral", user);
  }

  @Test
  void specialCreationRedirectsToCurrentUsersList() {
    var request = new SpecialRequest();
    request.setId(UUID.randomUUID());
    var result = new BeanPropertyBindingResult(request, "specialRequest");

    when(specialRequestService.saveRequest(request)).thenReturn(request);

    var view = specialRequestController.specialRequests(request, result);

    assertThat(view).isEqualTo("redirect:/user-special-requests/all");
    verify(registryBookService).create(request.getId(), "Special", user);
  }
}
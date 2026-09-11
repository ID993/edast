package com.ivodam.finalpaper.edast.service;

import com.ivodam.finalpaper.edast.entity.BDMRequest;
import com.ivodam.finalpaper.edast.entity.EducationRequest;
import com.ivodam.finalpaper.edast.entity.WorkRequest;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.repository.EducationRequestRepository;
import com.ivodam.finalpaper.edast.repository.WorkRequestRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EducationRequestService {

  private final EducationRequestRepository educationRequestRepository;

  private final RegistryBookService registryBookService;

  public EducationRequest saveRequest(EducationRequest educationRequest) {
    educationRequest.setRead(false);
    educationRequest.setCompleted(false);
    educationRequest.setDateCreated(
        LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")));
    return educationRequestRepository.save(educationRequest);
  }

  public EducationRequest findById(UUID id) {
    return educationRequestRepository.findById(id).orElse(null);
  }

  @Transactional
  public void deleteOwnedBy(UUID requestId, UUID userId) throws AppException {

    var request = educationRequestRepository.findById(requestId).orElseThrow(
        ()
            -> new AppException("Education request not found",
                                HttpStatus.NOT_FOUND));

    if (!request.getUser().getId().equals(userId)) {
      throw new AppException("Education request not found",
                             HttpStatus.NOT_FOUND);
    }

    var registryBook = registryBookService.findByRequestId(requestId);

    if (registryBook != null) {
      registryBookService.deleteById(registryBook.getId());
    }

    educationRequestRepository.delete(request);
  }

  public void updateRequest(EducationRequest educationRequest) {
    educationRequestRepository.save(educationRequest);
  }

  public void readRequest(EducationRequest educationRequest) {
    educationRequest.setRead(true);
    educationRequestRepository.save(educationRequest);
  }

  public List<EducationRequest> findAll() {
    return educationRequestRepository.findAll();
  }

  public List<EducationRequest> findAllByUserId(UUID userId) {
    return educationRequestRepository.findAllByUserId(userId);
  }

  public Page<EducationRequest> searchAllByKeyword(String keyword, UUID userId,
                                                   Pageable pageable) {
    return educationRequestRepository.searchAllByKeyword(keyword, userId,
                                                         pageable);
  }
}

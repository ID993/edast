package com.ivodam.finalpaper.edast.service;

import com.ivodam.finalpaper.edast.entity.BDMRequest;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.repository.BDMRequestRepository;
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
public class BDMRequestService {

  private final BDMRequestRepository bdmRequestRepository;

  private final RegistryBookService registryBookService;

  public BDMRequest saveRequest(BDMRequest bdmRequest) {
    bdmRequest.setRead(false);
    bdmRequest.setCompleted(false);
    bdmRequest.setDateCreated(
        LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")));
    return bdmRequestRepository.save(bdmRequest);
  }

  public BDMRequest findById(UUID id) {
    return bdmRequestRepository.findById(id).orElse(null);
  }

  @Transactional
  public void deleteOwnedBy(UUID requestId, UUID userId) throws AppException {

    var request = bdmRequestRepository.findById(requestId).orElseThrow(
        ()
            -> new AppException("Registry request not found",
                                HttpStatus.NOT_FOUND));

    if (!request.getUser().getId().equals(userId)) {
      throw new AppException("Registry request not found",
                             HttpStatus.NOT_FOUND);
    }

    var registryBook = registryBookService.findByRequestId(requestId);

    if (registryBook != null) {
      registryBookService.deleteById(registryBook.getId());
    }

    bdmRequestRepository.delete(request);
  }

  public void updateRequest(BDMRequest bdmRequest) {
    bdmRequestRepository.save(bdmRequest);
  }

  public void readRequest(BDMRequest bdmRequest) {
    bdmRequest.setRead(true);
    bdmRequestRepository.save(bdmRequest);
  }

  public List<BDMRequest> findAll() { return bdmRequestRepository.findAll(); }

  public List<BDMRequest> findAllByUserId(UUID userId) {
    return bdmRequestRepository.findAllByUserId(userId);
  }

  public List<BDMRequest>
  searchAllByFullNameContainingIgnoreCase(String fullName) {
    return bdmRequestRepository.searchAllByFullNameContainingIgnoreCase(
        fullName);
  }

  public Page<BDMRequest> searchAllByKeyword(String keyword, UUID userId,
                                             Pageable pageable) {
    return bdmRequestRepository.searchAllByKeyword(keyword, userId, pageable);
  }
}

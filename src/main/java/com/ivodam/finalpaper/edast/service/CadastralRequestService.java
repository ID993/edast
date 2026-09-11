package com.ivodam.finalpaper.edast.service;

import com.ivodam.finalpaper.edast.entity.CadastralRequest;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.repository.CadastralRequestRepository;
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
public class CadastralRequestService {

  private final CadastralRequestRepository cadastralRequestRepository;

  private final RegistryBookService registryBookService;

  public CadastralRequest saveRequest(CadastralRequest cadastralRequest) {
    cadastralRequest.setRead(false);
    cadastralRequest.setCompleted(false);
    cadastralRequest.setDateCreated(
        LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")));
    return cadastralRequestRepository.save(cadastralRequest);
  }

  public CadastralRequest findById(UUID id) throws AppException {
    return cadastralRequestRepository.findById(id).orElseThrow(
        ()
            -> new AppException("Cadastral request with id " + id +
                                    " doesn't exist!",
                                HttpStatus.NOT_FOUND));
  }

  @Transactional
  public void deleteOwnedBy(UUID requestId, UUID userId) throws AppException {

    var request = cadastralRequestRepository.findById(requestId).orElseThrow(
        ()
            -> new AppException("Cadastral request not found",
                                HttpStatus.NOT_FOUND));

    if (!request.getUser().getId().equals(userId)) {
      throw new AppException("Cadastral request not found",
                             HttpStatus.NOT_FOUND);
    }

    var registryBook = registryBookService.findByRequestId(requestId);

    if (registryBook != null) {
      registryBookService.deleteById(registryBook.getId());
    }

    cadastralRequestRepository.delete(request);
  }

  public void updateRequest(CadastralRequest cadastralRequest) {
    cadastralRequestRepository.save(cadastralRequest);
  }

  public void readRequest(CadastralRequest cadastralRequest) {
    cadastralRequest.setRead(true);
    cadastralRequestRepository.save(cadastralRequest);
  }

  public List<CadastralRequest> findAll() {
    return cadastralRequestRepository.findAll();
  }

  public List<CadastralRequest> findAllByUserId(UUID userId) {
    return cadastralRequestRepository.findAllByUserId(userId);
  }

  public Page<CadastralRequest> searchAllByKeyword(String keyword, UUID userId,
                                                   Pageable pageable) {
    return cadastralRequestRepository.searchAllByKeyword(keyword, userId,
                                                         pageable);
  }
}

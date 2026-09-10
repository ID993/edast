package com.ivodam.finalpaper.edast.service;

import com.ivodam.finalpaper.edast.entity.RegistryBook;
import com.ivodam.finalpaper.edast.entity.Response;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.repository.ResponseRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ResponseService {

  private final ResponseRepository responseRepository;

  public Response create(RegistryBook registryBook, Response response) {
    var responseToSave = new Response();
    responseToSave.setTitle(response.getTitle());
    responseToSave.setContent(response.getContent());
    responseToSave.setRegistryBook(registryBook);
    responseToSave.setEmployee(registryBook.getEmployee());
    responseToSave.setUser(registryBook.getUser());
    responseToSave.setRead(false);
    responseToSave.setDateOfCreation(
        LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")));

    return responseRepository.save(responseToSave);
  }

  public Response findById(UUID id) throws AppException {
    return responseRepository.findById(id).orElseThrow(
        () -> new AppException("Response not found", HttpStatus.NOT_FOUND));
  }

  public void update(Response response) { responseRepository.save(response); }

  public void deleteById(UUID id) { responseRepository.deleteById(id); }

  public Response findByRegistryBookId(UUID registryBookId)
      throws AppException {

    return responseRepository.findByRegistryBookId(registryBookId)
        .orElseThrow(
            () -> new AppException("Response not found", HttpStatus.NOT_FOUND));
  }

  public List<Response> findAll() { return responseRepository.findAll(); }

  public List<Response> findAllByUserId(UUID userId) {
    return responseRepository.findAllByUserId(userId);
  }

  public long countByRead(boolean read, UUID userID) {
    return responseRepository.countByReadAndUserId(read, userID);
  }
}

package com.ivodam.finalpaper.edast.service;

import com.ivodam.finalpaper.edast.entity.RegistryBook;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.repository.RegistryBookRepository;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RequestAccessService {

  private final RegistryBookRepository registryBookRepository;

  public RegistryBook requireAccess(UUID requestId, User currentUser)
      throws AppException {

    var registryBook =
        registryBookRepository.findByRequestId(requestId).orElseThrow(
            this::requestNotFound);

    var currentUserId = currentUser.getId();
    var currentRole = currentUser.getRole();

    var hasAccess = currentRole == Enums.Roles.ROLE_ADMIN ||
                    currentRole == Enums.Roles.ROLE_USER &&
                        hasId(registryBook.getUser(), currentUserId) ||
                    currentRole == Enums.Roles.ROLE_EMPLOYEE &&
                        hasId(registryBook.getEmployee(), currentUserId);

    if (!hasAccess) {
      throw requestNotFound();
    }

    return registryBook;
  }

  public RegistryBook requireAssignedEmployee(UUID requestId, User currentUser)
      throws AppException {

    var registryBook =
        registryBookRepository.findByRequestId(requestId).orElseThrow(
            this::requestNotFound);

    if (currentUser.getRole() != Enums.Roles.ROLE_EMPLOYEE ||
        !hasId(registryBook.getEmployee(), currentUser.getId())) {
      throw requestNotFound();
    }

    return registryBook;
  }

  private boolean hasId(User user, UUID expectedId) {
    return user != null && Objects.equals(user.getId(), expectedId);
  }

  private AppException requestNotFound() {
    return new AppException("Request not found", HttpStatus.NOT_FOUND);
  }
}
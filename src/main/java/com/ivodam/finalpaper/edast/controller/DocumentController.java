package com.ivodam.finalpaper.edast.controller;

import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.service.RequestAccessService;
import com.ivodam.finalpaper.edast.service.ResponseService;
import com.ivodam.finalpaper.edast.utility.Utility;
import java.io.IOException;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@AllArgsConstructor
public class DocumentController {

  private final Utility utility;
  private final RequestAccessService requestAccessService;
  private final ResponseService responseService;

  @GetMapping("/response/request/{requestId}/download")
  public ResponseEntity<byte[]> downloadFiles(@PathVariable UUID requestId)
      throws IOException, AppException {

    var registryBook =
        requestAccessService.requireAccess(requestId, currentUser());
    var response = responseService.findByRegistryBookId(registryBook.getId());
    var zipContent = utility.createZipFile(response.getId());

    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    headers.setContentDisposition(ContentDisposition.attachment()
                                      .filename("edast-documents.zip")
                                      .build());
    headers.setContentLength(zipContent.length);
    headers.setCacheControl("no-store");

    return ResponseEntity.ok().headers(headers).body(zipContent);
  }

  private User currentUser() {
    return (User)SecurityContextHolder.getContext()
        .getAuthentication()
        .getPrincipal();
  }
}
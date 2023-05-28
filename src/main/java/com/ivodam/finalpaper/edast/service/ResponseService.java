package com.ivodam.finalpaper.edast.service;

import com.ivodam.finalpaper.edast.entity.Response;
import com.ivodam.finalpaper.edast.repository.RegistryBookRepository;
import com.ivodam.finalpaper.edast.repository.ResponseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ResponseService {

    private final ResponseRepository responseRepository;
    private final RegistryBookRepository registryBookRepository;

    public Response create(UUID requestId, Response response){
        var responseToSave = new Response();
        responseToSave.setTitle(response.getTitle());
        responseToSave.setContent(response.getContent());
        responseToSave.setRequestId(requestId);
        responseToSave.setEmployee(registryBookRepository.findByRequestId(requestId).orElseThrow().getEmployee());
        responseToSave.setUser(registryBookRepository.findByRequestId(requestId).orElseThrow().getUser());
        responseToSave.setRead(false);
        responseToSave.setDateOfCreation(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")));
        return responseRepository.save(responseToSave);
    }

    public Response findById(UUID id){
        return responseRepository.findById(id).orElse(null);
    }

    public void update(Response response){
        responseRepository.save(response);
    }


    public void deleteById(UUID id){
        responseRepository.deleteById(id);
    }

    public Response findByRequestId(UUID requestId){
        return responseRepository.findByRequestId(requestId);
    }

    public List<Response> findAll(){
        return responseRepository.findAll();
    }

    public List<Response> findAllByUserId(UUID userId){
        return responseRepository.findAllByUserId(userId);
    }

    public long countByRead(boolean read, UUID userID){
        return responseRepository.countByReadAndUserId(read, userID);
    }

}

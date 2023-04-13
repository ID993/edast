package com.ivodam.finalpaper.edast.service;

import com.ivodam.finalpaper.edast.entity.Response;
import com.ivodam.finalpaper.edast.repository.ResponseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ResponseService {

    private final ResponseRepository responseRepository;

    public void create(Response response){
        response.setDateOfCreation(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")));
        responseRepository.save(response);
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

    public Iterable<Response> findAll(){
        return responseRepository.findAll();
    }
}

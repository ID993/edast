package com.ivodam.finalpaper.edast.service;

import com.ivodam.finalpaper.edast.entity.RegistryBook;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RegistryBookService {

    private final RegistryBookRepository registryBookRepository;
    private final UserService userService;
    private final BDMRequestRepository bdmRequestRepository;
    private final WorkRequestRepository workRequestRepository;
    private final EducationRequestRepository educationRequestRepository;
    private final CadastralRequestRepository cadastralRequestRepository;
    private final SpecialRequestRepository specialRequestRepository;
    private static final String STATUS = "In progress";
    private static final String DATE_FORMAT = "dd.MM.yyyy.";


    public String classNumberGenerator(String requestName) {
        return "611-12/" + LocalDate.now().getYear() + "-" + requestName.toUpperCase()
                + (registryBookRepository.countByRequestName(requestName) + 1);
    }

    public User findUserWithLeastRequests() {
        var users = userService.findAllByRoleAndJobTitle("ROLE_EMPLOYEE", "Archivist");
        var userWithLeastRequests = users.get(0);
        for (User user : users) {
            if(registryBookRepository.countByStatusAndEmployeeId(STATUS, user.getId()) <=
                    registryBookRepository.countByStatusAndEmployeeId(STATUS, userWithLeastRequests.getId()))
                userWithLeastRequests = user;
        }
        return userWithLeastRequests;
    }

    public static int index = 0;
    public User roundRobin() {
        var users = userService.findAllByRoleAndJobTitle("ROLE_EMPLOYEE", "Archivist");
        if (index >= users.size())
            index = 0;
        var user = users.get(index);
        index++;
        return user;
    }

    public void divideRequests(UUID userId) {
        var registryBooks = registryBookRepository.findByEmployeeIdAndStatus(userId, "In progress");
        for (var regBook : registryBooks) {
            regBook.setEmployee(roundRobin());
            regBook.setRead(false);
            registryBookRepository.save(regBook);
        }
    }

    public String getDateDay(long days){
        if(LocalDate.now().getDayOfWeek() == DayOfWeek.SATURDAY)
            return LocalDate.now().plusDays(days + 2).format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        else if(LocalDate.now().getDayOfWeek() == DayOfWeek.SUNDAY)
            return LocalDate.now().plusDays(days + 1).format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        else
            return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    public String getDeadlineDate(){
        if(LocalDate.now().getDayOfWeek() == DayOfWeek.SATURDAY)
            return LocalDate.now().plusDays(17).format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        else if(LocalDate.now().getDayOfWeek() == DayOfWeek.SUNDAY)
            return LocalDate.now().plusDays(16).format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        else
            return LocalDate.now().plusDays(15).format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }


    public void create(UUID requestId, String requestName, User user) {
        var registryBook = new RegistryBook();
        registryBook.setClassNumber(classNumberGenerator(requestName));
        registryBook.setRegistryNumber("2189-101-" + LocalDate.now().getYear() + "-"  + requestName + "/1");
        registryBook.setTitle(requestName + " Request");
        registryBook.setRequestName(requestName);
        registryBook.setStatus(STATUS);
        registryBook.setReceivedDate(getDateDay(0));
        registryBook.setDeadlineDate(getDateDay(15));
        registryBook.setRequestId(requestId);
        registryBook.setEmployee(roundRobin());
        registryBook.setUser(user);
        registryBook.setRead(false);
        registryBookRepository.save(registryBook);
    }

    public void deleteById(UUID id) {
        registryBookRepository.deleteById(id);
    }

    public RegistryBook findById(UUID id) {
        return registryBookRepository.findById(id).orElse(null);
    }

    public List<RegistryBook> findAll() {
        return registryBookRepository.findAll();
    }

    public RegistryBook findByRequestId(UUID id) {
        return registryBookRepository.findByRequestId(id).orElse(null);
    }

    public Page<RegistryBook> findByRequestName(String requestName, Pageable pageable) {
        return registryBookRepository.findByRequestName(requestName, pageable);
    }
    public Page<RegistryBook> findByEmployeeIdAndKeyword(UUID id, String keyword, Pageable pageable) {
        return registryBookRepository.findByEmployeeIdAndKeyword(id, keyword, pageable);
    }

    public Page<RegistryBook> findByEmployeeIdAndRequestName(UUID id, String requestName, Pageable pageable) {
        return registryBookRepository.findByEmployeeIdAndRequestName(id, requestName, pageable);
    }

    public void updateRequestByName(UUID requestId,String requestName) throws AppException {
        switch (requestName) {
            case "BDM" -> {
                var bdmRequest = bdmRequestRepository.findById(requestId).orElseThrow(() ->
                        new AppException("Request with id: " + requestId + " not found", HttpStatus.NOT_FOUND));
                bdmRequest.setCompleted(true);
                bdmRequestRepository.save(bdmRequest);
            }
            case "Work" -> {
                var workRequest = workRequestRepository.findById(requestId).orElseThrow(() ->
                        new AppException("Request with id: " + requestId + " not found", HttpStatus.NOT_FOUND));
                workRequest.setCompleted(true);
                workRequestRepository.save(workRequest);
            }
            case "Education" -> {
                var educationRequest = educationRequestRepository.findById(requestId).orElseThrow(() ->
                        new AppException("Request with id: " + requestId + " not found", HttpStatus.NOT_FOUND));
                educationRequest.setCompleted(true);
                educationRequestRepository.save(educationRequest);
            }
            case "Cadastral" -> {
                var cadastralRequest = cadastralRequestRepository.findById(requestId).orElseThrow(() ->
                        new AppException("Request with id: " + requestId + " not found", HttpStatus.NOT_FOUND));
                cadastralRequest.setCompleted(true);
                cadastralRequestRepository.save(cadastralRequest);
            }
            default -> {
                var specialRequest = specialRequestRepository.findById(requestId).orElseThrow(() ->
                        new AppException("Request with id: " + requestId + " not found", HttpStatus.NOT_FOUND));
                specialRequest.setCompleted(true);
                specialRequestRepository.save(specialRequest);
            }
        }
    }

    public void updateRegistryBook(UUID id) throws AppException {
        var registryBook = registryBookRepository.findByRequestId(id).orElseThrow(() ->
                new AppException("Request with id: " + id + " not found", HttpStatus.NOT_FOUND));
        updateRequestByName(id, registryBook.getRequestName());
        registryBook.setStatus("Completed");
        registryBook.setCompletedDate(LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        registryBookRepository.save(registryBook);
    }

    public void update(RegistryBook registryBook) {
        registryBookRepository.save(registryBook);
    }

    public void updateReadStatus(UUID requestId) {
        var registryBook = registryBookRepository.findByRequestId(requestId).orElse(null);
        assert registryBook != null;
        registryBook.setRead(true);
        registryBookRepository.save(registryBook);
    }

    public Page<RegistryBook> findByEmployeeIdAndRead(UUID employeeId, boolean read, Pageable pageable) {
        return registryBookRepository.findByEmployeeIdAndRead(employeeId, read, pageable);
    }


    public long countByEmployeeIdAndRead(UUID employeeId, boolean read) {
        return registryBookRepository.countByEmployeeIdAndRead(employeeId, read);
    }

    public Page<RegistryBook> searchByClassNumberOrUserOrEmployee(String keyword, Pageable pageable) {
        return registryBookRepository.searchAllByClassNumberOrUserOrEmployee(keyword, pageable);
    }

    public Page<RegistryBook> searchAllBdmByClassNumberOrUser(String keyword, UUID employeeId, Pageable pageable) {
        return registryBookRepository.searchAllBdmByClassNumberOrUser(keyword, employeeId, pageable);
    }

    public Page<RegistryBook> searchAllWorkByClassNumberOrUser(String keyword, UUID employeeId, Pageable pageable) {
        return registryBookRepository.searchAllWorkByClassNumberOrUser(keyword, employeeId, pageable);
    }

    public Page<RegistryBook> searchAllEducationByClassNumberOrUser(String keyword, UUID employeeId, Pageable pageable) {
        return registryBookRepository.searchAllEducationByClassNumberOrUser(keyword, employeeId, pageable);
    }

    public Page<RegistryBook> searchAllCadastralByClassNumberOrUser(String keyword, UUID employeeId, Pageable pageable) {
        return registryBookRepository.searchAllCadastralByClassNumberOrUser(keyword, employeeId, pageable);
    }

    public Page<RegistryBook> searchAllSpecialByClassNumberOrUser(String keyword, UUID employeeId, Pageable pageable) {
        return registryBookRepository.searchAllSpecialByClassNumberOrUser(keyword, employeeId, pageable);
    }

    public Page<RegistryBook> searchAllByClassNumberOrUserOrEmployeeAndRequestName(String keyword, String requestName, Pageable pageable) {
        return registryBookRepository.searchAllByClassNumberOrUserOrEmployeeAndRequestName(keyword, requestName, pageable);
    }

    public Page<RegistryBook> findAllPaging(Pageable pageable) {
        return registryBookRepository.findAll(pageable);
    }

    public List<Object[]> countByRequestName() {
        return registryBookRepository.getTotalRequestsByRequestName();
    }

    public long count() {
        return registryBookRepository.count();
    }
}

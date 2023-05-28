package com.ivodam.finalpaper.edast.service;

import com.ivodam.finalpaper.edast.entity.RegistryBook;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.repository.BDMRequestRepository;
import com.ivodam.finalpaper.edast.repository.RegistryBookRepository;
import lombok.AllArgsConstructor;
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
    private static final String STATUS = "In progress";
    private static final String DATE_FORMAT = "dd.MM.yyyy.";

    public String classNumberGenerator(String requestName){
        return "611-12/" + LocalDate.now().getYear() + "-05/"
                + (registryBookRepository.countByRequestName(requestName) + 1);
    }

    public User findUserWithLeastRequests(){
        var users = userService.findAllByRoleAndJobTitle("ROLE_EMPLOYEE", "Archivist");
        var userWithLeastRequests = users.get(0);
        for (User user : users) {
            if(registryBookRepository.countByStatusAndEmployeeId(STATUS, user.getId()) <=
                    registryBookRepository.countByStatusAndEmployeeId(STATUS, userWithLeastRequests.getId()))
                userWithLeastRequests = user;
        }
        return userWithLeastRequests;
    }

    public String getDateDay(){
        if(LocalDate.now().getDayOfWeek() == DayOfWeek.SATURDAY)
            return LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        else if(LocalDate.now().getDayOfWeek() == DayOfWeek.SUNDAY)
            return LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        else
            return LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
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
        registryBook.setReceivedDate(getDateDay());
        registryBook.setDeadlineDate(getDeadlineDate());
        registryBook.setRequestId(requestId);
        registryBook.setEmployee(findUserWithLeastRequests());
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

    public List<RegistryBook> findByRequestName(String requestName) {
        return registryBookRepository.findByRequestName(requestName);
    }
    public List<RegistryBook> findByEmployeeId(UUID id) {
        return registryBookRepository.findByEmployeeId(id);
    }

    public List<RegistryBook> findByEmployeeIdAndRequestName(UUID id, String requestName) {
        return registryBookRepository.findByEmployeeIdAndRequestName(id, requestName);
    }

    public void updateRegistryBook(UUID id) {
        var registryBook = registryBookRepository.findByRequestId(id).orElse(null);
        var bdmRequest = bdmRequestRepository.findById(id).orElse(null);
        assert registryBook != null;
        assert bdmRequest != null;
        bdmRequest.setCompleted(true);
        registryBook.setStatus("Completed");
        registryBook.setCompletedDate(LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        registryBookRepository.save(registryBook);
        bdmRequestRepository.save(bdmRequest);
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

    public List<RegistryBook> findByEmployeeIdAndReadOrderByReceivedDateDesc(UUID employeeId, boolean read) {
        return registryBookRepository.findByEmployeeIdAndReadOrderByReceivedDateDesc(employeeId, read);
    }

    public List<RegistryBook> findByEmployeeIdOrderByReceivedDateDesc(UUID employeeId) {
        return registryBookRepository.findByEmployeeIdOrderByReceivedDateDesc(employeeId);
    }

    public List<RegistryBook> orderByReceivedDateDesc() {
        return registryBookRepository.findByOrderByReceivedDateDesc();
    }
    public long countByEmployeeIdAndRead(UUID employeeId, boolean read) {
        return registryBookRepository.countByEmployeeIdAndRead(employeeId, read);
    }
}

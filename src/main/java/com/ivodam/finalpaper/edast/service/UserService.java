package com.ivodam.finalpaper.edast.service;

import com.ivodam.finalpaper.edast.dto.UserDto;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.helpers.PasswordHandler;
import com.ivodam.finalpaper.edast.mappers.UserMapper;
import com.ivodam.finalpaper.edast.repository.UserRepository;
import com.ivodam.finalpaper.edast.security.SecurityConfiguration;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private SecurityConfiguration configuration;
    private UserMapper userMapper;
    private PasswordHandler passwordHandler;


    public void updatePassword(User user, String password) {
        user.setPassword(configuration.passwordEncoder().encode(password));
        userRepository.save(user);
    }

    public User create(User user) throws AppException {
//        if(!passwordHandler.isValid(user.getPassword()))
//            throw new AppException("Password is not valid", HttpStatus.BAD_REQUEST);
        user.setPassword(configuration.passwordEncoder().encode(user.getPassword()));
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
        user.setJoinDate(LocalDate.now().format(dateFormat));
        return userRepository.save(user);

    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findAllUsers() {
        return userRepository.findAllByRole(Enums.Roles.ROLE_USER);
    }

    public List<User> findAllEmployee() {
        return userRepository.findAllByRole(Enums.Roles.ROLE_EMPLOYEE);
    }

    public User findById(UUID id) throws AppException {
            return userRepository.findById(id)
                    .orElseThrow(() -> new AppException("User with id: " + id + " not found", HttpStatus.NOT_FOUND));
        }

    public User findByEmail(String email) throws AppException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("User with email:" + email + " not found", HttpStatus.NOT_FOUND));
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }


    public User updateRole(User user) {
        return userRepository.save(user);
    }


    public void updateUserAccount(UserDto userDto) throws AppException {
        var user = findById(userDto.getId());
        userDto.setPassword(user.getPassword());
        userDto.setJoinDate(user.getJoinDate());
        userDto.setRole(user.getRole());
        userDto.setPersonalIdentificationNumber(user.getPersonalIdentificationNumber());
        var updatedUser = userMapper.userDtoToUser(userDto);
        userRepository.save(updatedUser);
    }

    public List<User> findAllByNameContainingIgnoreCase(String name) {
        return userRepository.findAllByNameContainingIgnoreCase(name);
    }

    public List<User> findAllByRoleAndJobTitle(String role, String jobTitle) {
        return userRepository.findAllByRoleAndJobTitle(Enums.Roles.valueOf(role), jobTitle);
    }

}

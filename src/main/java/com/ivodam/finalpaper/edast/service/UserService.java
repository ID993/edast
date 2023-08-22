package com.ivodam.finalpaper.edast.service;

import com.ivodam.finalpaper.edast.dto.UserDto;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.utility.PasswordHandler;
import com.ivodam.finalpaper.edast.mappers.UserMapper;
import com.ivodam.finalpaper.edast.repository.UserRepository;
import com.ivodam.finalpaper.edast.security.SecurityConfiguration;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Transactional
    public User create(UserDto userDto) {
        var user = userMapper.userDtoToUser(userDto);
        user.setPassword(configuration.passwordEncoder().encode(user.getPassword()));
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
        user.setJoinDate(LocalDate.now().format(dateFormat));
        return userRepository.save(user);

    }

    @Transactional
    public User create(User user) {
        user.setPassword(configuration.passwordEncoder().encode(user.getPassword()));
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
        user.setJoinDate(LocalDate.now().format(dateFormat));
        return userRepository.save(user);
    }


    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    public Page<User> findAllByRole(Enums.Roles role, Pageable pageable) {
        return userRepository.findAllByRole(role, pageable);
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
        //userDto.setPersonalIdentificationNumber(user.getPersonalIdentificationNumber());
        var updatedUser = userMapper.userDtoToUser(userDto);
        System.out.println(updatedUser);
        userRepository.save(updatedUser);
    }

    public Page<User> findAllByEmailOrNameOrJobTitleContainingIgnoreCase(String search, Pageable pageable) {
        return userRepository.findAllByEmailOrNameOrJobTitleContainingIgnoreCase(search, pageable);
    }

    public List<User> findAllByRoleAndJobTitle(String role, String jobTitle) {
        return userRepository.findAllByRoleAndJobTitle(Enums.Roles.valueOf(role), jobTitle);
    }

    public long count() {
        return userRepository.count();
    }

    public List<Object[]> countByRole() {
        return userRepository.getTotalUsersByRole();
    }

    public String isUserLegit(UserDto user) {
        if (existsByEmail(user.getEmail())) {
            return "Email already exists";
        } else if (!passwordHandler.isValid(user.getPassword())) {
            return "Password is not valid";
        } else if (!passwordHandler.checkConfirmPassword(user.getPassword(), user.getConfirmPassword())) {
            return "Passwords do not match";
        } else if (!user.getCaptcha().equals(user.getHiddenCaptcha())) {
            return "Captcha is not valid";
        } else {
            return "Success";
        }
    }
}

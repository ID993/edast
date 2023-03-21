package com.ivodam.finalpaper.edast.service;

import com.ivodam.finalpaper.edast.dto.UserDto;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.mappers.UserMapper;
import com.ivodam.finalpaper.edast.repository.UserRepository;
import com.ivodam.finalpaper.edast.security.SecurityConfiguration;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private SecurityConfiguration configuration;
    private UserMapper userMapper;


    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public boolean isValid(final String password) {
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public User create(User user) throws AppException {
        if(!isValid(user.getPassword()))
            throw new AppException("Password is not valid", HttpStatus.BAD_REQUEST);
        user.setPassword(configuration.passwordEncoder().encode(user.getPassword()));
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
        user.setJoinDate(LocalDate.now().format(dateFormat));
        return userRepository.save(user);

    }

    public User update(User user) {
        return userRepository.save(user);
    }

    public void updateUserAccount(UserDto userDto) throws AppException {
        var user = findById(userDto.getId());
        if(!Objects.equals(userDto.getPassword(), ""))
            user.setPassword(configuration.passwordEncoder().encode(userDto.getPassword()));
        userDto.setPassword(user.getPassword());
        userDto.setJoinDate(user.getJoinDate());
        userDto.setRole(user.getRole());
        userDto.setPersonalIdentificationNumber(user.getPersonalIdentificationNumber());
        user = userMapper.userDtoToUser(userDto);
        userRepository.save(user);
    }


    public boolean checkIfValidOldPassword(User user, String oldPassword){
        return configuration.passwordEncoder().matches(oldPassword, user.getPassword());
    }

    public void updatePassword(User user, String password) {
        user.setPassword(configuration.passwordEncoder().encode(password));
        userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(long id) throws AppException {
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

    public void deleteById(long id) {
        userRepository.deleteById(id);
    }






}

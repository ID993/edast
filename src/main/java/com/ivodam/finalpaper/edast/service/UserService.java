package com.ivodam.finalpaper.edast.service;

import com.ivodam.finalpaper.edast.dto.UserDto;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.mappers.UserMapper;
import com.ivodam.finalpaper.edast.repository.UserRepository;
import com.ivodam.finalpaper.edast.utility.PasswordHandler;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

  private UserRepository userRepository;
  private PasswordEncoder passwordEncoder;
  private UserMapper userMapper;
  private PasswordHandler passwordHandler;

  public void updatePassword(User user, String password) {
    user.setPassword(passwordEncoder.encode(password));
    userRepository.save(user);
  }

  @Transactional
  public User registerUser(UserDto userDto) {
    return createNewUser(userDto, Enums.Roles.ROLE_USER, null);
  }

  @Transactional
  public User createStaffUser(UserDto userDto, Enums.Roles role,
                              String jobTitle) throws AppException {

    if (role != Enums.Roles.ROLE_ADMIN && role != Enums.Roles.ROLE_EMPLOYEE) {
      throw new AppException("Invalid staff role", HttpStatus.BAD_REQUEST);
    }

    return createNewUser(userDto, role, jobTitle);
  }

  private User createNewUser(UserDto userDto, Enums.Roles role,
                             String jobTitle) {

    var user = new User();
    user.setName(userDto.getName());
    user.setEmail(userDto.getEmail());
    user.setPassword(passwordEncoder.encode(userDto.getPassword()));
    user.setJoinDate(
        LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")));
    user.setRole(role);
    user.setJobTitle(jobTitle);

    return userRepository.save(user);
  }

  public Page<User> findAll(Pageable pageable) {
    return userRepository.findAll(pageable);
  }
  public Page<User> findAllByRole(Enums.Roles role, Pageable pageable) {
    return userRepository.findAllByRole(role, pageable);
  }

  public User findById(UUID id) throws AppException {
    return userRepository.findById(id).orElseThrow(
        ()
            -> new AppException("User with id: " + id + " not found",
                                HttpStatus.NOT_FOUND));
  }

  public User findByEmail(String email) throws AppException {
    return userRepository.findByEmail(email).orElseThrow(
        ()
            -> new AppException("User with email:" + email + " not found",
                                HttpStatus.NOT_FOUND));
  }

  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  public void deleteById(UUID id) { userRepository.deleteById(id); }

  public void updateRole(User user) { userRepository.save(user); }

  @Transactional
  public void updateUserAccount(UUID id, UserDto userDto) throws AppException {

    var user = findById(id);
    user.setName(userDto.getName());
    user.setEmail(userDto.getEmail());
    user.setJobTitle(userDto.getJobTitle());
    userRepository.save(user);
  }

  @Transactional
  public void updateOwnAccount(String authenticatedEmail, UserDto userDto)
      throws AppException {

    var user = findByEmail(authenticatedEmail);
    user.setName(userDto.getName());

    if (!Enums.Roles.ROLE_EMPLOYEE.equals(user.getRole())) {
      user.setJobTitle(userDto.getJobTitle());
    }

    userRepository.save(user);
  }

  public Page<User>
  findAllByEmailOrNameOrJobTitleContainingIgnoreCase(String search,
                                                     Pageable pageable) {
    return userRepository.findAllByEmailOrNameOrJobTitleContainingIgnoreCase(
        search, pageable);
  }

  public List<User> findAllByRoleAndJobTitle(String role, String jobTitle) {
    return userRepository.findAllByRoleAndJobTitle(Enums.Roles.valueOf(role),
                                                   jobTitle);
  }

  public long count() { return userRepository.count(); }

  public List<Object[]> countByRole() {
    return userRepository.getTotalUsersByRole();
  }

  public String isUserLegit(UserDto user) {
    if (existsByEmail(user.getEmail())) {
      return "Email already exists";
    } else if (!passwordHandler.isValid(user.getPassword())) {
      return "Password is not valid";
    } else if (!passwordHandler.checkConfirmPassword(
                   user.getPassword(), user.getConfirmPassword())) {
      return "Passwords do not match";
    } else if (!user.getCaptcha().equals(user.getHiddenCaptcha())) {
      return "Captcha is not valid";
    } else {
      return "Success";
    }
  }
}

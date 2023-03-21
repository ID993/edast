package com.ivodam.finalpaper.edast.controller;

import com.ivodam.finalpaper.edast.dto.UserDto;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.mappers.UserMapper;
import com.ivodam.finalpaper.edast.service.UserService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
public class UserController {

    private UserService userService;

    private UserMapper userMapper;

    @PostMapping("/users")
    public User create(@RequestBody User user) throws AppException {
        return userService.create(user);
    }

    @PostMapping("/users/add-role")
    public User update(@RequestBody UserDto userDto) {
        var user = userMapper.userDtoToUser(userDto);
        return userService.update(user);
    }


    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> findAllRest() {
        var users = userService.findAll();
        var userDto = users.stream()
                .map(userMapper::userToUserDto)
                .toList();
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/users/{id}")
    public UserDto findById(@PathVariable long id) throws AppException {
        var user = userService.findById(id);
        return userMapper.userToUserDto(user);
    }

    @GetMapping("/users/email/{email}")
    public User findByEmail(@PathVariable String email) throws AppException {
        return userService.findByEmail(email);
    }


    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(@PathVariable long id) {
        userService.deleteById(id);
    }











}

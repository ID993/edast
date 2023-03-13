package com.ivodam.finalpaper.edast.controller;

import com.ivodam.finalpaper.edast.dto.UserDto;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.mappers.UserMapper;
import com.ivodam.finalpaper.edast.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@AllArgsConstructor
public class UserController {

    private UserService userService;

    private UserMapper userMapper;

    @PostMapping("/users")
    public User create(@RequestBody User user) {
        return userService.create(user);
    }


    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<UserDto>> findAllRest() {
        var users = userService.findAll();
        var userDto = users.stream()
                .map(userMapper::userToUserDto)
                .toList();
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/users/{id}")
    public Optional<User> findById(@PathVariable long id) {
        return userService.findById(id);
    }

    @GetMapping("/users/email/{email}")
    public Optional<User> findByEmail(@PathVariable String email) {
        return userService.findByEmail(email);
    }

    @PostMapping("/users/{userId}/roles")
    public String updateRole(@PathVariable long userId, @RequestParam String role) {

        var user = userService.findById(userId);
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (role.equals("admin")) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else if (role.equals("employee")) {
            authorities.add(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
        } else {
            // handle invalid role
            // return error page or redirect to user management page
            return "redirect:/users";
        }
        user.get().setAuthorities(authorities);

        userService.update(user.get());
        return "redirect:/users";
    }

    @DeleteMapping("/users/{id}")
    public void deleteById(@PathVariable long id) {
        userService.deleteById(id);
    }

    @PutMapping("/users/update/{id}")
    public User update(@PathVariable long id) {
        var user = userService.findById(id);
        return userService.update(user.get());
    }

//    @PatchMapping("/users/update/{id}")
//    public User patch(@RequestBody User user) {
//        return userService.update(user);
//    }


}

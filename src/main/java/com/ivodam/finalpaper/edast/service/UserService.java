package com.ivodam.finalpaper.edast.service;

import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.repository.UserRepository;
import com.ivodam.finalpaper.edast.security.SecurityConfiguration;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private SecurityConfiguration configuration;



    public User create(User user) {
        user.setPassword(configuration.passwordEncoder().encode(user.getPassword()));
        user.setJoinDate(LocalDate.now());
        return userRepository.save(user);
    }

    public User update(User user) {
        return userRepository.save(user);
    }


    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void deleteById(long id) {
        userRepository.deleteById(id);
    }


    public void addRole(String username, String role) {
        var user = findByEmail(username);
        if (user.isPresent()) {
            var authorities = (Set<GrantedAuthority>) user.get().getAuthorities();
            authorities.add(new SimpleGrantedAuthority(role));
            user.get().setAuthorities(authorities);
            update(user.get());
        }
    }

    public void removeRole(String username, String role) {
        var user = findByEmail(username);
        if (user.isPresent()) {
            var authorities = (Set<GrantedAuthority>) user.get().getAuthorities();
            authorities.remove(new SimpleGrantedAuthority(role));
            user.get().setAuthorities(authorities);
            update(user.get());
        }
    }

}

package com.ivodam.finalpaper.edast.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "users")
public class User implements UserDetails{

    @Id
    @SequenceGenerator(name = "users_user_id_seq", sequenceName = "users_user_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_user_id_seq")
    @Column(name = "user_id", updatable = false)
    private long id;

    private String name;

    private String email;

    private String password;

    private String profession;

    private String address;

    private String telephone;

    private String numberOfIdCard;

    private String purposeOfResearch;

    private LocalDate joinDate;

    private String role;

    @Transient
    private Set<GrantedAuthority> authorities;



    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = new HashSet<>(authorities);
        this.role = StringUtils.collectionToCommaDelimitedString(
                authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authorities == null) {
            authorities = new HashSet<>();
            if (role != null) {
                var authorityStrings = role.split(",");
                for (String authorityString : authorityStrings) {
                    authorities.add(new SimpleGrantedAuthority(authorityString));
                }
            }
        }
        return authorities;
    }


    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

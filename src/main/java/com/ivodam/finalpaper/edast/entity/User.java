package com.ivodam.finalpaper.edast.entity;

import com.ivodam.finalpaper.edast.enums.Enums;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "users")
public class User implements UserDetails{

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "user_id", columnDefinition = "uuid", updatable = false)
    private UUID id;

    @Size(min = 2, message = "Name must be at least 2 characters long")
    private String name;

    @Email(message = "Enter a valid email address")
    @NotNull(message = "Email is required")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "Password must contain at least one uppercase letter, one lowercase letter and one number")
    @NotNull(message = "Password is required")
    private String password;

    private String address;

    private String joinDate;

    private Enums.Roles role;

    @Column(name = "pin")
    @Size(min = 11, max = 11, message = "Personal Identification Number must be 11 digits long")
    @NotNull(message = "Personal Identification Number is required")
    private String personalIdentificationNumber;

    @Size(min = 9, message = "Mobile number must be at least 9 characters long")
    private String mobile;

    private String jobTitle;

    @Transient
    private Set<GrantedAuthority> authorities;

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = new HashSet<>(authorities);
        this.role = Enums.Roles.valueOf(StringUtils.collectionToCommaDelimitedString(
                authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authorities == null && role != null) {
            authorities = new HashSet<>();
            var authorityString = role.toString();
            authorities.add(new SimpleGrantedAuthority(authorityString));
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

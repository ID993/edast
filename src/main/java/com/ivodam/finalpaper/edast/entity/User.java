package com.ivodam.finalpaper.edast.entity;

import com.ivodam.finalpaper.edast.enums.Enums;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Conditional;
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
    @NotEmpty(message = "Email is required")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "Password must contain at least one uppercase letter, one lowercase letter and one number")
    private String password;

    private String joinDate;

    private Enums.Roles role;

    private String jobTitle;

    @Transient
    private Set<GrantedAuthority> authorities;

    @Transient
    private String captcha;

    @Transient
    private String hiddenCaptcha;

    @Transient
    private String realCaptcha;

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

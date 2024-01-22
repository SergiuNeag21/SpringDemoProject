package socialnetwork.domain;


import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data   // Generates getter, setter, toString, equals, and hashCode methods
@AllArgsConstructor // Generates a constructor with all fields
@NoArgsConstructor
@Entity // it is treated as a JPA entity, and its instances can be managed by an entity manager.
@Builder
@Table(name = "app_user", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Email
    @NotEmpty(message = "Email cannot be empty")
    @Size(min = 4, max =  20, message = "Email should have between 4 and 15 characters")
    private String email;


    @NotEmpty(message = "Password cannot be empty")
    // @Size(min = 4, max = 20, message = "Password should have between 4 and 15 characters")
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;


    // essentially provides the roles/authorities associated with the user.
    // a user logs in and tries to access a particular method or endpoint,
    // Spring Security checks the roles associated with that user, and access is granted or denied based on the roles
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
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

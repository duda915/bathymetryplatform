package com.mdud.bathymetryplatform.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mdud.bathymetryplatform.user.authority.Authority;
import com.mdud.bathymetryplatform.user.userauthority.UserAuthority;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.Set;

@Data
@ToString(exclude = "password")
@Entity @NoArgsConstructor
@Table(name = "application_user")
public class ApplicationUser {
    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name")
    private String username;

    @Column(name = "pass_hash")
    @JsonIgnore
    private String password;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private Set<UserAuthority> userAuthorities;

    public ApplicationUser(String username, String password, Set<UserAuthority> userAuthorities) {
        this.username = username;
        this.setPassword(password);
        this.userAuthorities = userAuthorities;
    }

    public void setPassword(String password) {
        this.password = PASSWORD_ENCODER.encode(password);
    }

    public boolean checkRole(Authority authority) {
        for(UserAuthority userAuthority : userAuthorities) {
            if(userAuthority.getAuthority().getAuthorityName() == authority.getAuthorityName()) {
                return true;
            }
        }
        return false;
    }

}


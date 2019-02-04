package com.mdud.bathymetryplatform.user.registration;

import com.mdud.bathymetryplatform.user.ApplicationUser;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Table(name = "registration_token")
@NoArgsConstructor
public class RegistrationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private ApplicationUser applicationUser;

    @Column(name = "token")
    private String token = UUID.randomUUID().toString();

    @Column(name = "expiration_date")
    private Date expirationDate = setTommorowDate();

    private Date setTommorowDate() {
        LocalDate tommorow = LocalDate.now().plusDays(1);
        return Date.valueOf(tommorow);
    }

    public RegistrationToken(ApplicationUser applicationUser) {
        this.applicationUser = applicationUser;
    }
}


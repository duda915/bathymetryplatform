package com.mdud.bathymetryplatform.user.registration;

import com.mdud.bathymetryplatform.user.ApplicationUser;
import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Table(name = "registration_token")
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
        long unix = LocalDate.now().plusDays(1).toEpochDay();
        return new Date(unix);
    }

    public RegistrationToken(ApplicationUser applicationUser) {
        this.applicationUser = applicationUser;
    }
}


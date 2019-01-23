package com.mdud.bathymetryplatform.user;

import com.mdud.bathymetryplatform.user.authority.Authority;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data @NoArgsConstructor
@Entity
@Table (name = "user_authorities")
public class UserAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "authority_id")
    private Authority authority;

    public UserAuthority(Authority authority) {
        this.authority = authority;
    }
}

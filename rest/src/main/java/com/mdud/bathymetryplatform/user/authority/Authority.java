package com.mdud.bathymetryplatform.user.authority;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data @NoArgsConstructor
@Entity
@Table(name = "authority")
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "authority_name")
    private Authorities authorityName;

    public Authority(Authorities authorityName) {
        this.authorityName = authorityName;
    }
}


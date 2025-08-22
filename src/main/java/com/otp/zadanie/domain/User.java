package com.otp.zadanie.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "uuid", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(name = "fio", nullable = false, length = 255)
    private String fio;

    @Column(name = "phone_number", nullable = false, length = 32)
    private String phoneNumber;

    @Column(name = "avatar", nullable = false, length = 1024)
    private String avatar;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_uuid", nullable = false, foreignKey = @ForeignKey(name = "fk_users_roles"))
    private Role role;
}

package com.example.tounip.tounip.membership.infrastructure.persistence.entity;

import com.example.tounip.tounip.membership.application.model.MembershipRole;
import com.example.tounip.tounip.space.infrastructure.persistence.entity.SpaceEntity;
import com.example.tounip.tounip.user.infrastructure.persistence.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
        name = "membership",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_membership_user_space",
                        columnNames = {"user_id", "space_id"}
                )
        }
)
public class MembershipEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    private SpaceEntity space;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private MembershipRole role;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    public void prePersist() {
        if (role == null) {
            role = MembershipRole.MEMBER;
        }

        if (joinedAt == null) {
            joinedAt = LocalDateTime.now();
        }
    }
}

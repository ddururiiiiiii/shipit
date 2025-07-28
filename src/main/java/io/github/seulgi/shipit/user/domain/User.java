package io.github.seulgi.shipit.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private TeamType team;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    protected User(String username, String email, String password,
                   TeamType team, RoleType role, UserStatus status, LocalDateTime createdAt) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.team = team;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static User create(String username, String email, String password, TeamType team, RoleType role) {
        return new User(username, email, password, team, role, UserStatus.ACTIVE, LocalDateTime.now());
    }

    public void changeUsername(String username) {
        this.username = username;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void changeTeam(TeamType team) {
        this.team = team;
    }

    public void changeRole(RoleType role) {
        this.role = role;
    }

    public void withdraw() {
        this.status = UserStatus.WITHDRAWN;
    }
}

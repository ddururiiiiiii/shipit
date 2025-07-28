package io.github.seulgi.shipit.user.repository;

import io.github.seulgi.shipit.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /*이메일 중복 확인*/
    boolean existsByEmail(String email);

    /*이메일로 찾기*/
    Optional<User> findByEmail(String email);

}
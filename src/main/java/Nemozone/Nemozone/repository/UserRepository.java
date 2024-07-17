package Nemozone.Nemozone.repository;

import Nemozone.Nemozone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByKakaoId(Long kakaoId);
    Optional<User> findUserByRelationConnectId(Long relationConnectId);
}

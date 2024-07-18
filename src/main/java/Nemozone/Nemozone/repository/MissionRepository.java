package Nemozone.Nemozone.repository;

import Nemozone.Nemozone.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MissionRepository extends JpaRepository<Mission, Long> {

    Optional<Mission> findMissionByOrder(Long order);
}

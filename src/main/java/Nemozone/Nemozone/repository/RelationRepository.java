package Nemozone.Nemozone.repository;

import Nemozone.Nemozone.entity.Relation;
import Nemozone.Nemozone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RelationRepository extends JpaRepository<Relation, Long> {

    Optional<Relation> findRelationByUsersContains(User user);
}

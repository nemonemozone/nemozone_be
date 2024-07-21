package Nemozone.Nemozone.repository;

import Nemozone.Nemozone.entity.Photo;
import Nemozone.Nemozone.entity.Relation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    public List<Photo> findPhotoByRelation(Relation r);
    public Optional<Photo> findPhotoById(Long id);
}

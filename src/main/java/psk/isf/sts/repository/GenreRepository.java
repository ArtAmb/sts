package psk.isf.sts.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import psk.isf.sts.entity.Genre;

@Repository
public interface GenreRepository extends CrudRepository<Genre, Long> {

}

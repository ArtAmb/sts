package psk.isf.sts.repository.serial;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import psk.isf.sts.entity.serial.Genre;

@Repository
public interface GenreRepository extends CrudRepository<Genre, Long> {

	Collection<Genre> findByName(String name);

}

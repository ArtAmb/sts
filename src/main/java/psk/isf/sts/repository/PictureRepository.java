package psk.isf.sts.repository;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import psk.isf.sts.entity.Picture;

@Repository
public interface PictureRepository extends CrudRepository<Picture, Long> {

	Collection<Picture> findByName(String name);
}

package psk.isf.sts.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import psk.isf.sts.entity.Picture;

@Repository
public interface PictureRepository extends CrudRepository<Picture, Long> {

}

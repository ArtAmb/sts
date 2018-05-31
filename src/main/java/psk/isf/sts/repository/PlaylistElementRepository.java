package psk.isf.sts.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import psk.isf.sts.entity.PlaylistElement;

@Repository
public interface PlaylistElementRepository extends CrudRepository<PlaylistElement, Long> {

}

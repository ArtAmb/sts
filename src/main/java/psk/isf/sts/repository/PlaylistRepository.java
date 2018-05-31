package psk.isf.sts.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import psk.isf.sts.entity.Playlist;

@Repository
public interface PlaylistRepository extends CrudRepository<Playlist, Long> {

}

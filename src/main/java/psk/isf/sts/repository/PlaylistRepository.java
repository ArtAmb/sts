package psk.isf.sts.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import psk.isf.sts.entity.Playlist;
import psk.isf.sts.entity.registration.User;

@Repository
public interface PlaylistRepository extends CrudRepository<Playlist, Long> {

	List<Playlist> findByUser(User user);

	Collection<Playlist> findAll();

}

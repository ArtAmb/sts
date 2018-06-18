package psk.isf.sts.repository.authorization;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import psk.isf.sts.entity.registration.User;
import psk.isf.sts.entity.registration.UserSourceSystem;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

	boolean existsByLogin(String login);

	boolean existsByEmail(String email);

	User findByLogin(String login);

	User findByExtIdAndSourceSystem(String extId, UserSourceSystem system);

	Collection<User> findAll();

}

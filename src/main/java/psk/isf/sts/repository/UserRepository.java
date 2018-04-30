package psk.isf.sts.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import psk.isf.sts.entity.registration.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

	boolean existsByLogin(String login);

	boolean existsByEmail(String email);

}

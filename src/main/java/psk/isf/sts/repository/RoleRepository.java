package psk.isf.sts.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import psk.isf.sts.entity.registration.Role;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {

}

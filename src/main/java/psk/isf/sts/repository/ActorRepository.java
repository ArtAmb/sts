package psk.isf.sts.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import psk.isf.sts.entity.Actor;

@Repository
public interface ActorRepository extends CrudRepository<Actor, Long> {

}

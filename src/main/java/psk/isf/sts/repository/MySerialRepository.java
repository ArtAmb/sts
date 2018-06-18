package psk.isf.sts.repository;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import psk.isf.sts.entity.myserial.MySerial;
import psk.isf.sts.entity.registration.User;

@Repository
public interface MySerialRepository extends CrudRepository<MySerial, Long> {
	Collection<MySerial> findByUser(User user);
}

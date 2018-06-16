package psk.isf.sts.repository;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import psk.isf.sts.entity.MySerialConfig;
import psk.isf.sts.entity.registration.User;

@Repository
public interface MySerialConfigRepository extends CrudRepository<MySerialConfig, Long> {

	Collection<MySerialConfig> findByUser(User user);

	Collection<MySerialConfig> findByUserAndSendNotificationsTrue(User user);
}

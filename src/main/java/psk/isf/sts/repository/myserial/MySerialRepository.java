package psk.isf.sts.repository.myserial;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import psk.isf.sts.entity.myserial.MySerial;
import psk.isf.sts.entity.registration.User;

@Repository
public interface MySerialRepository extends JpaRepository<MySerial, Long> {
	Collection<MySerial> findByUser(User user);

	// @Query("delete from MySerial tmp where tmp.= ?1")
	// void deleteByConfig_id(Long configId);
}

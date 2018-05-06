package psk.isf.sts.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import psk.isf.sts.entity.MySerial;

@Repository
public interface MySerialRepository extends CrudRepository<MySerial, Long> {

}

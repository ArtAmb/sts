package psk.isf.sts.repository.serial;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import psk.isf.sts.entity.serial.SerialElement;
import psk.isf.sts.entity.serial.SerialElementType;

@Repository
public interface SerialRepository extends CrudRepository<SerialElement, Long> {

	Collection<SerialElement> findByParentAndElementType(SerialElement parent, SerialElementType type);
}

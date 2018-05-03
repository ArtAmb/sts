package psk.isf.sts.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import psk.isf.sts.entity.SerialElement;

@Repository
public interface SerialRepository extends CrudRepository<SerialElement, Long> {

}

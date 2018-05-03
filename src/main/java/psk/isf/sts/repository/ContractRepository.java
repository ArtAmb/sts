package psk.isf.sts.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import psk.isf.sts.entity.Contract;

@Repository
public interface ContractRepository extends CrudRepository<Contract, Long> {

}

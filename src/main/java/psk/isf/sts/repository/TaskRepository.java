package psk.isf.sts.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import psk.isf.sts.entity.Task;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {

}